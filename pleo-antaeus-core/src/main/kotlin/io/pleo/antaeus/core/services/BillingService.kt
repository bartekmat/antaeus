package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.core.handlers.BillingExceptionHandler
import io.pleo.antaeus.core.logger.Logger
import io.pleo.antaeus.models.Invoice

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
            Logger.log.info { "Invoice "+invoice.id+" successfully charged" }
        }
        return charged
    }

    private fun chargeInvoice(invoice: Invoice): Boolean {
        try {
            val success = paymentProvider.charge(invoice)
            if (!success){
                handleNoMoney(invoice.id)
                return false
            }
        } catch (exception: Exception) {
           handler.handleException(exception)
            return false
        }
        return true
    }

    private fun handleNoMoney(id: Int) {
         //TODO: implement handling of this situation - log and pass to external service
        Logger.log.info { "Not managed to charge invoice $id , customer account balance did not allow the charge" }
    }
}
