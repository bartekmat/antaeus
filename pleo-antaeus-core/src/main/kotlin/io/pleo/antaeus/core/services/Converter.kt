package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.logger.Logger
import io.pleo.antaeus.core.services.CurrencyRatesProvider
import io.pleo.antaeus.models.Currency
import java.math.BigDecimal
import java.util.*

class Converter(
        private val ratesProvider: CurrencyRatesProvider
) {
    fun convert(invoiceCurrency: Currency, customerCurrency: Currency, amount: BigDecimal): Optional<BigDecimal> {
        val exchangeRate = ratesProvider.getExchangeRate(invoiceCurrency, customerCurrency)
        return if(exchangeRate.isPresent){
            Optional.of(calculateConversion(invoiceCurrency, customerCurrency, amount, exchangeRate.get()))
        }else Optional.empty()

    }
    private fun calculateConversion(invoiceCurrency: Currency, customerCurrency: Currency, amount: BigDecimal, exchangeRate: Double): BigDecimal{
        Logger.log.info { "converted " + amount.toDouble() + " " + invoiceCurrency + " to " + customerCurrency }
        return amount.multiply(BigDecimal.valueOf(exchangeRate))
    }
}