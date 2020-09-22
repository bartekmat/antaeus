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
        return if(exchangeRate.isPresent){
            val convertedAmount = calculateConversion(from, to, amount, exchangeRate.get())
            Optional.of(Money(convertedAmount, to))
        }else Optional.empty()

    }
    private fun calculateConversion(from: Currency, to: Currency, amount: BigDecimal, exchangeRate: Double): BigDecimal{
        Logger.log.info { "converted " + amount.toDouble() + " " + from + " to " + to }
        return amount.multiply(BigDecimal.valueOf(exchangeRate))
    }
}