package kz.hashiroii.data.repository

import android.service.notification.StatusBarNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kz.hashiroii.data.local.SubscriptionDao
import kz.hashiroii.data.local.SubscriptionRoomMapper.toEntity
import kz.hashiroii.data.local.SubscriptionRoomMapper.toRoomEntity
import kz.hashiroii.data.mapper.SubscriptionMapper
import kz.hashiroii.data.service.NotificationListener
import kz.hashiroii.data.service.SubscriptionDetectionService
import kz.hashiroii.domain.model.service.Subscription
import kz.hashiroii.domain.repository.AuthRepository
import java.time.LocalDate
import kz.hashiroii.domain.repository.FirestoreSubscriptionRepository
import kz.hashiroii.domain.repository.NotificationRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val subscriptionDao: SubscriptionDao,
    private val subscriptionDetectionService: SubscriptionDetectionService,
    private val authRepository: AuthRepository,
    private val firestoreSubscriptionRepository: FirestoreSubscriptionRepository
) : NotificationRepository {

    private val _notificationEvents = MutableStateFlow<StatusBarNotification?>(null)
    val notificationEvents: SharedFlow<StatusBarNotification?> = _notificationEvents.asSharedFlow()

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        setupNotificationListener()
        observeNotifications()
    }

    private fun setupNotificationListener() {
        NotificationListener.setNotificationHandler { notification ->
            _notificationEvents.value = notification
        }
    }

    override suspend fun refreshSubscriptions(userId: String?) {
        withContext(Dispatchers.IO) {
            if (userId == null) {
                subscriptionDao.deleteAll()
                return@withContext
            }
            try {
                val today = LocalDate.now()
                val subscriptions = firestoreSubscriptionRepository.getUserSubscriptions(userId).first()
                val rolledSubscriptions = subscriptions.map { it.rolledIfExpired(today) }
                val roomEntities = rolledSubscriptions.map { SubscriptionMapper.toEntity(it).toRoomEntity() }
                subscriptionDao.replaceAll(roomEntities)
                firestoreSubscriptionRepository.syncSubscriptions(userId, rolledSubscriptions)
            } catch (e: Exception) {
                // Firestore failed (e.g. API key expired); keep existing local data.
            }
        }
    }

    private fun observeNotifications() {
        repositoryScope.launch {
            notificationEvents.collect { notification ->
                notification?.let { processNotification(it) }
            }
        }
    }

    private fun processNotification(notification: StatusBarNotification) {
        val entity = subscriptionDetectionService.detectSubscription(notification) ?: return
        val id = entity.id ?: UUID.nameUUIDFromBytes(
            "${entity.serviceDomain}-${entity.serviceName}".toByteArray()
        ).toString()
        val entityWithId = entity.copy(id = id)

        repositoryScope.launch {
            withContext(Dispatchers.IO) {
                subscriptionDao.insert(entityWithId.toRoomEntity())
            }
            val user = authRepository.getCurrentUser()
            if (user != null) {
                val subscriptions = subscriptionDao.getAllFlow().first()
                    .map { it.toEntity() }
                    .map { SubscriptionMapper.toDomain(it) }
                firestoreSubscriptionRepository.syncSubscriptions(user.id, subscriptions)
            }
        }
    }

    override fun getSubscriptions(): Flow<List<Subscription>> {
        return subscriptionDao.getAllFlow().map { roomEntities ->
            val today = LocalDate.now()
            val domainList = roomEntities.map { it.toEntity() }.map { SubscriptionMapper.toDomain(it) }
            val rolledList = domainList.map { it.rolledIfExpired(today) }
            rolledList.zip(domainList).filter { (rolled, orig) -> rolled != orig }.forEach { (rolled, orig) ->
                repositoryScope.launch {
                    withContext(Dispatchers.IO) {
                        updateSubscription(orig, rolled)
                        authRepository.getCurrentUser()?.let { user ->
                            rolled.id?.let { id ->
                                firestoreSubscriptionRepository.updateSubscription(user.id, id, rolled)
                            }
                        }
                    }
                }
            }
            rolledList
        }
    }

    override suspend fun addSubscription(subscription: Subscription) {
        withContext(Dispatchers.IO) {
            val entity = SubscriptionMapper.toEntity(subscription)
            subscriptionDao.insert(entity.toRoomEntity())
        }
    }

    override suspend fun updateSubscription(oldSubscription: Subscription, newSubscription: Subscription) {
        withContext(Dispatchers.IO) {
            val newEntity = SubscriptionMapper.toEntity(newSubscription)
            subscriptionDao.update(newEntity.toRoomEntity())
        }
    }

    override suspend fun getSubscriptionById(serviceName: String, serviceDomain: String): Subscription? {
        return withContext(Dispatchers.IO) {
            val subscription = subscriptionDao.getByServiceNameAndDomain(serviceName, serviceDomain)
                ?.toEntity()
                ?.let { SubscriptionMapper.toDomain(it) } ?: return@withContext null
            val today = LocalDate.now()
            val rolled = subscription.rolledIfExpired(today)
            if (rolled != subscription) {
                updateSubscription(subscription, rolled)
                authRepository.getCurrentUser()?.let { user ->
                    rolled.id?.let { id ->
                        firestoreSubscriptionRepository.updateSubscription(user.id, id, rolled)
                    }
                }
            }
            rolled
        }
    }

    override suspend fun deleteSubscription(subscription: Subscription) {
        withContext(Dispatchers.IO) {
            subscription.id?.let { subscriptionDao.deleteById(it) }
                ?: subscriptionDao.deleteByServiceNameAndDomain(
                    subscription.serviceInfo.name,
                    subscription.serviceInfo.domain
                )
        }
    }
}
