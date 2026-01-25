package kz.hashiroii.profile.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kz.hashiroii.navigation.Profile
import kz.hashiroii.profile.ProfileScreenRoute

fun NavGraphBuilder.profileScreen(
    onBackClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    composable<Profile> {
        ProfileScreenRoute(
            onBackClick = onBackClick,
            onSignInClick = onSignInClick
        )
    }
}
