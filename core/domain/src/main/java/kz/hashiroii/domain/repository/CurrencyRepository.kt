package kz.hashiroii.domain.repository

import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun getExchangeRates(): Flow<Map<String, Double>>
}
