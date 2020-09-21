package io.pleo.antaeus.core.commands

import io.pleo.antaeus.core.exceptions.MultipleTryFailedException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.logger.Logger
import java.util.function.Supplier

class RetryableCommand<T>(
        private val maxRetries: Int,
        private var tryCounter: Int = 1
) {
    fun run(function: Supplier<T>): T {
        try {
            return function.get()
        } catch (exception: NetworkException) {
            return retry(function)
        }
    }

    private fun retry(function: Supplier<T>): T {
        Logger.log.error { "Task unsuccessful due to network exception" }
        while (tryCounter <= maxRetries) {
            try {
                return function.get()
            } catch (exception: NetworkException) {
                tryCounter++
                Logger.log.error { "Task unsuccessful due to network exception - " + tryCounter + " try failed" }
            }
        }
        throw MultipleTryFailedException()
    }
}