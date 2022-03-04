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
import com.google.accompanist.pager.ExperimentalPagerApi
import fi.metropolia.intellicircumstances.view.faq.FaqView
import fi.metropolia.intellicircumstances.view.home.HomeView
import fi.metropolia.intellicircumstances.view.home.SharedScheduleView
import fi.metropolia.intellicircumstances.view.measure.MeasureSpaceView
import fi.metropolia.intellicircumstances.view.measure.SpaceSelectionView
import fi.metropolia.intellicircumstances.view.schedule.NewScheduleView
import fi.metropolia.intellicircumstances.view.schedule.ScheduleResultsView
import fi.metropolia.intellicircumstances.view.schedule.SchedulesView
import fi.metropolia.intellicircumstances.view.settings.DevicesView
import fi.metropolia.intellicircumstances.view.settings.SettingsView
import fi.metropolia.intellicircumstances.view.spaces.PropertiesView
import fi.metropolia.intellicircumstances.view.spaces.SpacesView

@ExperimentalPagerApi
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
                    composable(
                        NavigationRoutes.NEW_SCHEDULE,
                        arguments = listOf(navArgument("spaceId") { type = NavType.StringType })
                    ) {
                        NewScheduleView(navController, it.arguments?.getString("spaceId")?.toLong())
                    }
                    composable(NavigationRoutes.PROPERTIES) { PropertiesView(navController) }
                    composable(
                        NavigationRoutes.SCHEDULES,
                        arguments = listOf(navArgument("spaceId") { type = NavType.StringType })
                    ) {
                        SchedulesView(navController, it.arguments?.getString("spaceId")?.toLong())
                    }
                    composable(
                        NavigationRoutes.SCHEDULE_RESULTS,
                        arguments = listOf(
                            navArgument("spaceId") { type = NavType.StringType },
                            navArgument("scheduleId") { type = NavType.StringType }
                        )
                    ) {
                        ScheduleResultsView(
                            navController,
                            it.arguments?.getString("spaceId")?.toLong(),
                            it.arguments?.getString("scheduleId")?.toLong()
                        )
                    }
                    composable(NavigationRoutes.SETTINGS) { SettingsView(navController) }
                    composable(NavigationRoutes.DEVICES) { DevicesView(navController) }
                    composable(
                        NavigationRoutes.SHARED_SCHEDULE,
                        arguments = listOf(navArgument("uuid") { type = NavType.StringType })
                    ) {
                        SharedScheduleView(navController, it.arguments?.getString("uuid"))
                    }
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
    const val NEW_SCHEDULE = "measure/{spaceId}/new-schedule"
    const val PROPERTIES = "properties"
    const val SCHEDULES = "measure/{spaceId}/schedules"
    const val SCHEDULE_RESULTS = "measure/{spaceId}/schedules/{scheduleId}"
    const val SETTINGS = "settings"
    const val DEVICES = "settings/devices"
    const val SHARED_SCHEDULE = "home/shared-schedule/{uuid}"
    const val SPACES = "properties/{propertyId}"
}