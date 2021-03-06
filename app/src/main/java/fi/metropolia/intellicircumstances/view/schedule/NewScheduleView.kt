package fi.metropolia.intellicircumstances.view.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.component.DatePicker
import fi.metropolia.intellicircumstances.component.TimePicker

@Composable
fun NewScheduleView(
    navController: NavController,
    spaceId: Long?,
    newScheduleViewModel: NewScheduleViewModel = viewModel()
) {
    var selectedName by rememberSaveable { mutableStateOf("") }
    var startDate by rememberSaveable { mutableStateOf<Long?>(null) }
    var startHour by rememberSaveable { mutableStateOf(12) }
    var startMinute by rememberSaveable { mutableStateOf(0) }

    var endDate by rememberSaveable { mutableStateOf<Long?>(null) }
    var endHour by rememberSaveable { mutableStateOf(12) }
    var endMinute by rememberSaveable { mutableStateOf(0) }

    var showFormErrors by rememberSaveable { mutableStateOf(false) }

    val formErrors by newScheduleViewModel.formErrors.observeAsState()

    LaunchedEffect(formErrors) {
        formErrors?.let { showFormErrors = it.isNotEmpty() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.new_schedule)) },
                actions = {
                    IconButton(
                        onClick = {
                            val result = newScheduleViewModel.validateForm(
                                spaceId!!,
                                selectedName,
                                startDate!!,
                                startHour,
                                startMinute,
                                endDate!!,
                                endHour,
                                endMinute
                            )
                            if (result) {
                                navController.navigateUp()
                            }
                        },
                        enabled = startDate != null && endDate != null
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.NavigateBefore,
                            contentDescription = stringResource(
                                id = R.string.back_to, stringResource(id = R.string.schedule_list)
                            )
                        )
                    }
                }
            )
        },
        content = {
            if (showFormErrors) {
                AlertDialog(
                    onDismissRequest = {
                        showFormErrors = false
                        newScheduleViewModel.resetFormErrors()
                    },
                    title = { Text(text = stringResource(id = R.string.form_errors)) },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showFormErrors = false
                                newScheduleViewModel.resetFormErrors()
                            }
                        ) {
                            Text(text = stringResource(id = R.string.ok))
                        }
                    },
                    confirmButton = {},
                    text = {
                        formErrors?.let {
                            Text(text = it.joinToString("\n\n"))
                        }
                    }
                )
            }

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
                        label = { Text(text = stringResource(id = R.string.schedule_name)) },
                        singleLine = true
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                ) {
                    DatePicker(
                        label = stringResource(id = R.string.start_date),
                        value = startDate,
                        onSelectDate = { startDate = it },
                        dateConstraints = newScheduleViewModel.getDateConstraints(startDate, true),
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .padding(10.dp)
                    )
                    TimePicker(
                        label = stringResource(id = R.string.start_time),
                        hourValue = startHour,
                        minuteValue = startMinute,
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
                        value = endDate,
                        onSelectDate = { endDate = it },
                        dateConstraints = newScheduleViewModel.getDateConstraints(startDate, false),
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .padding(10.dp)
                    )
                    TimePicker(
                        label = stringResource(id = R.string.end_time),
                        hourValue = endHour,
                        minuteValue = endMinute,
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
                    Button(
                        onClick = {
                            selectedName = ""
                            startDate = null
                            startHour = 12
                            startMinute = 0
                            endDate = null
                            endHour = 12
                            endMinute = 0
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = stringResource(id = R.string.reset))
                    }
                }
            }
        }
    )
}