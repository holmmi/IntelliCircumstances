package fi.metropolia.intellicircumstances.view.schedule

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.component.DatePicker
import fi.metropolia.intellicircumstances.component.TimePicker
import fi.metropolia.intellicircumstances.navigation.NavigationRoutes

@Composable
fun ScheduleView(navController: NavController, spaceId: Long?) {
    var selectedName by rememberSaveable { mutableStateOf("") }
    var startDate by rememberSaveable { mutableStateOf<Long?>(null) }
    var startHour by rememberSaveable { mutableStateOf(12) }
    var startMinute by rememberSaveable { mutableStateOf(0) }

    var endDate by rememberSaveable { mutableStateOf<Long?>(null) }
    var endHour by rememberSaveable { mutableStateOf(12) }
    var endMinute by rememberSaveable { mutableStateOf(0) }
    var frequency by rememberSaveable { mutableStateOf("") }
    val scaffoldState = rememberScaffoldState()

    val context = LocalContext.current
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.measure)) },
                actions = {
                    IconButton(
                        onClick = {
                            //TODO: check if schedule is already done, if so, show this icon that can delete the schedule
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                    }
                    IconButton(
                        onClick = {
                            //TODO: Init schedule
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Filled.NavigateBefore, contentDescription = null)
                    }
                }
            )
        },
        content = {
            // TODO: Build a schedule form as it is in Figma
            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, bottom = 20.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = stringResource(id = R.string.schedule), style = MaterialTheme.typography.h3)
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, bottom = 20.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp),
                        value = selectedName,
                        onValueChange = { selectedName = it },
                        label = { Text(text = stringResource(id = R.string.schedule_name)) })
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    DatePicker(
                        label = stringResource(id = R.string.start_date),
                        onSelectDate = { startDate = it },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .padding(10.dp)
                    )
                    TimePicker(
                        label = stringResource(id = R.string.start_time),
                        initialHour = startHour,
                        initialMinute = startMinute,
                        modifier = Modifier.padding(10.dp),
                        onSelectTime = { hour, minute ->
                            startHour = hour
                            startMinute = minute
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    DatePicker(
                        label = stringResource(id = R.string.end_date),
                        onSelectDate = { endDate = it },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .padding(10.dp)
                    )
                    TimePicker(
                        label = stringResource(id = R.string.end_time),
                        initialHour = startHour,
                        initialMinute = startMinute,
                        modifier = Modifier.padding(10.dp),
                        onSelectTime = { hour, minute ->
                            endHour = hour
                            endMinute = minute
                        }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, bottom = 20.dp)
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 10.dp),
                        value = frequency,
                        onValueChange = { frequency = it },
                        label = { Text(text = stringResource(id = R.string.frequency)) })
                }
            }
        }
    )
}

