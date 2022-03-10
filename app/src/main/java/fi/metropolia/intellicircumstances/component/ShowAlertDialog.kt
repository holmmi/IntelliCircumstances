package fi.metropolia.intellicircumstances.component

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import fi.metropolia.intellicircumstances.R

@Composable
fun ShowAlertDialog(
    title: String? = null,
    content: String,
    visible: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (visible) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                if (!title.isNullOrEmpty()) {
                    Text(title)
                }
            },
            text = { Text(text = content) },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(text = stringResource(id = R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(text = stringResource(id = R.string.ok))
                }
            }
        )
    }
}