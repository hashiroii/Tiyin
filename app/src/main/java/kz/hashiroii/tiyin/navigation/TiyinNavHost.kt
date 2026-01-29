package kz.hashiroii.tiyin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import kz.hashiroii.analytics.navigation.analyticsScreen
import kz.hashiroii.groups.navigation.groupsScreen
import kz.hashiroii.home.navigation.homeScreen
import kz.hashiroii.navigation.AddSubscription
import kz.hashiroii.navigation.EditSubscription
import kz.hashiroii.navigation.Home
import kz.hashiroii.navigation.Profile
import kz.hashiroii.navigation.Settings
import kz.hashiroii.profile.navigation.profileScreen
import kz.hashiroii.settings.navigation.settingsScreen
import kz.hashiroii.subscriptionmanager.navigation.subscriptionManagerScreen

@Composable
fun TiyinNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onSignInClick: () -> Unit = {},
    onDeleteSubscriptionReady: ((() -> Unit) -> Unit)? = null
) {
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier
    ) {
        homeScreen(
            onAddSubscriptionClick = { navController.navigate(AddSubscription()) },
            onEditSubscriptionClick = { serviceName, serviceDomain ->
                navController.navigate(EditSubscription(serviceName, serviceDomain))
            }
        )
        analyticsScreen()
        groupsScreen()
        profileScreen(
            onBackClick = { navController.navigateUp() },
            onSignInClick = onSignInClick
        )
        settingsScreen()
        subscriptionManagerScreen(
            onBackClick = { navController.navigateUp() },
            onDeleteSubscriptionReady = onDeleteSubscriptionReady
        )
    }
}
