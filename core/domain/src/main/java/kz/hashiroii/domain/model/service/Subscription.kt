package kz.hashiroii.domain.model.service

import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.ChronoUnit

enum class SubscriptionPeriod {
    MONTHLY,
    YEARLY,
    WEEKLY,
    DAILY,
    QUARTERLY
}

data class Subscription(
    val id: String? = null,
    val serviceInfo: ServiceInfo,
    val amount: BigDecimal,
    val currency: String,
    val period: SubscriptionPeriod,
    val nextPaymentDate: LocalDate,
    val currentPaymentDate: LocalDate
) {
    fun daysUntilNextPayment(today: LocalDate = LocalDate.now()): Int {
        return if (nextPaymentDate.isAfter(today)) {
            ChronoUnit.DAYS.between(today, nextPaymentDate).toInt()
        } else {
            0
        }
    }

    /**
     * If the current period has expired ([today] >= [nextPaymentDate]), returns a copy with
     * start/end rolled to the next period. Otherwise returns this subscription unchanged.
     */
    fun rolledIfExpired(today: LocalDate = LocalDate.now()): Subscription {
        if (today.isBefore(nextPaymentDate)) return this
        val periodLengthDays = ChronoUnit.DAYS.between(currentPaymentDate, nextPaymentDate).toInt()
        if (periodLengthDays <= 0) return this
        val newStart = nextPaymentDate
        val newEnd = newStart.plusDays(periodLengthDays.toLong())
        return copy(currentPaymentDate = newStart, nextPaymentDate = newEnd)
    }

    /** Progress from start to end date only; 0 if before start, 1 if at or after end. */
    fun progressPercentage(today: LocalDate = LocalDate.now()): Float {
        val totalDays = ChronoUnit.DAYS.between(currentPaymentDate, nextPaymentDate).toInt()
        if (totalDays <= 0) return 0f
        if (today.isBefore(currentPaymentDate)) return 0f
        if (!today.isBefore(nextPaymentDate)) return 1f
        val daysPassed = ChronoUnit.DAYS.between(currentPaymentDate, today).toInt()
        return (daysPassed.toFloat() / totalDays).coerceIn(0f, 1f)
    }
}
