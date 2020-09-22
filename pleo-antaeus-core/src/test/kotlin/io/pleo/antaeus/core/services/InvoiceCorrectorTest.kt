package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.models.*
import io.pleo.antaeus.models.Currency
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class InvoiceCorrectorTest {
    private val existingCustomer = Customer(1, Currency.DKK)
    private val convertedMoney = Money(BigDecimal.TEN, existingCustomer.currency)

    private val invalidInvoice = Invoice(1, 1, Money(BigDecimal.ONE, Currency.DKK), InvoiceStatus.FAILED)
    private val correctedInvoice = Invoice(2, 1, convertedMoney, InvoiceStatus.PENDING)

    @Test
    fun `will return corrected copy`() {
        //mocks
        val converter = mockk<CurrencyConverter> {
            every { convert(any(), any(), any()) } returns Optional.of(convertedMoney)
        }
        val customerService = mockk<CustomerService> {
            every { fetch(any()) } returns existingCustomer
        }
        val invoiceService = mockk<InvoiceService> {
            every { create(convertedMoney, existingCustomer) } returns correctedInvoice
        }
        //mock injection
        val invoiceCorrector = InvoiceCorrector(customerService, invoiceService, converter)

        val createdInvoice = invoiceCorrector.getCorrectCopy(invalidInvoice)

        assertTrue {
            createdInvoice.isPresent
            createdInvoice.get() == correctedInvoice
        }
    }

    @Test
    fun `will return empty optional if converter fails`() {
        //mocks
        val converter = mockk<CurrencyConverter> {
            every { convert(any(), any(), any()) } returns Optional.empty()
        }
        val customerService = mockk<CustomerService> {
            every { fetch(any()) } returns existingCustomer
        }
        val invoiceService = mockk<InvoiceService> {
            every { create(convertedMoney, existingCustomer) } returns correctedInvoice
        }
        //mock injection
        val invoiceCorrector = InvoiceCorrector(customerService, invoiceService, converter)

        val createdInvoice = invoiceCorrector.getCorrectCopy(invalidInvoice)

        assertTrue {
            createdInvoice.isEmpty
        }
    }
}