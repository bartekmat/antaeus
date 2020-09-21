package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.commands.RetryableCommand
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.handlers.BillingExceptionHandler
import io.pleo.antaeus.core.logger.Logger
import io.pleo.antaeus.models.Invoice
import java.util.function.Supplier

class BillingService(
        private val paymentProvider: PaymentProvider,
        private val invoiceService: InvoiceService,
        private val handler: BillingExceptionHandler
) {
    // TODO - Add code e.g. here
    fun proceedAllPendingInvoices() {
        val fetched = invoiceService.fetchPendingInvoices()
        println(fetched.size)
        fetched.forEach { proceedSingleInvoice(it) }
    }

    fun proceedSingleInvoice(invoice: Invoice): Boolean {
        val charged: Boolean = chargeInvoice(invoice)
        if (charged) {
            invoiceService.markAsPaid(invoice.id)
            Logger.log.info { "Invoice " + invoice.id + " successfully charged" }
        }
        return charged
    }

    private fun chargeInvoice(invoice: Invoice): Boolean {
        try {
            val success = RetryableCommand<Boolean>(maxRetries = 3)
                    .run(Supplier { paymentProvider.charge(invoice) })
            if (!success) {
                handler.handleNoMoney(invoice.id)
                return false
            }
        } catch (exception: Exception) {
            handler.handleException(exception,invoice.id)
            return false
        }
        return true
    }
}
