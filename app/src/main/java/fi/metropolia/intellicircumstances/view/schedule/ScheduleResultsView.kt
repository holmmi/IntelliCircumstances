package fi.metropolia.intellicircumstances.view.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import fi.metropolia.intellicircumstances.R

@Composable
fun ScheduleResultsView(
    navController: NavController,
    spaceId: Long?,
    scheduleId: Long?,
    scheduleResultsViewModel: ScheduleResultsViewModel = viewModel()
) {
    var selectedTab by rememberSaveable { mutableStateOf(0) }

    Scaffold(
        topBar = {
             TopAppBar(
                 title = {},
                 navigationIcon = {
                     IconButton(onClick = { navController.navigateUp() }) {
                         Icon(imageVector = Icons.Filled.NavigateBefore, contentDescription = null)
                     }
                 }
             )
        },
        content = {
            TabRow(selectedTabIndex = selectedTab) {
                measurementTabs.forEachIndexed { index, measurementTab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(text = stringResource(id = measurementTab.tabName)) },
                    )
                }
            }
            when (measurementTabs[selectedTab]) {
                MeasurementTab.HumidityTab -> HumidityTabContent(scheduleResultsViewModel)
                MeasurementTab.PressureTab -> PressureTabContent(scheduleResultsViewModel)
                MeasurementTab.TemperatureTab -> TemperatureTabContent(scheduleResultsViewModel)
            }
        }
    )
}

@Composable
private fun HumidityTabContent(scheduleResultsViewModel: ScheduleResultsViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Humidity")
    }
}

@Composable
private fun PressureTabContent(scheduleResultsViewModel: ScheduleResultsViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Pressure")
    }
}

@Composable
private fun TemperatureTabContent(scheduleResultsViewModel: ScheduleResultsViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = "Temperature")
    }
}

private val measurementTabs = listOf(
    MeasurementTab.HumidityTab,
    MeasurementTab.PressureTab,
    MeasurementTab.TemperatureTab
)

private sealed class MeasurementTab(val tabName: Int) {
    object HumidityTab: MeasurementTab(R.string.humid)
    object PressureTab: MeasurementTab(R.string.pressure)
    object TemperatureTab: MeasurementTab(R.string.temp)
}