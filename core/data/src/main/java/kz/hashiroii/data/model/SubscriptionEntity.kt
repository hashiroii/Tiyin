package kz.hashiroii.data.model

import java.time.LocalDate

data class SubscriptionEntity(
    val serviceName: String,
    val serviceDomain: String,
    val cost: String,
    val period: String,
    val nextPaymentDate: LocalDate,
    val currentPaymentDate: LocalDate,
    val serviceType: String,
    val primaryColor: Long = 0,
    val secondaryColor: Long = 0
)
