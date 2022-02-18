package fi.metropolia.intellicircumstances.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.bluetooth.RuuviTagDevice

@Composable
fun RuuviTagSearcher(
    ruuviTagDevices: List<RuuviTagDevice>?,
    onDismissRequest: () -> Unit,
    onConnect: () -> Unit,
    onSelect: (Int) -> Unit
) {
    var selectedOption by rememberSaveable { mutableStateOf<Int?>(null) }
   // var ruuviTagDevices = viewModel.tagDevices.observeAsState()
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
                        text = stringResource(id = R.string.connect_to_ruuvi_tag),
                        style = MaterialTheme.typography.subtitle1
                    )
                    if(ruuviTagDevices.isNullOrEmpty()) {
                        Text(stringResource(id = R.string.searching))
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
                                        .height(56.dp)
                                        .selectable(
                                            selected = (selectedOption == index),
                                            onClick = {
                                                selectedOption = index
                                                onSelect(index)
                                            },
                                            role = Role.RadioButton
                                        )
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row (modifier = Modifier.fillMaxWidth(0.3f)) {
                                        Icon(
                                            Icons.Filled.Bluetooth,
                                            "Bluetooth ${stringResource(R.string.icon)}"
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        RadioButton(
                                            selected = (selectedOption == index),
                                            onClick = null
                                        )
                                    }

                                    //Spacer(modifier = Modifier.width(10.dp))
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Text(
                                                text = device.name,
                                                fontSize = 20.sp,
                                                modifier = Modifier.padding(start = 16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text("${device.rssi} dBm", fontSize = 15.sp,)
                                        }
                                        Row(modifier = Modifier.fillMaxWidth()) {
                                            Text("MAC: ${device.macAddress}")
                                        }
                                    }
                                    Divider()
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