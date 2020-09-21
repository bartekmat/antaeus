package io.pleo.antaeus.core.commands

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.MultipleTryFailedException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.logger.Logger
import java.util.function.Supplier
import kotlin.random.Random

class RetryableCommand<T>(
        private val maxRetries: Int,
        private var retryCounter: Int = 1
) {
    fun run(function: Supplier<T>): T {
        try {
            generateError()
            return function.get()
        } catch (exception: NetworkException) {
            return retry(function)
        }
    }

    private fun retry(function: Supplier<T>): T {
        Logger.log.error { "Task unsuccessful due to network exception" }
        while (retryCounter <= maxRetries) {
            try {
                generateError()
                return function.get()
            } catch (exception: NetworkException) {
                retryCounter++
                Logger.log.error { "Task unsuccessful due to network exception - " + retryCounter + " try failed" }
            }
        }
        throw MultipleTryFailedException()
    }

    //for test purposes
    private fun generateError() {
        val nextInt = Random.nextInt(1, 15)
        if (nextInt == 2) throw NetworkException()
        if (nextInt == 9) throw NetworkException()
        if (nextInt == 12) throw NetworkException()
        if (nextInt == 5) throw CurrencyMismatchException(1, 1)
        if (nextInt == 8) throw CustomerNotFoundException(1)
    }
}