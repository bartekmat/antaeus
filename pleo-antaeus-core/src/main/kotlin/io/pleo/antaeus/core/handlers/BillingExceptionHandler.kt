package io.pleo.antaeus.core.handlers

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.EntityNotFoundException
import io.pleo.antaeus.core.exceptions.MultipleTryFailedException
import io.pleo.antaeus.core.logger.Logger
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.models.Invoice

class BillingExceptionHandler(
        private val invoiceService: InvoiceService,
        private val currencyMismatchHandler: CurrencyMismatchHandler
) {
    fun handleException(exception: Exception, invoiceId: Int) {
        when (exception) {
            is MultipleTryFailedException -> handleFailedRetry(invoiceId)
            is CustomerNotFoundException -> handleNoCustomerFound(invoiceId)
            is CurrencyMismatchException -> handleCurrencyMismatch(invoiceId)
        }
    }

    private fun handleFailedRetry(id: Int) {
        Logger.log.info { "failed to proceed - retry number exceeded" }
        invoiceService.markAsFailed(id)
        //TODO: here we could not execute despite several tries - pass to external service to check/reschedule
    }

    private fun handleCurrencyMismatch(id: Int) {
        Logger.log.info { "Invoice $id has currency mismatch. Fallback - cancel > evaluate > prepare new" }
        currencyMismatchHandler.handle(id)
    }

    private fun handleNoCustomerFound(id: Int) {
        Logger.log.info { "Invoice $id not proceeded, such customer was not found in DB" }
        invoiceService.markAsFailed(id)
        //TODO: invoice is invalid, customer does not exist, no way to find him - call external service to proceed with the situation
    }

    fun handleNoMoney(id: Int) {
        Logger.log.info { "Not managed to charge invoice $id , customer account balance did not allow the charge" }
        val invoice: Invoice = invoiceService.markAsCustomerHadNoMoney(id)
        //TODO: external service can suspend subscription and/or send email to customer - call external service
    }
}