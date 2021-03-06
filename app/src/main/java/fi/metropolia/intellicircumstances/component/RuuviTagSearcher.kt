package fi.metropolia.intellicircumstances.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.bluetooth.RuuviTagDevice
import fi.metropolia.intellicircumstances.component.animation.ShowAnimation

@Composable
fun RuuviTagSearcher(
    ruuviTagDevices: List<RuuviTagDevice>?,
    onDismissRequest: () -> Unit,
    onConnect: () -> Unit,
    onSelect: (Int) -> Unit
) {
    var selectedOption by rememberSaveable { mutableStateOf<Int?>(null) }
    Dialog(
        onDismissRequest = onDismissRequest,
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Text(
                        text = stringResource(
                            id = (if (ruuviTagDevices.isNullOrEmpty()) {
                                R.string.searching
                            } else {
                                R.string.connect_to_ruuvi_tag
                            })
                        ),
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    if (ruuviTagDevices.isNullOrEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.9f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            ShowAnimation("animations/55186-bluetooth.json")
                        }
                    }
                    ruuviTagDevices?.let {
                        Column(
                            Modifier
                                .selectableGroup()
                                .verticalScroll(rememberScrollState())
                                .fillMaxHeight()
                                .padding(bottom = 16.dp)
                        ) {
                            it.forEachIndexed { index, device ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = (selectedOption == index),
                                            onClick = {
                                                selectedOption = index
                                                onSelect(index)
                                            },
                                            role = Role.RadioButton
                                        )
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    RadioButton(
                                        selected = (selectedOption == index),
                                        onClick = null
                                    )
                                    Icon(
                                        Icons.Filled.Bluetooth,
                                        "Bluetooth ${stringResource(R.string.icon)}"
                                    )
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceEvenly,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = device.name,
                                            style = MaterialTheme.typography.body1
                                        )
                                        Text(
                                            text = "${device.rssi} dBm",
                                            style = MaterialTheme.typography.body2
                                        )
                                        Text(
                                            text = "MAC: ${device.macAddress}",
                                            style = MaterialTheme.typography.body2
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1.0f))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        TextButton(onClick = onDismissRequest) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                        TextButton(
                            onClick = {
                                onDismissRequest()
                                onConnect()
                            },
                            enabled = selectedOption != null
                        ) {
                            Text(text = stringResource(id = R.string.connect))
                        }
                    }
                }
            }
        }
    )
}