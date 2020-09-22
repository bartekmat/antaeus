/*
    Implements endpoints related to invoices.
 */

package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money

class InvoiceService(private val dal: AntaeusDal) {
    fun fetchAll(): List<Invoice> {
        return dal.fetchInvoices()
    }

    fun fetch(id: Int): Invoice {
        return dal.fetchInvoice(id) ?: throw InvoiceNotFoundException(id)
    }

    fun fetchPendingInvoices(): List<Invoice> {
        val invoicesByStatus = dal.fetchInvoicesByStatus(InvoiceStatus.PENDING)
        println("FETCHED "+invoicesByStatus.size+" invoices")
        return invoicesByStatus
    }

    fun create(amount: Money, customer: Customer): Invoice {
        return dal.createInvoice(amount, customer) ?: throw CustomerNotFoundException(customer.id)
    }

    fun updateStatus(id: Int, desiredStatus: InvoiceStatus): Invoice {
        return dal.updateInvoice(id, desiredStatus) ?: throw InvoiceNotFoundException(id)
    }
}
