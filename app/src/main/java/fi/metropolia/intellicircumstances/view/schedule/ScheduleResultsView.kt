package fi.metropolia.intellicircumstances.view.schedule

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.component.ShowAlertDialog
import fi.metropolia.intellicircumstances.component.TabsWithSwiping
import fi.metropolia.intellicircumstances.component.resultview.MeasurementTab

@ExperimentalPagerApi
@Composable
fun ScheduleResultsView(
    navController: NavController,
    spaceId: Long?,
    scheduleId: Long?,
    scheduleResultsViewModel: ScheduleResultsViewModel = viewModel()
) {
    val measurementTabs = listOf(
        MeasurementTab.HumidityTab,
        MeasurementTab.PressureTab,
        MeasurementTab.TemperatureTab
    )
    val circumstances by scheduleResultsViewModel.getCircumstancesByScheduleId(scheduleId!!)
        .observeAsState()
    val schedule by scheduleResultsViewModel.getScheduleById(scheduleId!!).observeAsState()
    val isScheduleShared by scheduleResultsViewModel.isScheduleShared.observeAsState()
    val shareSucceeded by scheduleResultsViewModel.shareSucceeded.observeAsState()
    var showAlreadySharedAlert by rememberSaveable { mutableStateOf(false) }
    var showSharingResultAlert by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(isScheduleShared) {
        isScheduleShared?.let { isShared ->
            if (!isShared) {
                schedule?.let { scheduleResultsViewModel.shareSchedule(it) }
            } else {
                showAlreadySharedAlert = true
            }
            scheduleResultsViewModel.resetScheduleIsShared()
        }
    }

    LaunchedEffect(shareSucceeded) {
        shareSucceeded?.let {
            showSharingResultAlert = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = schedule?.name ?: "",
                        modifier = Modifier.semantics { heading() })
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Filled.NavigateBefore, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            schedule?.let { s ->
                                s.uuid?.let { scheduleResultsViewModel.checkIfScheduleIsShared(it) }
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Share, contentDescription = null)
                    }
                }
            )
        },
        content = {
            ShowAlertDialog(
                title = stringResource(id = R.string.schedule_already_shared_title),
                content = stringResource(id = R.string.schedule_already_shared_content),
                visible = showAlreadySharedAlert,
                onConfirm = { showAlreadySharedAlert = false },
                onDismiss = { showAlreadySharedAlert = false }
            )

            ShowAlertDialog(
                title = stringResource(if (shareSucceeded == true) R.string.schedule_shared_title else R.string.schedule_share_failed_title),
                content = stringResource(if (shareSucceeded == true) R.string.schedule_shared_content else R.string.schedule_share_failed_content),
                visible = showSharingResultAlert,
                onConfirm = {
                    showSharingResultAlert = false
                    scheduleResultsViewModel.resetSharedSucceed()
                },
                onDismiss = {
                    showSharingResultAlert = false
                    scheduleResultsViewModel.resetSharedSucceed()
                }
            )

            TabsWithSwiping(
                measurementTabs = measurementTabs,
                circumstances = circumstances,
                schedule = schedule,
                dateFormatter = { date -> scheduleResultsViewModel.getFormattedDate(date) })
        }
    )
}
