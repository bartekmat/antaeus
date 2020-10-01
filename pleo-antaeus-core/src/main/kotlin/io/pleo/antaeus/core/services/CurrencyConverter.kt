package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.logger.Logger
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Money
import java.math.BigDecimal
import java.util.*

class CurrencyConverter(
        private val rateProvider: CurrencyRateProvider
) {
    fun convert(from: Currency, to: Currency, amount: BigDecimal): Optional<Money> {
        val exchangeRate = rateProvider.getExchangeRateBetween(from, to)
        return if (exchangeRate.isPresent) {
            val convertedAmount = calculateConversion(amount, exchangeRate.get())
            Logger.log.info { "Converted " + amount.toDouble() + " " + from + " to " + to }
            Optional.of(Money(convertedAmount, to))
        } else {
            Logger.log.info { "Conversion from $from to $to failed" }
            Optional.empty()
        }

    }

    private fun calculateConversion(amount: BigDecimal, exchangeRate: Double): BigDecimal {
        return amount.multiply(BigDecimal.valueOf(exchangeRate))
    }
}