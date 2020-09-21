package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.commands.RetryableCommand
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.EntityNotFoundException
import io.pleo.antaeus.core.exceptions.MultipleNetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.logger.Logger
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import java.util.function.Supplier

class BillingService(
        private val paymentProvider: PaymentProvider,
        private val invoiceCorrector: InvoiceCorrector
) {

    fun proceedAllPendingInvoices(invoices: List<Invoice>) {
        invoices.forEach { proceedSingleInvoice(it) }
        //TODO: here instead of iterating and fired they should be iterated and put on the queue
    }

    fun proceedSingleInvoice(invoice: Invoice): Boolean {
        val charged: Boolean = chargeInvoice(invoice)
        if (charged) {
            invoice.status = InvoiceStatus.PAID
            Logger.log.info { "Invoice " + invoice.id + " successfully charged " + invoice.amount.value.toInt() + " " + invoice.amount.currency }
        }
        return charged
    }

    private fun chargeInvoice(invoice: Invoice): Boolean {
        try {
            val success = RetryableCommand<Boolean>(maxRetries = 3)
                    .run(Supplier {
                        paymentProvider.charge(invoice)
                    })
            if (!success) {
                handleNoMoney(invoice)
                return false
            }
        } catch (exception: Exception) {
            handleException(exception, invoice)
            return false
        }
        return true
    }

    private fun handleException(exception: Exception, invoice: Invoice) {
        when (exception) {
            is MultipleNetworkException -> handleFailedRetry(invoice)
            is EntityNotFoundException -> handleNoCustomerFound(invoice)
            is CurrencyMismatchException -> handleCurrencyMismatch(invoice)
        }
    }

    private fun handleFailedRetry(invoice: Invoice) {
        Logger.log.info { "failed to proceed - retry number exceeded" }
        invoice.status = InvoiceStatus.FAILED
        //TODO: here we could not execute despite several tries - pass to external service to check/reschedule
    }

    private fun handleCurrencyMismatch(invoice: Invoice) {
        Logger.log.info { "Invoice ${invoice.id} has currency mismatch. Fallback - cancel > evaluate > prepare new" }
        invoice.status = InvoiceStatus.FAILED
        val correctedInvoice= invoiceCorrector.getCorrectCopy(invoice)
        if (correctedInvoice.isPresent){
            proceedSingleInvoice(correctedInvoice.get())
        }
        //TODO: here we could not execute despite several tries - pass to external service to check
    }

    private fun handleNoCustomerFound(invoice: Invoice) {
        Logger.log.info { "Invoice ${invoice.id} not proceeded, such customer was not found in DB" }
        invoice.status = InvoiceStatus.FAILED
        //TODO: invoice is invalid, customer does not exist, no way to find him - call external service to proceed with the situation
    }

    private fun handleNoMoney(invoice: Invoice) {
        Logger.log.info { "Not managed to charge invoice ${invoice.id} , customer account balance did not allow the charge" }
        invoice.status = InvoiceStatus.CUSTOMER_HAD_NO_MONEY
        //TODO: external service can suspend subscription and/or send email to customer - call external service
    }
}

