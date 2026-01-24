package kz.hashiroii.domain.usecase.subscription

import kz.hashiroii.domain.model.service.Subscription
import kz.hashiroii.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubscriptionsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    operator fun invoke(): Flow<List<Subscription>> = repository.getSubscriptions()
}
