package io.pleo.antaeus.core.handlers

import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.Money
import java.util.*

class InvoiceCorrector(
        private val customerService: CustomerService,
        private val invoiceService: InvoiceService,
        private val converter: Converter
) {
    fun getCorrectCopy(invoice: Invoice): Optional<Invoice> {

        val customer = customerService.fetch(invoice.customerId)
        val convertedAmount = converter.convert(invoice.amount.currency, customer.currency, invoice.amount.value)
        return if (convertedAmount.isPresent) {
            val correctMoney = Money(convertedAmount.get(), customer.currency)
            Optional.of(invoiceService.create(amount = correctMoney, customer = customer))
        } else Optional.empty()
    }
}


