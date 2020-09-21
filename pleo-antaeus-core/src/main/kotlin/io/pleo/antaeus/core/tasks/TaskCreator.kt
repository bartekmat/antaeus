package io.pleo.antaeus.core.tasks

import io.pleo.antaeus.core.services.BillingService
import io.pleo.antaeus.core.services.InvoiceService
import java.util.function.Supplier

class TaskCreator(
        private val billingService: BillingService,
        private val invoiceService: InvoiceService
) {

    fun createSubscriptionSettlement(): Supplier<Unit> {
        return   Supplier { billingService.proceedAllPendingInvoices(invoiceService.fetchPendingInvoices()) }

    }
}