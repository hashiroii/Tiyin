package kz.hashiroii.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kz.hashiroii.auth.AuthScreen
import kz.hashiroii.navigation.Auth

fun NavGraphBuilder.authScreen(
    onAuthSuccess: () -> Unit,
    onSkip: () -> Unit,
    onSignInClick: () -> Unit
) {
    composable<Auth> {
        AuthScreen(
            onAuthSuccess = onAuthSuccess,
            onSkip =  onSkip,
            onSignInClick = onSignInClick
        )
    }
}