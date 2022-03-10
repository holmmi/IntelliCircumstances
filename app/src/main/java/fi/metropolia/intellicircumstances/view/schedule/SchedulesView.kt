package fi.metropolia.intellicircumstances.view.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.work.WorkInfo
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.component.animation.ShowAnimation
import fi.metropolia.intellicircumstances.navigation.NavigationRoutes

@Composable
fun SchedulesView(
    navController: NavController,
    spaceId: Long?,
    schedulesViewModel: SchedulesViewModel = viewModel()
) {

    val schedules by schedulesViewModel.getSchedules(spaceId!!).observeAsState()
    var selectedSchedule by rememberSaveable { mutableStateOf<String?>(null) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.schedule),
                        modifier = Modifier.semantics { heading() })
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.NavigateBefore,
                            contentDescription = stringResource(
                                id = R.string.back_to, stringResource(id = R.string.measure)
                            )
                        )
                    }
                }
            )
        },
        content = {
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                schedulesViewModel.deleteSchedule(selectedSchedule!!)
                                showDeleteDialog = false
                            }
                        ) {
                            Text(text = stringResource(id = R.string.delete))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    },
                    text = {
                        Text(text = stringResource(id = R.string.delete_schedule))
                    }
                )
            }

            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                schedules?.let {
                    if (it.isNotEmpty()) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(it) { schedule ->
                                Column {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 4.dp)
                                    ) {
                                        Column() {
                                            Text(
                                                text = schedule.name,
                                                style = MaterialTheme.typography.subtitle1
                                            )
                                            Text(
                                                text = String.format(
                                                    stringResource(id = R.string.starts),
                                                    schedulesViewModel.formatDate(schedule.startDate)
                                                ),
                                                style = MaterialTheme.typography.subtitle2
                                            )
                                            Text(
                                                text = String.format(
                                                    stringResource(id = R.string.ends),
                                                    schedulesViewModel.formatDate(schedule.endDate)
                                                ),
                                                style = MaterialTheme.typography.subtitle2
                                            )
                                        }
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceEvenly,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector =
                                                when (WorkInfo.State.valueOf(schedule.status)) {
                                                    WorkInfo.State.SUCCEEDED -> Icons.Default.Done
                                                    WorkInfo.State.FAILED -> Icons.Default.Clear
                                                    WorkInfo.State.ENQUEUED -> Icons.Default.Pending
                                                    else -> Icons.Default.Help
                                                },
                                                contentDescription = when (WorkInfo.State.valueOf(
                                                    schedule.status
                                                )) {
                                                    WorkInfo.State.SUCCEEDED -> stringResource(id = R.string.schedule_done)
                                                    WorkInfo.State.FAILED -> stringResource(id = R.string.schedule_failed)
                                                    WorkInfo.State.ENQUEUED -> stringResource(id = R.string.schedule_enqueued)
                                                    else -> ""
                                                },
                                            )
                                            IconButton(
                                                onClick = {
                                                    selectedSchedule = schedule.uuid
                                                    showDeleteDialog = true
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = stringResource(
                                                        id = R.string.contentdesc_delete,
                                                        stringResource(
                                                            id = R.string.schedule
                                                        ),
                                                        schedule.name
                                                    )
                                                )
                                            }
                                            if (WorkInfo.State.valueOf(schedule.status) == WorkInfo.State.SUCCEEDED) {
                                                IconButton(
                                                    onClick = {
                                                        if (spaceId != null && schedule.id != null) {
                                                            navController.navigate(
                                                                NavigationRoutes.SCHEDULE_RESULTS
                                                                    .replace(
                                                                        "{spaceId}",
                                                                        spaceId.toString()
                                                                    )
                                                                    .replace(
                                                                        "{scheduleId}",
                                                                        schedule.id.toString()
                                                                    )
                                                            )
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.NavigateNext,
                                                        contentDescription = null
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    Divider()
                                }
                            }
                        }
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Box {
                                ShowAnimation("animations/18019-shedule.json")
                            }
                            Text(
                                text = stringResource(id = R.string.no_schedules),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.body1
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(
                        NavigationRoutes.NEW_SCHEDULE.replace("{spaceId}", spaceId!!.toString())
                    )
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Add, contentDescription = stringResource(
                        id = R.string.add_new, stringResource(
                            id = R.string.schedule
                        )
                    )
                )
            }
        }
    )
}