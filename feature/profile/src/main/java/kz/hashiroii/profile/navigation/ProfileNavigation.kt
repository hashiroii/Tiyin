package kz.hashiroii.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kz.hashiroii.navigation.Profile
import kz.hashiroii.profile.ProfileScreen

fun NavGraphBuilder.profileScreen(
    onBackClick: () -> Unit
) {
    composable<Profile> {
        ProfileScreen(onBackClick = onBackClick)
    }
}
