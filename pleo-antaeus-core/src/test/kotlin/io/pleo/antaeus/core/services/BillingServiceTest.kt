package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BillingServiceTest {
    //testData
    private val noProblemInvoice = Invoice(1, 1, Money(BigDecimal.TEN, Currency.DKK), InvoiceStatus.PENDING)
    private val problemInvoice = Invoice(2, 2, Money(BigDecimal.TEN, Currency.DKK), InvoiceStatus.PENDING)

    //Mocks
    private val paymentProvider = mockk<PaymentProvider> {
        every { charge(noProblemInvoice) } returns true
        every { charge(problemInvoice) } returns false
    }


    //Tests
    @Test
    fun `will return true if payment goes through`() {
        //mocks for this scenario
        val invoiceService = mockk<InvoiceService> {
            every { markAsPaid(any()) } returns noProblemInvoice
            every { fetchPendingInvoices() } returns listOf(noProblemInvoice)
        }
        //mock injection
        val billingService = BillingService(paymentProvider = paymentProvider, invoiceService = invoiceService)

        assertTrue {
            billingService.proceedSingleInvoice(noProblemInvoice)
        }
    }

    @Test
    fun `will return false if payment fails`() {
        //mocks for this scenario
        val invoiceService = mockk<InvoiceService> {
        }
        //mock injection
        val billingService = BillingService(paymentProvider = paymentProvider, invoiceService = invoiceService)

        assertFalse {
            billingService.proceedSingleInvoice(problemInvoice)
        }
        verify(exactly = 0) { invoiceService.markAsPaid(any()) }
    }
}