/*
    Implements endpoints related to invoices.
 */

package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import java.util.*

class InvoiceService(private val dal: AntaeusDal) {
    fun fetchAll(): List<Invoice> {
        return dal.fetchInvoices()
    }

    fun fetch(id: Int): Invoice {
        return dal.fetchInvoice(id) ?: throw InvoiceNotFoundException(id)
    }

    fun fetchPendingInvoices(): List<Invoice> {
        val invoicesByStatus = dal.fetchInvoicesByStatus(InvoiceStatus.PENDING)
        println("FETCHED " + invoicesByStatus.size + " invoices")
        return invoicesByStatus
    }

    fun create(amount: Money, customer: Customer): Optional<Invoice> {
        val createdInvoice = dal.createInvoice(amount, customer)
        return if (createdInvoice != null) Optional.of(createdInvoice) else Optional.empty()
    }

    fun updateStatus(id: Int, desiredStatus: InvoiceStatus): Invoice {
        return dal.updateInvoice(id, desiredStatus) ?: throw InvoiceNotFoundException(id)
    }
}
