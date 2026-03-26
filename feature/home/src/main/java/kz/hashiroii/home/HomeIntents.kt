package kz.hashiroii.home

sealed interface HomeIntent {
    data object RefreshSubscriptions : HomeIntent
    data class SetSortOrder(val order: SubscriptionSortOrder) : HomeIntent
}
