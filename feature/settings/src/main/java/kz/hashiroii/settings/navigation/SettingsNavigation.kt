package kz.hashiroii.settings.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kz.hashiroii.navigation.Settings
import kz.hashiroii.settings.SettingsRoute

fun NavGraphBuilder.settingsScreen() {
    composable<Settings> {
        SettingsRoute()
    }
}
