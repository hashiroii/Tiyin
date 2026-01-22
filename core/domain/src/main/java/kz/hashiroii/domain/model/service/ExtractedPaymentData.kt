package kz.hashiroii.domain.model.service

data class ExtractedPaymentData(
    val amount: String,
    val currency: String,
    val isRecurring: Boolean
)
