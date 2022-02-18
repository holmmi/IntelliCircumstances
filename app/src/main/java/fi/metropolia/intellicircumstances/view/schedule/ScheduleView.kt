package fi.metropolia.intellicircumstances.view.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.component.DatePicker
import fi.metropolia.intellicircumstances.component.TimePicker

@Composable
fun ScheduleView(navController: NavController, spaceId: Long?) {
    var selectedDate by rememberSaveable { mutableStateOf<Long?>(null) }
    var startHour by rememberSaveable { mutableStateOf(12) }
    var startMinute by rememberSaveable { mutableStateOf(0) }

    val context = LocalContext.current
    Scaffold(
        content = {
            // TODO: Build a schedule form as it is in Figma
            Column(modifier = Modifier.padding(10.dp)) {
                DatePicker(
                    label = "Date test",
                    onSelectDate = { selectedDate = it },
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                TimePicker(
                    label = "Time test",
                    initialHour = startHour,
                    initialMinute = startMinute,
                    modifier = Modifier.fillMaxWidth(0.5f),
                    onSelectTime = { hour, minute ->
                        startHour = hour
                        startMinute = minute
                    }
                )
            }
        }
    )
}

