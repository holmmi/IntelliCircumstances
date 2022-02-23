package fi.metropolia.intellicircumstances.view.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.google.android.material.datepicker.CompositeDateValidator
import com.google.android.material.datepicker.DateValidatorPointBackward.before
import com.google.android.material.datepicker.DateValidatorPointForward.from
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.component.DatePicker
import fi.metropolia.intellicircumstances.component.TimePicker


@Composable
fun ScheduleView(
    navController: NavController,
    spaceId: Long?,
    scheduleViewModel: ScheduleViewModel = viewModel()
) {
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
                title = { Text(text = stringResource(id = R.string.schedule)) },
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
                    Text(
                        text = stringResource(id = R.string.schedule),
                        style = MaterialTheme.typography.h3
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
                        onSelectDate = {
                            startDate = it
                            scheduleViewModel.setDateConstraints(it, true)
                        },
                        viewModel = scheduleViewModel,
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
                        onSelectDate = {
                            endDate = it
                            scheduleViewModel.setDateConstraints(it, false)
                        },
                        viewModel = scheduleViewModel,
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
                    TextButton(onClick = {
                        startDate = null
                        startHour = 12
                        startMinute = 0
                        endDate = null
                        endHour = 12
                        endMinute = 0
                        scheduleViewModel.resetDateConstraints()
                    }) {
                        Text(text = stringResource(id = R.string.reset))
                    }
                }
            }
        }
    )
}

