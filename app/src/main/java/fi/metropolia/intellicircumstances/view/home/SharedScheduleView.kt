package fi.metropolia.intellicircumstances.view.home

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import com.google.accompanist.pager.ExperimentalPagerApi
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.component.TabsWithSwiping
import fi.metropolia.intellicircumstances.component.resultview.MeasurementTab
import fi.metropolia.intellicircumstances.database.Circumstance
import fi.metropolia.intellicircumstances.database.Schedule

@ExperimentalPagerApi
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
    val dateError = stringResource(id = R.string.date_error)

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
            if (sharedSchedule != null) {
                sharedSchedule?.records?.let { rec ->
                    TabsWithSwiping(
                        measurementTabs = measurementTabs,
                        circumstances = rec.map {
                            Circumstance(
                                scheduleId = null,
                                time = it.date,
                                airPressure = it.airPressure,
                                humidity = it.humidity,
                                temperature = it.temperature,
                            )
                        },
                        schedule = Schedule(
                            spaceId = null,
                            uuid = sharedSchedule!!.uuid,
                            name = sharedSchedule!!.name ?: "",
                            startDate = sharedSchedule!!.startDate ?: 0,
                            endDate = sharedSchedule!!.endDate ?: 9999999L,
                        ),
                        dateFormatter = { date ->
                            sharedScheduleViewModel.getFormattedDate(
                                date
                            ) ?: dateError
                        }
                    )
                }
            }

        }
    )
}
