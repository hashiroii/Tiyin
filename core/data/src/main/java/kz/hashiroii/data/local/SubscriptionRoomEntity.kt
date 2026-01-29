package kz.hashiroii.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "subscriptions")
data class SubscriptionRoomEntity(
    @PrimaryKey
    val id: String,
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
