package fi.metropolia.intellicircumstances.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
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

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun Navigation() {
    val navController = rememberAnimatedNavController()
    Scaffold(
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                AnimatedNavHost(navController = navController, startDestination = NavigationRoutes.HOME) {
                    composable(
                        route = NavigationRoutes.FAQ,
                        enterTransition = {
                            when (initialState.destination.route) {
                                NavigationRoutes.HOME -> slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
                                else -> null
                            }
                        },
                        exitTransition = {
                            when (targetState.destination.route) {
                                NavigationRoutes.HOME -> slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
                                else -> null
                            }
                        }
                    ) { FaqView(navController) }
                    composable(
                        route = NavigationRoutes.HOME
                    ) { HomeView(navController) }
                    composable(
                        route = NavigationRoutes.MEASURE
                    ) { SpaceSelectionView(navController) }
                    composable(
                        route = NavigationRoutes.MEASURE_SPACE,
                        arguments = listOf(
                            navArgument("spaceId") { type = NavType.StringType },
                            navArgument("spaceName") { type = NavType.StringType }),
                        enterTransition = {
                            when (initialState.destination.route) {
                                NavigationRoutes.MEASURE -> slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
                                else -> null
                            }
                        },
                        exitTransition = {
                            when (targetState.destination.route) {
                                NavigationRoutes.MEASURE -> slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
                                else -> null
                            }
                        }
                    ) {
                        MeasureSpaceView(
                            navController,
                            it.arguments?.getString("spaceId")?.toLong(),
                            it.arguments?.getString("spaceName")
                        )
                    }
                    composable(
                        route = NavigationRoutes.NEW_SCHEDULE,
                        arguments = listOf(navArgument("spaceId") { type = NavType.StringType }),
                        enterTransition = {
                            when (initialState.destination.route) {
                                NavigationRoutes.SCHEDULES -> slideInVertically(animationSpec = tween(700))
                                else -> null
                            }
                        },
                        exitTransition = {
                            when (targetState.destination.route) {
                                NavigationRoutes.SCHEDULES -> slideOutVertically(animationSpec = tween(700))
                                else -> null
                            }
                        }
                    ) {
                        NewScheduleView(navController, it.arguments?.getString("spaceId")?.toLong())
                    }
                    composable(
                        route = NavigationRoutes.PROPERTIES,
                    ) { PropertiesView(navController) }
                    composable(
                        route = NavigationRoutes.SCHEDULES,
                        arguments = listOf(navArgument("spaceId") { type = NavType.StringType }),
                        enterTransition = {
                            when (initialState.destination.route) {
                                NavigationRoutes.MEASURE_SPACE -> slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
                                else -> null
                            }
                        },
                        exitTransition = {
                            when (targetState.destination.route) {
                                NavigationRoutes.MEASURE_SPACE -> slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
                                else -> null
                            }
                        }
                    ) {
                        SchedulesView(navController, it.arguments?.getString("spaceId")?.toLong())
                    }
                    composable(
                        route = NavigationRoutes.SCHEDULE_RESULTS,
                        arguments = listOf(
                            navArgument("spaceId") { type = NavType.StringType },
                            navArgument("scheduleId") { type = NavType.StringType }
                        ),
                        enterTransition = {
                            when (initialState.destination.route) {
                                NavigationRoutes.SCHEDULES -> slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
                                else -> null
                            }
                        },
                        exitTransition = {
                            when (targetState.destination.route) {
                                NavigationRoutes.SCHEDULES -> slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
                                else -> null
                            }
                        }
                    ) {
                        ScheduleResultsView(
                            navController,
                            it.arguments?.getString("spaceId")?.toLong(),
                            it.arguments?.getString("scheduleId")?.toLong()
                        )
                    }
                    composable(
                        route = NavigationRoutes.SETTINGS
                    ) { SettingsView(navController) }
                    composable(
                        route = NavigationRoutes.DEVICES,
                        enterTransition = {
                            when (initialState.destination.route) {
                                NavigationRoutes.SETTINGS -> slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
                                else -> null
                            }
                        },
                        exitTransition = {
                            when (targetState.destination.route) {
                                NavigationRoutes.SETTINGS -> slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
                                else -> null
                            }
                        }
                    ) { DevicesView(navController) }
                    composable(
                        route = NavigationRoutes.SHARED_SCHEDULE,
                        arguments = listOf(navArgument("uuid") { type = NavType.StringType }),
                        enterTransition = {
                            when (initialState.destination.route) {
                                NavigationRoutes.HOME -> slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
                                else -> null
                            }
                        },
                        exitTransition = {
                            when (targetState.destination.route) {
                                NavigationRoutes.HOME -> slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
                                else -> null
                            }
                        }
                    ) {
                        SharedScheduleView(navController, it.arguments?.getString("uuid"))
                    }
                    composable(
                        route = NavigationRoutes.SPACES,
                        arguments = listOf(navArgument("propertyId") { type = NavType.StringType }),
                        enterTransition = {
                            when (initialState.destination.route) {
                                NavigationRoutes.PROPERTIES -> slideIntoContainer(AnimatedContentScope.SlideDirection.Left, animationSpec = tween(700))
                                else -> null
                            }
                        },
                        exitTransition = {
                            when (targetState.destination.route) {
                                NavigationRoutes.PROPERTIES -> slideOutOfContainer(AnimatedContentScope.SlideDirection.Right, animationSpec = tween(700))
                                else -> null
                            }
                        }
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
    const val MEASURE_SPACE = "measure/{spaceId}?spaceName={spaceName}"
    const val NEW_SCHEDULE = "measure/{spaceId}/new-schedule"
    const val PROPERTIES = "properties"
    const val SCHEDULES = "measure/{spaceId}/schedules"
    const val SCHEDULE_RESULTS = "measure/{spaceId}/schedules/{scheduleId}"
    const val SETTINGS = "settings"
    const val DEVICES = "settings/devices"
    const val SHARED_SCHEDULE = "home/shared-schedule/{uuid}"
    const val SPACES = "properties/{propertyId}"
}