package io.pleo.antaeus.core.services

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
        val convertedAmount = currencyConverter.convert(from = invoice.amount.currency, to = customer.currency, amount = invoice.amount.value)
        return if (convertedAmount.isPresent) {
            val correctMoney = convertedAmount.get()
            Optional.of(invoiceService.create(amount = correctMoney, customer = customer))
        } else Optional.empty()
    }
}


