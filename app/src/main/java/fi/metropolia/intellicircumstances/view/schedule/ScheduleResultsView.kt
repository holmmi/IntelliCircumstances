package fi.metropolia.intellicircumstances.view.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.component.ShowAlertDialog
import fi.metropolia.intellicircumstances.component.resultview.MeasurementTab
import fi.metropolia.intellicircumstances.component.resultview.TabContent
import fi.metropolia.intellicircumstances.database.Circumstance
import fi.metropolia.intellicircumstances.ui.theme.Red100
import fi.metropolia.intellicircumstances.ui.theme.Red200
import fi.metropolia.intellicircumstances.ui.theme.Red300
import fi.metropolia.intellicircumstances.ui.theme.Red500

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
    var selectedTab by rememberSaveable { mutableStateOf(0) }
    val circumstances by scheduleResultsViewModel.getCircumstancesByScheduleId(scheduleId!!).observeAsState()
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
                 title = { Text(text = schedule?.name ?: "") },
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
                    schedule?.let {
                        Text(
                            text = String.format(
                                stringResource(R.string.schedule_date),
                                scheduleResultsViewModel.getFormattedDate(it.startDate),
                                scheduleResultsViewModel.getFormattedDate(it.endDate)
                            ),
                            style = MaterialTheme.typography.subtitle1,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                        )
                    }
                    circumstances?.let {
                        TabContent(
                            measurementTab = measurementTabs[selectedTab],
                            circumstances = it,
                            dateFormatter = { date -> scheduleResultsViewModel.getFormattedDate(date) }
                        )
                    }
                }
            }
        }
    )
}
