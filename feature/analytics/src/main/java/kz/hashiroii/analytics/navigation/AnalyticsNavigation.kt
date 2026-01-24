package kz.hashiroii.analytics.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kz.hashiroii.analytics.AnalyticsScreen
import kz.hashiroii.navigation.Analytics

fun NavGraphBuilder.analyticsScreen() {
    composable<Analytics> {
        AnalyticsScreen()
    }
}
