package io.pleo.antaeus.core.handlers

import com.google.gson.GsonBuilder
import io.pleo.antaeus.models.Currencies
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Rates
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.*

class CurrencyRatesProvider {
    /*
HERE DECISION - ideally api endpoint which takes two currencies, the amount and returns converted value should be used
however, i was not able to found free api - therefore free endpoint was used and it returns just the list of currencies
 */
    fun getExchangeRate(invoiceCurrency: Currency, customerCurrency: Currency): Optional<Double> {
        val ratesOptional = getRates()
        return if (ratesOptional.isPresent) {
            val rates = ratesOptional.get()
            Optional.of(rates.get(invoiceCurrency) / rates.get(customerCurrency))
        } else Optional.empty()
    }

    private fun getRates(): Optional<Rates> {
        val request = prepareRequest()

        val response: Response = callAPI(request)
        return if (response.isSuccessful) {
            Optional.of(parseRates(response))
        } else {
            Optional.empty()
        }
    }

    private fun prepareRequest(): Request {
        val url = "https://api.currencyfreaks.com/latest?apikey=1018a7da5be040ac85567ba3562ae776"
        return Request.Builder().url(url).build()
    }

    private fun callAPI (request: Request): Response {
        return OkHttpClient().newCall(request).execute()
    }

    private fun parseRates(response: Response): Rates {
        val body = response.body?.string()
        val gson = GsonBuilder().create()
        return gson.fromJson(body, Currencies::class.java).rates
    }

}