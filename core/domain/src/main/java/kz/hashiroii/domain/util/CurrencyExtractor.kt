package kz.hashiroii.domain.util

object CurrencyExtractor {
    private val currencySymbols = mapOf(
        "$" to "USD",
        "€" to "EUR",
        "₸" to "KZT",
        "₽" to "RUB",
        "£" to "GBP"
    )
    
    private val currencyText = mapOf(
        "dollar" to "USD",
        "euro" to "EUR",
        "тенге" to "KZT",
        "рубль" to "RUB",
        "pound" to "GBP"
    )
    
    fun extractCurrencySymbol(cost: String): String? {
        for ((symbol, code) in currencySymbols) {
            if (cost.contains(symbol)) {
                return symbol
            }
        }
        val lowerCost = cost.lowercase()
        for ((text, code) in currencyText) {
            if (lowerCost.contains(text)) {
                return currencySymbols.entries.find { it.value == code }?.key
            }
        }
        return null
    }
    
    fun extractCurrencyCode(cost: String): String? {
        for ((symbol, code) in currencySymbols) {
            if (cost.contains(symbol)) {
                return code
            }
        }
        val lowerCost = cost.lowercase()
        for ((text, code) in currencyText) {
            if (lowerCost.contains(text)) {
                return code
            }
        }
        return null
    }
    
    fun extractAmount(cost: String): Double {
        val cleaned = cost.replace(Regex("[^0-9.,]"), "").replace(",", ".")
        return cleaned.toDoubleOrNull() ?: 0.0
    }
}
