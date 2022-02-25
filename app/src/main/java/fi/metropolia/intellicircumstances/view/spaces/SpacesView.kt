package fi.metropolia.intellicircumstances.view.spaces

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.bluetooth.RuuviTagDevice
import fi.metropolia.intellicircumstances.ui.theme.Red100
import fi.metropolia.intellicircumstances.component.RuuviTagSearcher
import fi.metropolia.intellicircumstances.util.PermissionUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SpacesView(
    navController: NavController,
    propertyId: Long?,
    spacesViewModel: SpacesViewModel = viewModel()
) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var spaceName by rememberSaveable { mutableStateOf("") }
    var spaceNameIsEmpty by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var showSearchScreen by rememberSaveable { mutableStateOf(false) }
    var selectedSpace by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedTagInfo by rememberSaveable { mutableStateOf<RuuviTagDevice?>(null) }
    var newSpace by rememberSaveable { mutableStateOf<Long?>(null) }

    var permissionsGiven by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    val permissionsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            it.values.all { value -> value }
        }

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
                        }
                    },
                    confirmButton = {
                        val coroutineScope = rememberCoroutineScope()
                        TextButton(
                            onClick = {
                                if (spaceName.isEmpty()) {
                                    spaceNameIsEmpty = true
                                } else {
                                    coroutineScope.launch(Dispatchers.IO) {
                                        val id = spacesViewModel.addSpace(
                                            propertyId, spaceName
                                        )
                                        newSpace = id
                                        selectedTagInfo?.let { device ->
                                            spacesViewModel.addDevice(
                                                id,
                                                device
                                            )
                                        }

                                        spaceNameIsEmpty = false
                                        showAddDialog = false
                                        spaceName = ""
                                        selectedTagInfo = null
                                    }
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
                val devices = spacesViewModel.ruuviTagDevices.observeAsState()
                var selectedOption by rememberSaveable { mutableStateOf<Int?>(null) }

                RuuviTagSearcher(
                    ruuviTagDevices = devices.value,
                    onDismissRequest = {
                        showSearchScreen = false
                        spacesViewModel.stopScan()
                   },
                    onConnect = {
                        if (newSpace != null && selectedOption != null) {
                            devices.value?.let {
                                spacesViewModel.addDeviceAndConnect(
                                    newSpace!!,
                                    it[selectedOption!!]
                                )
                            }
                        }
                    },
                    onSelect = { selectedOption = it }
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
                                                        if (permissionsGiven) {
                                                            spacesViewModel.startScan()
                                                            showSearchScreen = true
                                                        } else {
                                                            permissionsLauncher
                                                                .launch(
                                                                    arrayOf(
                                                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                                                        Manifest.permission.ACCESS_FINE_LOCATION
                                                                    )
                                                                )
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.BluetoothSearching,
                                                        contentDescription = null
                                                    )
                                                }
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

    LaunchedEffect(Unit) {
        permissionsGiven =
            PermissionUtil.checkBluetoothPermissions(context, onCheckPermissions = { permissionsLauncher.launch(it) })
    }
}