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
import androidx.compose.ui.text.style.TextAlign
import androidx.work.WorkInfo
import com.airbnb.lottie.compose.*
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.navigation.NavigationRoutes

@Composable
fun SchedulesView(navController: NavController,
                  spaceId: Long?,
                  schedulesViewModel: SchedulesViewModel = viewModel()) {

    val schedules by schedulesViewModel.getSchedules(spaceId!!).observeAsState()
    var selectedSchedule by rememberSaveable { mutableStateOf<String?>(null) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.schedule)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Filled.NavigateBefore, contentDescription = null)
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
                                                contentDescription = null
                                            )
                                            IconButton(
                                                onClick = {
                                                    selectedSchedule = schedule.uuid
                                                    showDeleteDialog = true
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = null
                                                )
                                            }
                                            if (WorkInfo.State.valueOf(schedule.status) == WorkInfo.State.SUCCEEDED) {
                                                IconButton(onClick = { /*TODO: Navigate to view data*/ }) {
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
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            NoSchedulesAnimation()
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
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }
    )
}

@Composable
private fun NoSchedulesAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("animations/18019-shedule.json"))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
       composition,
        progress
    )
}