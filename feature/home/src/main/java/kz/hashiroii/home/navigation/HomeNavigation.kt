package kz.hashiroii.home.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kz.hashiroii.home.HomeScreenRoute
import kz.hashiroii.navigation.Home

fun NavGraphBuilder.homeScreen() {
    composable<Home> {
        HomeScreenRoute()
    }
}
