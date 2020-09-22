package io.pleo.antaeus.core.services

import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Rates
import java.util.*

class CurrencyRateProvider(
        private val api: CurrencyFreaksApi
) {

    fun getExchangeRateBetween(baseCurrency: Currency, targetCurrency: Currency): Optional<Double> {
        val ratesOptional = api.getRates()
        return if (ratesOptional.isPresent) {
            val rates = ratesOptional.get()
            Optional.of(calculateRate(rates, baseCurrency, targetCurrency))
        } else Optional.empty()
    }

    private fun calculateRate(rates: Rates, invoiceCurrency: Currency, customerCurrency: Currency): Double {
        return rates.get(invoiceCurrency) / rates.get(customerCurrency)
    }
}