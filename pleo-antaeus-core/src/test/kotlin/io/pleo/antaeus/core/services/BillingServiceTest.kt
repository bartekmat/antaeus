package io.pleo.antaeus.core.services

import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class BillingServiceTest {
    //testData
    private val noProblemInvoice = Invoice(1, 1, Money(BigDecimal.TEN, Currency.DKK), InvoiceStatus.PENDING)
    private val problemInvoice = Invoice(2, 2, Money(BigDecimal.TEN, Currency.DKK), InvoiceStatus.PENDING)
    private val slot = CapturingSlot<Exception>()

    //Mocks
    private val paymentProvider = mockk<PaymentProvider> {
        every { charge(noProblemInvoice) } returns true
        every { charge(problemInvoice) } returns false
    }
    private val invoiceCorrector = mockk<InvoiceCorrector> {
        every { getCorrectCopy(noProblemInvoice) } returns Optional.of(noProblemInvoice)
        every { getCorrectCopy(problemInvoice) } returns Optional.of(noProblemInvoice)
    }
    //mock injection
    val billingService = BillingService(paymentProvider = paymentProvider, invoiceCorrector = invoiceCorrector)

    //Tests
    @Test
    fun `will return true if payment goes through`() {

        assertTrue {
            billingService.proceedSingleInvoice(noProblemInvoice)
        }
        verify(exactly = 0) { invoiceCorrector.getCorrectCopy(any()) }
    }

    @Test
    fun `will return false if payment fails due to account balance`() {

        assertFalse {
            billingService.proceedSingleInvoice(problemInvoice)
        }
        verify(exactly = 0) { invoiceCorrector.getCorrectCopy(any()) }
    }

    @Test
    fun `will handle CurrencyException if thrown by payment provider`() {
        //mocks for this scenario
        val provider = mockk<PaymentProvider> {
            every { charge(problemInvoice) } throws CurrencyMismatchException(1, 1)
        }
        //mock injection
        val billingService = BillingService(paymentProvider = provider, invoiceCorrector = invoiceCorrector)
        //when
        billingService.proceedSingleInvoice(problemInvoice)
        verify(atLeast = 1) { invoiceCorrector.getCorrectCopy(any()) }
    }
}
