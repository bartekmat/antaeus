package io.pleo.antaeus.core.services

import io.mockk.every
import io.mockk.mockk
import io.pleo.antaeus.models.Currency
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.util.*

class CurrencyConverterTest {
    private val rate = 1.00
    private val base = Currency.DKK
    private val target = Currency.SEK
    private val amount = BigDecimal.TEN

    @Test
    fun `will convert correctly if provider provides rate`() {
        //mocks
        val rateProvider = mockk<CurrencyRateProvider> {
            every { getExchangeRateBetween(base, target) } returns Optional.of(rate)
        }
        //mock injection
        val currencyConverter = CurrencyConverter(rateProvider)

        val result = currencyConverter.convert(base, target, amount)
        assertTrue {
            result.isPresent
            result.get().value == amount.multiply(BigDecimal.valueOf(rate)) //at rate 1.00
        }
    }

    @Test
    fun `will return empty optional if no rate provided`() {
        //mocks
        val rateProvider = mockk<CurrencyRateProvider> {
            every { getExchangeRateBetween(base, target) } returns Optional.empty()
        }
        //mock injection
        val currencyConverter = CurrencyConverter(rateProvider)

        val result = currencyConverter.convert(base, target, amount)
        assertTrue {
            result.isEmpty
        }
    }
}