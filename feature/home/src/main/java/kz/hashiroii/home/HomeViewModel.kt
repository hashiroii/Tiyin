package kz.hashiroii.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.hashiroii.domain.usecase.CalculateTotalCostUseCase
import kz.hashiroii.domain.usecase.GetSubscriptionsUseCase
import kz.hashiroii.domain.usecase.RefreshSubscriptionsUseCase
import kz.hashiroii.ui.UiText
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSubscriptionsUseCase: GetSubscriptionsUseCase,
    private val refreshSubscriptionsUseCase: RefreshSubscriptionsUseCase,
    private val calculateTotalCostUseCase: CalculateTotalCostUseCase
) : ViewModel() {

    private val preferredCurrency = "KZT"
    private val preferredLocale = Locale("kz", "KZ")

    val uiState: StateFlow<HomeUiState> = combine(
        getSubscriptionsUseCase(),
        calculateTotalCostUseCase(preferredCurrency)
    ) { subscriptions, totalCostResult ->
        val activeCount = subscriptions.size
        
        val currencySymbol = when (totalCostResult.targetCurrency) {
            "KZT" -> "₸"
            "USD" -> "$"
            "EUR" -> "€"
            "RUB" -> "₽"
            "GBP" -> "£"
            else -> totalCostResult.targetCurrency
        }
        
        val formattedAmount = String.format("%.2f", totalCostResult.total).trimEnd('0').trimEnd('.')
        val formattedTotal = "$currencySymbol$formattedAmount"
        
        HomeUiState.Success(
            subscriptions = subscriptions,
            activeSubscriptionsCount = activeCount,
            totalCost = UiText.DynamicString(formattedTotal),
            totalCostCurrency = totalCostResult.targetCurrency
        ) as HomeUiState
    }
        .catch { e ->
            emit(
                HomeUiState.Error(
                    message = UiText.DynamicString(
                        e.message ?: "Unknown error"
                    )
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Loading
        )

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadSubscriptions -> {
            }
            is HomeIntent.RefreshSubscriptions -> refreshSubscriptions()
        }
    }

    private fun refreshSubscriptions() {
        viewModelScope.launch {
            try {
                refreshSubscriptionsUseCase()
            } catch (e: Exception) {
            }
        }
    }
}
