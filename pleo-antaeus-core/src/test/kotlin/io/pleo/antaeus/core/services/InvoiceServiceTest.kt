package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.*
import io.pleo.antaeus.models.Currency
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.*

class InvoiceServiceTest {
    private val existingInvoice = Invoice(1, 1, Money(BigDecimal.TEN, Currency.DKK), InvoiceStatus.PENDING)
    private val paidInvoice = Invoice(1, 1, Money(BigDecimal.TEN, Currency.DKK), InvoiceStatus.PAID)

    private val dal = mockk<AntaeusDal> {
        every { fetchInvoice(404) } returns null
        every { fetchInvoice(1) } returns existingInvoice
    }

    private val invoiceService = InvoiceService(dal = dal)

    @Test
    fun `will throw if invoice is not found`() {
        assertThrows<InvoiceNotFoundException> {
            invoiceService.fetch(404)
        }
    }

    @Test
    fun `will return invoice if found`() {
        assertTrue {
            invoiceService.fetch(1).equals(existingInvoice)
        }
    }

    @Test
    fun `will return list of pending invoices if and exist`() {
        val dal = mockk<AntaeusDal> {
            every { fetchInvoicesByStatus(InvoiceStatus.PENDING) } returns listOf(existingInvoice)
        }

        val invoiceService = InvoiceService(dal = dal)

        assertTrue(invoiceService.fetchPendingInvoices().isNotEmpty())

        invoiceService.fetchPendingInvoices().forEach {
            assertTrue {
                it.status.equals(InvoiceStatus.PENDING)
            }
        }
    }

    @Test
    fun `will return empty list if no pending invoice exist`() {
        val dal = mockk<AntaeusDal> {
            every { fetchInvoicesByStatus(InvoiceStatus.PENDING) } returns listOf()
        }

        val invoiceService = InvoiceService(dal = dal)

        assertTrue(invoiceService.fetchPendingInvoices().isEmpty())
    }

    @Test
    fun `will return invoice if saved successfully`() {
        val dal = mockk<AntaeusDal> {
            every { createInvoice(any(), any()) } returns existingInvoice
        }
        val invoiceService1 = InvoiceService(dal = dal)
        //when
        val created = invoiceService1.create(Money(BigDecimal.ONE, Currency.DKK), Customer(1, Currency.DKK))
        assertTrue(created.get().equals(existingInvoice))
    }

    @Test
    fun `will throw if saving fails`() {
        val dal = mockk<AntaeusDal> {
            every { createInvoice(any(), any()) } returns null
        }
        val invoiceService = InvoiceService(dal = dal)
        val created: Optional<Invoice> = invoiceService.create(Money(BigDecimal.ONE, Currency.DKK), Customer(1, Currency.DKK))
        assertTrue {
            created.isEmpty
        }
    }
}
