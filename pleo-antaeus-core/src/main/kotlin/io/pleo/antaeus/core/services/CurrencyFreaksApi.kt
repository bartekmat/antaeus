package io.pleo.antaeus.core.services

import com.google.gson.GsonBuilder
import io.pleo.antaeus.models.Currencies
import io.pleo.antaeus.models.Rates
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.*

/*
EXPLANATION - Ideally the API would take two currencies, the amount and return converted value
Free endpoint was used and it returns just the list of currency names and their rates related to USD
 */

class CurrencyFreaksApi {

    fun getRates(): Optional<Rates> {
        val response = callAPI()
        return if (response.isSuccessful) {
            Optional.of(parseRates(response))
        } else {
            Optional.empty()
        }
    }

    private fun callAPI(): Response {
        return OkHttpClient().newCall(prepareRequest()).execute()
    }

    private fun prepareRequest(): Request {
        val url = "https://api.currencyfreaks.com/latest?apikey=1018a7da5be040ac85567ba3562ae776"
        return Request.Builder().url(url).build()
    }

    private fun parseRates(response: Response): Rates {
        val body = response.body?.string()
        val gson = GsonBuilder().create()
        return gson.fromJson(body, Currencies::class.java).rates
    }
}