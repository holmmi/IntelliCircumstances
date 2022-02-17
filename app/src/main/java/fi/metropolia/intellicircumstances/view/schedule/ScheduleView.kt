package fi.metropolia.intellicircumstances.view.schedule

import android.util.Log
import android.widget.TimePicker
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.bluetooth.BtWorker
import java.util.*


@Composable
fun ScheduleView(navController: NavController, spaceId: Long?) {
    var date by rememberSaveable { mutableStateOf(Date()) }
    var hour by rememberSaveable { mutableStateOf(Date()) }

    var showCalendar by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    Scaffold(
        content = {
            Column(modifier = Modifier.padding(15.dp)) {
                Button(onClick = { showCalendar = true }) {
                    Text(text = "Show calendar")
                }
                if (showCalendar) {
                    DatePicker(
                        onDateSelected = { date = it },
                        onDismissRequest = { showCalendar = false })
                }

                if (showTimePicker) {
                    HourPicker(onTimeSelected = { hour = it}, onDismissRequest = { showTimePicker = false })
                }

                Button(onClick = {
                    showTimePicker = true
                }) {
                    Text(text = "Show time picker")
                }

                Button(onClick = {
                    BtWorker.setEndTime(date = date, hour = hour)
                }) {
                    Text("Schedule")
                }
            }
        }
    )
}

