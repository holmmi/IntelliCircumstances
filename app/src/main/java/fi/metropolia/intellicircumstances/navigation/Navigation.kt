package fi.metropolia.intellicircumstances.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fi.metropolia.intellicircumstances.view.faq.FaqView
import fi.metropolia.intellicircumstances.view.home.HomeView
import fi.metropolia.intellicircumstances.view.measure.MeasureSpaceView
import fi.metropolia.intellicircumstances.view.measure.SpaceSelectionView
import fi.metropolia.intellicircumstances.view.schedule.ScheduleView
import fi.metropolia.intellicircumstances.view.settings.SettingsView
import fi.metropolia.intellicircumstances.view.spaces.PropertiesView
import fi.metropolia.intellicircumstances.view.spaces.SpacesView

@Composable
fun Navigation() {
    val navController = rememberNavController()
    Scaffold(
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                NavHost(navController = navController, startDestination = NavigationRoutes.HOME) {
                    composable(NavigationRoutes.FAQ) { FaqView(navController) }
                    composable(NavigationRoutes.HOME) { HomeView(navController) }
                    composable(NavigationRoutes.MEASURE) { SpaceSelectionView(navController) }
                    composable(
                        NavigationRoutes.MEASURE_SPACE,
                        arguments = listOf(navArgument("spaceId") { type = NavType.StringType })
                    ) {
                        MeasureSpaceView(navController, it.arguments?.getString("spaceId")?.toLong())
                    }
                    composable(NavigationRoutes.PROPERTIES) { PropertiesView(navController) }
                    composable(
                        NavigationRoutes.SCHEDULE,
                        arguments = listOf(navArgument("spaceId") { type = NavType.StringType })
                    ) {
                        ScheduleView(navController, it.arguments?.getString("spaceId")?.toLong())
                    }
                    composable(NavigationRoutes.SETTINGS) { SettingsView(navController) }
                    composable(
                        NavigationRoutes.SPACES,
                        arguments = listOf(navArgument("propertyId") { type = NavType.StringType })
                    ) {
                        SpacesView(navController, it.arguments?.getString("propertyId")?.toLong())
                    }
                }
            }
        },
        bottomBar = { BottomNavigationBar(navController) }
    )
}

object NavigationRoutes {
    const val FAQ = "home/faq"
    const val HOME = "home"
    const val MEASURE = "measure"
    const val MEASURE_SPACE = "measure/{spaceId}"
    const val PROPERTIES = "properties"
    const val SCHEDULE = "measure/{spaceId}/schedule"
    const val SETTINGS = "settings"
    const val SPACES = "properties/{propertyId}"
}