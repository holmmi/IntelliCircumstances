package fi.metropolia.intellicircumstances.view.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.component.resultview.MeasurementTab
import fi.metropolia.intellicircumstances.component.resultview.TabContent
import fi.metropolia.intellicircumstances.database.Circumstance

@Composable
fun SharedScheduleView(
    navController: NavController,
    uuid: String?,
    sharedScheduleViewModel: SharedScheduleViewModel = viewModel()
) {
    val sharedSchedule by sharedScheduleViewModel.sharedSchedule.observeAsState()
    val measurementTabs = listOf(
        MeasurementTab.HumidityTab,
        MeasurementTab.PressureTab,
        MeasurementTab.TemperatureTab
    )
    var selectedTab by rememberSaveable { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        uuid?.let { sharedScheduleViewModel.getScheduleByUuid(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Filled.NavigateBefore, contentDescription = null)
                    }
                },
                title = { Text(text = sharedSchedule?.name ?: "") }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                TabRow(selectedTabIndex = selectedTab) {
                    measurementTabs.forEachIndexed { index, measurementTab ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(text = stringResource(id = measurementTab.tabName)) },
                        )
                    }
                }
                Column(modifier = Modifier.padding(10.dp)) {
                    sharedSchedule?.let { schedule ->
                        val startDate = schedule.startDate
                        val endDate = schedule.endDate
                        if (startDate != null && endDate != null) {
                            Text(
                                text = String.format(
                                    stringResource(R.string.schedule_date),
                                    sharedScheduleViewModel.getFormattedDate(startDate),
                                    sharedScheduleViewModel.getFormattedDate(endDate)
                                ),
                                style = MaterialTheme.typography.subtitle1,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                            )
                        }
                        val dateError = stringResource(id = R.string.date_error)
                        schedule.records?.let { rec ->
                            TabContent(
                                measurementTab = measurementTabs[selectedTab],
                                circumstances = rec.map {
                                    Circumstance(
                                        scheduleId = null,
                                        time = it.date,
                                        airPressure = it.airPressure,
                                        humidity = it.humidity,
                                        temperature = it.temperature
                                    )
                                },
                                dateFormatter = { date ->
                                    sharedScheduleViewModel.getFormattedDate(
                                        date
                                    ) ?: dateError
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}
