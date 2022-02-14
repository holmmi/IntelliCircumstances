package fi.metropolia.intellicircumstances.view.spaces

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.bluetooth.decoder.FoundTag
import fi.metropolia.intellicircumstances.database.RuuviDevice
import fi.metropolia.intellicircumstances.ui.theme.Red100
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@Composable
fun SpacesView(
    navController: NavController,
    propertyId: Long?,
    spacesViewModel: SpacesViewModel = viewModel(),
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var spaceName by rememberSaveable { mutableStateOf("") }
    var spaceNameIsEmpty by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showSearchScreen by rememberSaveable { mutableStateOf(false) }
    var selectedSpace by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedTag by rememberSaveable { mutableStateOf<String?>("") }
    var selectedTagInfo by rememberSaveable { mutableStateOf<FoundTag?>(null) }

    Scaffold(
        topBar = {
            if (propertyId != null) {
                val spaces = spacesViewModel.getSpaces(propertyId).observeAsState()
                TopAppBar(
                    title = { Text(text = spaces.value?.property?.name ?: "-") },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Filled.NavigateBefore,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        },
        content = {
            if (showAddDialog && propertyId != null) {
                AlertDialog(
                    title = { Text(text = stringResource(id = R.string.create_space)) },
                    onDismissRequest = { showAddDialog = false },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = spaceName,
                                onValueChange = { spaceName = it },
                                label = { Text(text = stringResource(id = R.string.space_name)) },
                                singleLine = true,
                                isError = spaceNameIsEmpty
                            )
                            if (spaceNameIsEmpty) {
                                Text(
                                    text = stringResource(id = R.string.error_empty_field),
                                    color = MaterialTheme.colors.error,
                                    style = MaterialTheme.typography.caption,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                            Button(
                                modifier = Modifier.padding(top = 16.dp),
                                onClick = {
                                    showAddDialog = false
                                    showSearchScreen = true
                                }) {
                                Text(
                                    if (selectedTag.isNullOrBlank()) stringResource(id = R.string.add_tag) else {
                                        "${stringResource(id = R.string.selected_tag)}: ${selectedTagInfo?.name}"
                                    }
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (spaceName.isEmpty()) {
                                    spaceNameIsEmpty = true
                                } else {
                                    GlobalScope.launch(Dispatchers.IO) {
                                        val id = spacesViewModel.addDevice(
                                            RuuviDevice(
                                                macAddress = selectedTagInfo?.id ?: "",
                                                name = selectedTagInfo?.name,
                                                description = "RuuviTag ${selectedTagInfo?.name} of space $spaceName"
                                            )
                                        )
                                        spacesViewModel.addSpace(
                                            propertyId, spaceName, id
                                        )
                                    }
                                    spaceNameIsEmpty = false
                                    showAddDialog = false
                                    spaceName = ""
                                    selectedTag = ""
                                    selectedTagInfo = null
                                }
                            }
                        ) {
                            Text(text = stringResource(id = R.string.add))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                )
            }

            if (showSearchScreen) {
                val devices = spacesViewModel.foundTags.observeAsState()
                spacesViewModel.startScanning()

                AlertDialog(
                    onDismissRequest = { showSearchScreen = false },
                    text = {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(200.dp)
                        ) {
                            item {
                                Text(stringResource(id = R.string.found_devices), fontSize = 30.sp)
                            }
                            items(devices.value ?: listOf()) {
                                Row(
                                    modifier = Modifier.padding(15.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Filled.Bluetooth,
                                        "Bluetooth ${stringResource(R.string.icon)}"
                                    )
                                    RadioButton(selected = selectedTag == it.id, onClick = {
                                        selectedTag = it.id
                                        selectedTagInfo = it
                                    })
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            "${it.name}",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text("MAC: ${it.id}")
                                        Text("${it.rssi} dBm")
                                    }
                                }
                                Divider(color = MaterialTheme.colors.onSurface, thickness = 1.dp)
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showSearchScreen = false
                                showAddDialog = true
                                spacesViewModel.stopScanning()
                            }
                        ) {
                            Text(text = stringResource(id = R.string.confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showSearchScreen = false
                            spacesViewModel.stopScanning()
                        }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                )
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    text = {
                        Text(text = stringResource(id = R.string.delete_space))
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                spacesViewModel.deleteSpace(selectedSpace!!)
                            }
                        ) {
                            Text(text = stringResource(id = R.string.delete))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {
                if (propertyId != null) {
                    val spaces = spacesViewModel.getSpaces(propertyId).observeAsState()
                    spaces.value?.let {
                        if (it.spaces.isEmpty()) {
                            Text(
                                text = stringResource(id = R.string.no_spaces),
                                style = MaterialTheme.typography.h5,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(it.spaces) { space ->
                                    Card(
                                        backgroundColor = Red100,
                                        elevation = 8.dp,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                        ) {
                                            Column {
                                                Text(
                                                    text = space.name,
                                                    style = MaterialTheme.typography.h6
                                                )
                                            }
                                            Spacer(modifier = Modifier.weight(1.0f))
                                            Row {
                                                IconButton(
                                                    onClick = {
                                                        selectedSpace = space.id
                                                        showDeleteDialog = true
                                                    }
                                                ) {
                                                    Icon(Icons.Outlined.Delete, null)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        }
    )
}