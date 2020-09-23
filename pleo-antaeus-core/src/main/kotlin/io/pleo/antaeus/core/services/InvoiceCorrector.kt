package io.pleo.antaeus.core.services

import io.pleo.antaeus.models.Customer
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.Money
import java.util.*

class InvoiceCorrector(
        private val customerService: CustomerService,
        private val invoiceService: InvoiceService,
        private val currencyConverter: CurrencyConverter
) {
    fun getCorrectCopy(invoice: Invoice): Optional<Invoice> {

        val customer = customerService.fetch(invoice.customerId)
        return if (customer.isPresent) {
            getCorrectedInvoice(invoice, customer.get())
        } else Optional.empty()
    }

    private fun getCorrectedInvoice(invoice: Invoice, customer: Customer): Optional<Invoice> {
        val convertedAmount = currencyConverter.convert(from = invoice.amount.currency, to = customer.currency, amount = invoice.amount.value)
        return if (convertedAmount.isPresent) {
            createFixedInvoice(convertedAmount.get(), customer)
        } else Optional.empty()
    }

    private fun createFixedInvoice(convertedAmount: Money, customer: Customer): Optional<Invoice> {
        val createdInvoice = invoiceService.create(amount = convertedAmount, customer = customer)
        return if (createdInvoice.isPresent) Optional.of(createdInvoice.get()) else Optional.empty()
    }
}


