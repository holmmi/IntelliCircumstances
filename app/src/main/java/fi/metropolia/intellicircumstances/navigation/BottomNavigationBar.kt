package fi.metropolia.intellicircumstances.navigation

import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RoomPreferences
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import fi.metropolia.intellicircumstances.R

@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        
        val items = listOf(
            NavigationItem.Home,
            NavigationItem.Spaces,
            NavigationItem.Measure,
            NavigationItem.Settings
        )

        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.navigationIcon, null) },
                label = { Text(stringResource(item.labelText)) },
                selected = currentDestination?.hierarchy?.any {
                    it.route?.startsWith(item.route, true) ?: false
                } == true,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

private sealed class NavigationItem(val route: String, val navigationIcon: ImageVector, val labelText: Int) {
    object Home : NavigationItem(NavigationRoutes.HOME, Icons.Filled.Home, R.string.home)
    object Spaces : NavigationItem(NavigationRoutes.PROPERTIES, Icons.Filled.RoomPreferences, R.string.spaces)
    object Measure : NavigationItem(NavigationRoutes.MEASURE, Icons.Filled.Speed, R.string.measure)
    object Settings : NavigationItem(NavigationRoutes.SETTINGS, Icons.Filled.Settings, R.string.settings)
}