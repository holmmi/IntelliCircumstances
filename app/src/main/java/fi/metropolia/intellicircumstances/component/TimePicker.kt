package fi.metropolia.intellicircumstances.component

import android.text.format.DateFormat.is24HourFormat
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import fi.metropolia.intellicircumstances.extension.getActivity

@Composable
fun TimePicker(label: String,
               initialHour: Int,
               initialMinute: Int,
               modifier: Modifier = Modifier,
               onSelectTime: (hour: Int, minute: Int) -> Unit) {
    var selectedHour by rememberSaveable { mutableStateOf(initialHour) }
    var selectedMinute by rememberSaveable { mutableStateOf(initialMinute) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val context = LocalContext.current

    LaunchedEffect(isPressed) {
        if (isPressed) {
            showTimePicker(
                context.getActivity(),
                selectedHour,
                selectedMinute
            ) { hour, minute ->
                selectedHour = hour
                selectedMinute = minute
                onSelectTime(hour, minute)
            }
        }
    }

    OutlinedTextField(
        value = String.format("%02d:%02d", selectedHour, selectedMinute),
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        label = { Text(text = label) },
        trailingIcon = {
           Icon(imageVector = Icons.Default.Schedule, contentDescription = null)
        },
        interactionSource = interactionSource,
        modifier = modifier
    )
}

private fun showTimePicker(activity: AppCompatActivity?,
                           hour: Int,
                           minute: Int,
                           onSelectTime: (hour: Int, minute: Int) -> Unit) {
    activity?.let {
        val timePicker = MaterialTimePicker.Builder()
            .setHour(hour)
            .setMinute(minute)
            .setTimeFormat(
                if (is24HourFormat(it.applicationContext)) TimeFormat.CLOCK_24H
                else TimeFormat.CLOCK_12H
            )
            .build()
        timePicker.addOnPositiveButtonClickListener {
            onSelectTime(timePicker.hour, timePicker.minute)
        }
        timePicker.show(activity.supportFragmentManager, timePicker.toString())
    }
}