package kz.hashiroii.domain.usecase

import kz.hashiroii.domain.repository.NotificationRepository
import javax.inject.Inject

class RefreshSubscriptionsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke() = repository.refreshSubscriptions()
}
