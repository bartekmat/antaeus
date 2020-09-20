package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Invoice

class BillingService(
        private val paymentProvider: PaymentProvider,
        private val invoiceService: InvoiceService
) {
    // TODO - Add code e.g. here
    fun proceedAllPendingInvoices() {
        invoiceService.fetchPendingInvoices().forEach { proceedSingleInvoice(it) }
    }

    fun proceedSingleInvoice(invoice: Invoice): Boolean {
        val charged: Boolean = paymentProvider.charge(invoice)
        if (charged) {
            invoiceService.markAsPaid(invoice.id)
        } else {
            handleNoMoney()
        }
        return charged
    }

    private fun chargeInvoice(invoice: Invoice): Boolean {
        try {
            return paymentProvider.charge(invoice)
        } catch (exception: Exception) {

        }
        return false
    }

    private fun handleNoMoney() {
         //TODO: implement handling of this situation - log and pass to external service
    }
}
