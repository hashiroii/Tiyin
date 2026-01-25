package kz.hashiroii.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kz.hashiroii.domain.usecase.preferences.GetPreferencesUseCase
import kz.hashiroii.domain.usecase.preferences.SetCardViewUseCase
import kz.hashiroii.domain.usecase.preferences.SetCurrencyUseCase
import kz.hashiroii.domain.usecase.preferences.SetLanguageUseCase
import kz.hashiroii.domain.usecase.preferences.SetNotificationsEnabledUseCase
import kz.hashiroii.domain.usecase.preferences.SetShowSpendingCardUseCase
import kz.hashiroii.domain.usecase.preferences.SetThemeUseCase
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getPreferencesUseCase: GetPreferencesUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val setLanguageUseCase: SetLanguageUseCase,
    private val setCurrencyUseCase: SetCurrencyUseCase,
    private val setCardViewUseCase: SetCardViewUseCase,
    private val setShowSpendingCardUseCase: SetShowSpendingCardUseCase,
    private val setNotificationsEnabledUseCase: SetNotificationsEnabledUseCase
) : ViewModel() {

    private val _recreateActivity = MutableSharedFlow<Unit>()
    val recreateActivity: SharedFlow<Unit> = _recreateActivity.asSharedFlow()

    val uiState: StateFlow<SettingsUiState> = getPreferencesUseCase()
        .map { preferences ->
            SettingsUiState.Success(
                theme = ThemePreference.valueOf(preferences.theme),
                language = LanguagePreference.fromCode(preferences.language),
                currency = preferences.currency,
                cardView = preferences.cardView,
                showSpendingCard = preferences.showSpendingCard,
                notificationsEnabled = preferences.notificationsEnabled
            ) as SettingsUiState
        }
        .catch { e ->
            emit(
                SettingsUiState.Error(
                    message = e.message ?: "Unknown error"
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.Eagerly,
            initialValue = SettingsUiState.Loading
        )

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            is SettingsIntent.UpdateTheme -> {
                viewModelScope.launch {
                    setThemeUseCase(intent.theme.name)
                }
            }
            is SettingsIntent.UpdateLanguage -> {
                viewModelScope.launch {
                    setLanguageUseCase(intent.language.code)
                    val localeList = if (intent.language.code.isEmpty() || intent.language.code == "System") {
                        LocaleListCompat.getEmptyLocaleList()
                    } else {
                        LocaleListCompat.forLanguageTags(intent.language.code)
                    }
                    AppCompatDelegate.setApplicationLocales(localeList)
                    _recreateActivity.emit(Unit)
                }
            }
            is SettingsIntent.UpdateCurrency -> {
                viewModelScope.launch {
                    setCurrencyUseCase(intent.currency)
                }
            }
            is SettingsIntent.UpdateCardView -> {
                viewModelScope.launch {
                    setCardViewUseCase(intent.cardView)
                }
            }
            is SettingsIntent.UpdateShowSpendingCard -> {
                viewModelScope.launch {
                    setShowSpendingCardUseCase(intent.show)
                }
            }
            is SettingsIntent.UpdateNotifications -> {
                viewModelScope.launch {
                    setNotificationsEnabledUseCase(intent.enabled)
                }
            }
            is SettingsIntent.OpenFeedback -> {
                // Handle feedback
            }
            is SettingsIntent.OpenAbout -> {
                // Handle about
            }
            is SettingsIntent.OpenLicenses -> {
                // Handle licenses
            }
            is SettingsIntent.DeleteAccount -> {
                // Handle account deletion
            }
        }
    }
}