package io.pleo.antaeus.models

/**
EXPLANATION FOR THIS CLASS
    this class mirrors the object existing in json file that comes from the api
    unfortunately in the json they are not stored in a collection

    i am aware that this solution is very problematic when it comes to adding
    new currencies available in our system, proper way of parsing the json
    (could be done using a map) should be implemented
 */

class Rates(
        private val EUR: Double,
        private val USD: Double,
        private val DKK: Double,
        private val SEK: Double,
        private val GBP: Double) {
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