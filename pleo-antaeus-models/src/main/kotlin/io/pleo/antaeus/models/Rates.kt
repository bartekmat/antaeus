package io.pleo.antaeus.models

class Rates(val EUR: Double, val USD: Double, val DKK: Double, val SEK: Double, val GBP: Double) {
    fun get(symbol: Currency): Double {
        if (symbol == Currency.EUR) return EUR
        if (symbol == Currency.USD) return USD
        if (symbol == Currency.DKK) return DKK
        if (symbol == Currency.SEK) return SEK
        if (symbol == Currency.GBP) return GBP
        throw CurrencyNotFoundException()
    }
    class CurrencyNotFoundException : Exception()
}