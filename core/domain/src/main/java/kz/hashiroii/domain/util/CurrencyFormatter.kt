package kz.hashiroii.domain.util

import java.util.Currency
import java.util.Locale

enum class CurrencyCode(val code: String, val symbol: String) {
    USD("USD", "$"),
    EUR("EUR", "€"),
    KZT("KZT", "₸"),
    RUB("RUB", "₽"),
    GBP("GBP", "£")
}

object CurrencyFormatter {
    private var currentCurrency: CurrencyCode = CurrencyCode.USD
    private var currentLocale: Locale = Locale.US

    fun setCurrency(currency: CurrencyCode) {
        currentCurrency = currency
        currentLocale = when (currency) {
            CurrencyCode.USD -> Locale.US
            CurrencyCode.EUR -> Locale.GERMANY
            CurrencyCode.KZT -> Locale("kz", "KZ")
            CurrencyCode.RUB -> Locale("ru", "RU")
            CurrencyCode.GBP -> Locale.UK
        }
    }

    fun format(amount: Double): String {
        val currency = Currency.getInstance(currentCurrency.code)
        val formatter = java.text.NumberFormat.getCurrencyInstance(currentLocale)
        formatter.currency = currency
        return formatter.format(amount)
    }

    fun format(amount: String): String {
        return try {
            format(amount.toDouble())
        } catch (e: Exception) {
            "${currentCurrency.symbol}$amount"
        }
    }

    fun getCurrentCurrency(): CurrencyCode = currentCurrency
}
