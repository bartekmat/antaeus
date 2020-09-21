package io.pleo.antaeus.core.handlers

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.MultipleTryFailedException
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Money
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class BillingExceptionHandlerTest {
    private val updatedInvoice = Invoice(1, 1, Money(BigDecimal.TEN, Currency.DKK), InvoiceStatus.PENDING)
    private val invoiceService = mockk<InvoiceService>(){
        every { markAsFailed(any()) } returns updatedInvoice
        every { markAsPaid(any()) } returns updatedInvoice
        every { markAsCustomerHadNoMoney(any()) } returns updatedInvoice
    }
    private val currencyMismatchHandler = mockk<CurrencyMismatchHandler>(){
        every { handle(any()) } answers {}
    }

    private val billingExceptionHandler = BillingExceptionHandler(invoiceService = invoiceService, currencyMismatchHandler = currencyMismatchHandler)

    @Test
    fun`will update invoice status to failed if multiple network exception`(){
        billingExceptionHandler.handleException(MultipleTryFailedException(),1)
        verify(exactly = 1) { invoiceService.markAsFailed(1) }
    }
    @Test
    fun`will update invoice status to failed if customer not found`(){
        billingExceptionHandler.handleException(CustomerNotFoundException(1),1)
        verify(exactly = 1) { invoiceService.markAsFailed(1) }
    }
    @Test
    fun`will update invoice status to failed and schedule new invoice if currency mismatch`(){
        billingExceptionHandler.handleException(CurrencyMismatchException(1,1),1)
        verify(exactly = 1) { invoiceService.markAsFailed(1) }
        verify(exactly = 1) { invoiceService.markAsFailed(1) }
    }
}