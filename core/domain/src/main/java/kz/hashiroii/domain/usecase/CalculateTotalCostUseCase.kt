package kz.hashiroii.domain.usecase

import kz.hashiroii.domain.model.service.Subscription
import kz.hashiroii.domain.repository.CurrencyRepository
import kz.hashiroii.domain.repository.NotificationRepository
import kz.hashiroii.domain.util.CurrencyExtractor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

data class TotalCostResult(
    val total: Double,
    val targetCurrency: String
)

class CalculateTotalCostUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val currencyRepository: CurrencyRepository
) {
    operator fun invoke(
        targetCurrency: String = "KZT"
    ): Flow<TotalCostResult> {
        return combine(
            notificationRepository.getSubscriptions(),
            currencyRepository.getExchangeRates()
        ) { subscriptions, rates ->
            val targetRate = rates[targetCurrency] ?: 1.0
            
            val total = subscriptions.fold(BigDecimal.ZERO) { acc, subscription ->
                val sourceCurrency = CurrencyExtractor.extractCurrencyCode(subscription.cost) ?: "USD"
                val sourceRate = rates[sourceCurrency] ?: 480.0
                val amount = CurrencyExtractor.extractAmount(subscription.cost)
                
                val convertedAmount = if (sourceCurrency == targetCurrency) {
                    BigDecimal.valueOf(amount)
                } else {
                    BigDecimal.valueOf(amount)
                        .multiply(BigDecimal.valueOf(sourceRate))
                        .divide(BigDecimal.valueOf(targetRate), 4, RoundingMode.HALF_UP)
                }
                
                acc.add(convertedAmount)
            }
            
            TotalCostResult(
                total = total.setScale(2, RoundingMode.HALF_UP).toDouble(),
                targetCurrency = targetCurrency
            )
        }
    }
}
