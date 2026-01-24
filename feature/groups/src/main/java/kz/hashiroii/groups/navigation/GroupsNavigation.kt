package kz.hashiroii.groups.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kz.hashiroii.groups.GroupsScreen
import kz.hashiroii.navigation.Groups

fun NavGraphBuilder.groupsScreen() {
    composable<Groups> {
        GroupsScreen()
    }
}
