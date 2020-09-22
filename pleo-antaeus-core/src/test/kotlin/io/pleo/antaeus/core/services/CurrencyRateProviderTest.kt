package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Rates
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

class CurrencyRateProviderTest {
    private val someRates = Rates(DKK = 1.00, SEK = 2.00, EUR = 3.00, USD = 4.00, GBP = 5.00)
    private val invoiceCurrency = Currency.USD
    private val customerCurrency = Currency.SEK
    private val correctRate = 2.00

    @Test
    fun `will return correct exchange rate if api answers`() {

        //mocks
         val currencyFreaksApi = mockk<CurrencyFreaksApi> {
            every { getRates() } returns Optional.of(someRates)
        }

        //mock injection
         val currencyRateProvider = CurrencyRateProvider(currencyFreaksApi)

        //test call
        val exchangeRate = currencyRateProvider.getExchangeRateBetween(baseCurrency = invoiceCurrency, targetCurrency = customerCurrency)


        assertTrue {
            exchangeRate.isPresent
            exchangeRate.get() == correctRate
        }
    }

    @Test
    fun `wil return empty optional if api fails`(){

        //mocks
        val currencyFreaksApi = mockk<CurrencyFreaksApi> {
            every { getRates() } returns Optional.empty()
        }

        //mock injection
        val currencyRateProvider = CurrencyRateProvider(currencyFreaksApi)

        //test call
        val exchangeRate = currencyRateProvider.getExchangeRateBetween(baseCurrency = invoiceCurrency, targetCurrency = customerCurrency)


        assertTrue {
            exchangeRate.isEmpty
        }
    }
}