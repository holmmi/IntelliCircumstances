package fi.metropolia.intellicircumstances.component

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.material.datepicker.MaterialDatePicker
import fi.metropolia.intellicircumstances.extension.getActivity
import java.text.DateFormat
import java.util.*

@Composable
fun DatePicker(label: String,
               modifier: Modifier = Modifier,
               initialDate: Long? = null,
               onSelectDate: (Long) -> Unit) {
    var selectedDate by rememberSaveable { mutableStateOf(initialDate) }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val context = LocalContext.current

    LaunchedEffect(isPressed) {
        if (isPressed) {
            showDatePicker(
                context.getActivity(),
                selectedDate
            ) { date ->
                onSelectDate(date)
                selectedDate = date
            }
        }
    }

    OutlinedTextField(
        value = selectedDate?.let { getLocalizedDate(it) } ?: "",
        onValueChange = {},
        readOnly = true,
        singleLine = true,
        label = { Text(text = label) },
        trailingIcon = {
            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null)
        },
        interactionSource = interactionSource,
        modifier = modifier
    )
}

private fun showDatePicker(activity: AppCompatActivity?, selectedDate: Long?, onSelectDate: (Long) -> Unit) {
    activity?.let {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(selectedDate)
            .build()
        datePicker.addOnPositiveButtonClickListener(onSelectDate)
        datePicker.show(it.supportFragmentManager, datePicker.toString())
    }
}

private fun getLocalizedDate(date: Long): String {
    val dateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
    return dateFormat.format(Date(date))
}