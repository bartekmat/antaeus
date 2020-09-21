package io.pleo.antaeus.core.services

import io.mockk.*
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

class BillingServiceTest {
//    //testData
//    private val noProblemInvoice = Invoice(1, 1, Money(BigDecimal.TEN, Currency.DKK), InvoiceStatus.PENDING)
//    private val problemInvoice = Invoice(2, 2, Money(BigDecimal.TEN, Currency.DKK), InvoiceStatus.PENDING)
//    private val slot = CapturingSlot<Exception>()
//
//    //Mocks
//    private val paymentProvider = mockk<PaymentProvider> {
//        every { charge(noProblemInvoice) } returns true
//        every { charge(problemInvoice) } returns false
//    }
//    private val handler = mockk<HandlingStrategy> {
//        every { handleException(any()) } returns Unit
//    }
//
//
//    //Tests
//    @Test
//    fun `will return true if payment goes through`() {
//        //mocks for this scenario
//        val invoiceService = mockk<InvoiceService> {
//            every { markAsPaid(any()) } returns noProblemInvoice
//        }
//        //mock injection
//        val billingService = BillingService(paymentProvider = paymentProvider, invoiceService = invoiceService, handler = handler)
//
//        assertTrue {
//            billingService.proceedSingleInvoice(noProblemInvoice)
//        }
//        verify(exactly = 0) { handler.handleException(any()) }
//    }
//
//    @Test
//    fun `will return false if payment fails due to account balance`() {
//        //mocks for this scenario
//        val invoiceService = mockk<InvoiceService> {
//        }
//        //mock injection
//        val billingService = BillingService(paymentProvider = paymentProvider, invoiceService = invoiceService, handler = handler)
//
//        assertFalse {
//            billingService.proceedSingleInvoice(problemInvoice)
//        }
//        verify(exactly = 0) { invoiceService.markAsPaid(any()) }
//        verify(exactly = 0) { handler.handleException(any()) }
//    }
//
//    @Test
//    fun `will handle CurrencyException if thrown by payment provider`() {
//        //mocks for this scenario
//        val invoiceService = mockk<InvoiceService> {}
//
//        val provider = mockk<PaymentProvider> {
//            every { charge(any()) } throws CurrencyMismatchException(1, 1)
//        }
//        val handler = mockk<HandlingStrategy> {
//            every { handleException(exception = capture(slot)) } answers {
//                println(slot.captured)
//                false
//            }
//        }
//
//        //mock injection
//        val billingService = BillingService(paymentProvider = provider, invoiceService = invoiceService, handler = handler)
//        billingService.proceedSingleInvoice(problemInvoice)
//        val captured: Exception = slot.captured
//        assertTrue {
//            captured is CurrencyMismatchException
//        }
//    }
}
