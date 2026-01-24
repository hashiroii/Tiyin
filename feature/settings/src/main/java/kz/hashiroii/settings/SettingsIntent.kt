package kz.hashiroii.settings

sealed interface SettingsIntent {
    data class UpdateTheme(val theme: ThemePreference) : SettingsIntent
    data class UpdateLanguage(val language: LanguagePreference) : SettingsIntent
    data class UpdateCurrency(val currency: String) : SettingsIntent
    data class UpdateCardView(val cardView: String) : SettingsIntent
    data class UpdateShowSpendingCard(val show: Boolean) : SettingsIntent
    data class UpdateNotifications(val enabled: Boolean) : SettingsIntent
    data object OpenFeedback : SettingsIntent
    data object OpenAbout : SettingsIntent
    data object OpenLicenses : SettingsIntent
    data object DeleteAccount : SettingsIntent
}
