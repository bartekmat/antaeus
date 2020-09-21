package io.pleo.antaeus.core.handlers

import com.google.gson.GsonBuilder
import io.pleo.antaeus.core.logger.Logger
import io.pleo.antaeus.core.services.CustomerService
import io.pleo.antaeus.core.services.InvoiceService
import io.pleo.antaeus.models.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.math.BigDecimal

class InvoiceCorrector(
        private val customerService: CustomerService,
        private val invoiceService: InvoiceService
) {
    //TODO implement this
    fun getCorrectCopy(invoice: Invoice): Invoice{

        val customer = customerService.fetch(invoice.customerId)
        val convertedAmount = convert(invoice.amount.currency, customer.currency, invoice.amount.value)
        val correctMoney = Money(convertedAmount, customer.currency)

        return invoiceService.create(amount = correctMoney, customer = customer);
    }

    private fun convert(invoiceCurrency: Currency, customerCurrency: Currency, amount: BigDecimal): BigDecimal {
        val exchangeRate: Double = getExchangeRate(invoiceCurrency, customerCurrency)
        Logger.log.info { "converted " + amount.toDouble() + " " + invoiceCurrency + " to " + customerCurrency }
        return amount.multiply(BigDecimal.valueOf(exchangeRate))
    }

    private fun getExchangeRate(invoiceCurrency: Currency, customerCurrency: Currency): Double {
        val currencyRates: Rates
        val request = prepareRequest()

        val response: Response = callAPI(request)
        if (response.isSuccessful) {
            currencyRates = parseRates(response)
        } else {
            throw Exception()
        }
        return currencyRates.get(invoiceCurrency) / currencyRates.get(customerCurrency)
    }

    private fun prepareRequest(): Request {
        val url = "https://api.currencyfreaks.com/latest?apikey=1018a7da5be040ac85567ba3562ae776"
        val request = Request.Builder().url(url).build()
        return request
    }

    private fun callAPI (request: Request): Response{
       return OkHttpClient().newCall(request).execute()
    }

    private fun parseRates(response: Response): Rates {
        val body = response.body?.string()
        val gson = GsonBuilder().create()
        return gson.fromJson(body, Currencies::class.java).rates
    }
}
/*
HERE DECISION PROCESS - ideally api endpoint which takes two currencies, the amount and returns converted value should be used
however, i was not able to found free api - therefore free endpoint was used and it returns just the list of currencies
 */

