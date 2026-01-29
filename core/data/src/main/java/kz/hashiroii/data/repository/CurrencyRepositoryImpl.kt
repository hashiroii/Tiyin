package kz.hashiroii.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kz.hashiroii.domain.repository.CurrencyRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepositoryImpl @Inject constructor() : CurrencyRepository {

    private val exchangeRates = mapOf(  
        "USD" to 480.0,
        "KZT" to 1.0,
        "EUR" to 510.0,
        "RUB" to 6.0
    )

    private val _ratesFlow = MutableStateFlow(exchangeRates)
    private val ratesStateFlow: Flow<Map<String, Double>> = _ratesFlow.asStateFlow()

    override fun getExchangeRates(): Flow<Map<String, Double>> {
        return ratesStateFlow
    }
}
