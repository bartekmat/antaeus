package io.pleo.antaeus.core.commands

import io.pleo.antaeus.core.exceptions.MultipleNetworkException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.logger.Logger
import java.util.function.Supplier

class RetryableCommand<T>(
        private val maxTries: Int,
        private var tryCounter: Int = 1
) {
    fun run(function: Supplier<T>): T {
        while (tryCounter <= maxTries) {
            try {
                return function.get()
            } catch (exception: NetworkException) {
                Logger.log.error { "Task unsuccessful due to network exception - $tryCounter try failed" }
                tryCounter++
                run(function)
            }
        }
        throw MultipleNetworkException()
    }
}