package kz.hashiroii.data.repository

import android.service.notification.StatusBarNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kz.hashiroii.data.datasource.MockSubscriptionDataSource
import kz.hashiroii.data.mapper.SubscriptionMapper
import kz.hashiroii.data.model.SubscriptionEntity
import kz.hashiroii.data.service.NotificationListener
import kz.hashiroii.data.service.SubscriptionDetectionService
import kz.hashiroii.domain.model.service.Subscription
import kz.hashiroii.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val subscriptionDetectionService: SubscriptionDetectionService,
    private val mockDataSource: MockSubscriptionDataSource
) : NotificationRepository {

    private val _subscriptionEntities = MutableStateFlow<List<SubscriptionEntity>>(emptyList())
    private val _notificationEvents = MutableStateFlow<StatusBarNotification?>(null)
    val notificationEvents: SharedFlow<StatusBarNotification?> = _notificationEvents.asSharedFlow()

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        setupNotificationListener()
        loadMockData()
        observeNotifications()
    }

    private fun loadMockData() {
        _subscriptionEntities.value = mockDataSource.getMockSubscriptions()
    }

    private fun setupNotificationListener() {
        NotificationListener.setNotificationHandler { notification ->
            _notificationEvents.value = notification
        }
    }

    private fun observeNotifications() {
        notificationEvents
            .onEach { notification ->
                notification?.let { processNotification(it) }
            }
            .launchIn(repositoryScope)
    }

    private fun processNotification(notification: StatusBarNotification) {
        val entity = subscriptionDetectionService.detectSubscription(notification)
            ?: return

        val currentList = _subscriptionEntities.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.serviceName == entity.serviceName }
        
        if (existingIndex >= 0) {
            currentList[existingIndex] = entity
        } else {
            currentList.add(entity)
        }
        
        _subscriptionEntities.value = currentList
    }

    override fun getSubscriptions(): Flow<List<Subscription>> {
        return _subscriptionEntities.map { entities ->
            entities.map { SubscriptionMapper.toDomain(it) }
        }
    }

    override suspend fun refreshSubscriptions() {
    }
}
