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
        private val invoiceService: InvoiceService,
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
            invoiceService.updateStatus(invoice.id, InvoiceStatus.PAID)
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
                handleNoMoney(invoice.id)
                return false
            }
        } catch (exception: Exception) {
            handleException(exception, invoice.id)
            return false
        }
        return true
    }

    private fun handleException(exception: Exception, id: Int) {
        when (exception) {
            is MultipleNetworkException -> handleFailedRetry(id)
            is EntityNotFoundException -> handleNoCustomerFound(id)
            is CurrencyMismatchException -> handleCurrencyMismatch(id)
        }
    }

    private fun handleFailedRetry(id: Int) {
        Logger.log.info { "failed to proceed - retry number exceeded" }
        invoiceService.updateStatus(id, InvoiceStatus.FAILED)
        //TODO: here we could not execute despite several tries - pass to external service to check/reschedule
    }

    private fun handleCurrencyMismatch(id: Int) {
        Logger.log.info { "Invoice $id has currency mismatch. Fallback - cancel > evaluate > prepare new" }
        val wrongInvoice = invoiceService.updateStatus(id, InvoiceStatus.FAILED)
        val correctedInvoice= invoiceCorrector.getCorrectCopy(wrongInvoice)
        if (correctedInvoice.isPresent){
            proceedSingleInvoice(correctedInvoice.get())
        }
        //TODO: here we could not execute despite several tries - pass to external service to check
    }

    private fun handleNoCustomerFound(id: Int) {
        Logger.log.info { "Invoice $id not proceeded, such customer was not found in DB" }
        invoiceService.updateStatus(id, InvoiceStatus.FAILED)
        //TODO: invoice is invalid, customer does not exist, no way to find him - call external service to proceed with the situation
    }

    private fun handleNoMoney(id: Int) {
        Logger.log.info { "Not managed to charge invoice $id , customer account balance did not allow the charge" }
        invoiceService.updateStatus(id, InvoiceStatus.CUSTOMER_HAD_NO_MONEY)
        //TODO: external service can suspend subscription and/or send email to customer - call external service
    }
}

