package fi.metropolia.intellicircumstances.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import fi.metropolia.intellicircumstances.view.faq.FaqView
import fi.metropolia.intellicircumstances.view.home.HomeView
import fi.metropolia.intellicircumstances.view.measure.MeasureView
import fi.metropolia.intellicircumstances.view.settings.SettingsView
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
                    composable(NavigationRoutes.MEASURE) { MeasureView(navController) }
                    composable(NavigationRoutes.SETTINGS) { SettingsView(navController) }
                    composable(NavigationRoutes.SPACES) { SpacesView(navController) }
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
    const val SETTINGS = "settings"
    const val SPACES = "spaces"
}