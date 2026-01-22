package kz.hashiroii.domain.repository

import kz.hashiroii.domain.model.service.Subscription
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getSubscriptions(): Flow<List<Subscription>>
    suspend fun refreshSubscriptions()
}
