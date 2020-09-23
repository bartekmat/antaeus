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
        invoices.stream().limit(10).forEach { proceedSingleInvoice(it) }
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
            val success = RetryableCommand<Boolean>(3)
                    .run(Supplier { paymentProvider.charge(invoice) })
            if (!success) {
                handleNoSufficientBalance(invoice.id)
                return false
            }
        } catch (exception: Exception) {
            handleException(exception, invoice.id)
            return false
        }
        return true
    }

    private fun handleException(exception: Exception, id: Int) {
        Logger.log.info { exception.message }
        when (exception) {
            is CurrencyMismatchException -> handleCurrencyMismatch(id)
            is EntityNotFoundException -> handleWrongCustomerOnInvoice(id)
            is MultipleNetworkException -> handleConnectionToProviderLost(id)
        }
    }

    private fun handleCurrencyMismatch(id: Int) {
        val wrongInvoice = invoiceService.updateStatus(id, InvoiceStatus.FAILED)
        val correctedInvoice = invoiceCorrector.getCorrectCopy(wrongInvoice)
        if (correctedInvoice.isPresent) {
            proceedSingleInvoice(correctedInvoice.get())
        }
    }

    private fun handleWrongCustomerOnInvoice(id: Int) {
        /*
            THIS SHOULD PASS THE INVOICE TO EXTERNAL SERVICE THAT SOLVES THE CASE

            Seems to be unsolvable problem - generate rapport
        */
        invoiceService.updateStatus(id, InvoiceStatus.FAILED)
    }

    private fun handleConnectionToProviderLost(id: Int) {
        /*
            THIS SHOULD PASS THE INVOICE TO EXTERNAL SERVICE THAT SOLVES THE CASE


        */
        invoiceService.updateStatus(id, InvoiceStatus.FAILED)
    }

    private fun handleNoSufficientBalance(id: Int) {
        /*
            THIS SHOULD PASS THE INVOICE TO EXTERNAL SERVICE THAT SOLVES THE CASE

            Could for example suspend the subscription or send some notification to customer or/and accounting department
        */
        Logger.log.info { "Not managed to charge invoice $id , customer account balance did not allow the charge" }
        invoiceService.updateStatus(id, InvoiceStatus.CUSTOMER_HAD_NO_MONEY)
    }
}

