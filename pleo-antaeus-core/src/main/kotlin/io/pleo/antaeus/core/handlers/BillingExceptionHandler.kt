package io.pleo.antaeus.core.handlers

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.EntityNotFoundException
import io.pleo.antaeus.core.exceptions.MultipleTryFailedException
import io.pleo.antaeus.core.logger.Logger

class BillingExceptionHandler {
    fun handleException(exception: Exception) {
        when (exception) {
            is MultipleTryFailedException -> handleFailedRetry()
            is EntityNotFoundException -> handleNoCustomerFound()
            is CurrencyMismatchException -> handleCurrencyMismatch()
        }
    }

    private fun handleFailedRetry() {
        //TODO: here we could not execute despite several tries - log and pass to external handler
        Logger.log.info { "failed to proceed - retry number exceeded" }
    }

    private fun handleCurrencyMismatch() {
        //TODO: here i should change invoice status to invalid, log problem and create a new one with new data using external api currency converter
        Logger.log.info { "currency mismatch" }
    }

    private fun handleNoCustomerFound() {
        //TODO: invoice should be set as invalid , log, possibly send notification through some interface
        Logger.log.info { "no customer found" }
    }
}