package kz.hashiroii.subscriptionmanager

sealed interface SubscriptionManagerEvent {
    data object NavigateBack : SubscriptionManagerEvent
    data class ShowError(val message: String) : SubscriptionManagerEvent
}