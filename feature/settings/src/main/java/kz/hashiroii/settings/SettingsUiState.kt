package kz.hashiroii.settings

sealed interface SettingsUiState {
    data object Loading : SettingsUiState
    
    data class Success(
        val theme: ThemePreference,
        val language: LanguagePreference,
        val currency: String,
        val cardView: String,
        val showSpendingCard: Boolean,
        val notificationsEnabled: Boolean
    ) : SettingsUiState
    
    data class Error(
        val message: String
    ) : SettingsUiState
}

enum class ThemePreference {
    System, Dark, Light
}

enum class LanguagePreference(val code: String) {
    System(""),
    English("en"),
    Russian("ru"),
    Kazakh("kk");
    
    companion object {
        fun fromCode(code: String): LanguagePreference {
            return when (code) {
                "en" -> English
                "ru" -> Russian
                "kk" -> Kazakh
                "", "System" -> System
                else -> System
            }
        }
    }
}
