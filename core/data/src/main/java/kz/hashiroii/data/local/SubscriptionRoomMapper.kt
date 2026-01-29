package kz.hashiroii.data.local

import kz.hashiroii.data.model.SubscriptionEntity
import java.time.LocalDate

object SubscriptionRoomMapper {

    fun SubscriptionRoomEntity.toEntity(): SubscriptionEntity = SubscriptionEntity(
        id = id,
        serviceName = serviceName,
        serviceDomain = serviceDomain,
        cost = cost,
        period = period,
        nextPaymentDate = nextPaymentDate,
        currentPaymentDate = currentPaymentDate,
        serviceType = serviceType,
        primaryColor = primaryColor,
        secondaryColor = secondaryColor
    )

    fun SubscriptionEntity.toRoomEntity(): SubscriptionRoomEntity = SubscriptionRoomEntity(
        id = id ?: throw IllegalArgumentException("SubscriptionEntity must have id for Room"),
        serviceName = serviceName,
        serviceDomain = serviceDomain,
        cost = cost,
        period = period,
        nextPaymentDate = nextPaymentDate,
        currentPaymentDate = currentPaymentDate,
        serviceType = serviceType,
        primaryColor = primaryColor,
        secondaryColor = secondaryColor
    )
}
