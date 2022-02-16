package fi.metropolia.intellicircumstances.view.measure

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.bluetooth.ConnectionState
import kotlinx.coroutines.launch

@Composable
fun MeasureSpaceView(navController: NavController, spaceId: Long?, measureSpaceViewModel: MeasureSpaceViewModel = viewModel()) {
    var showBluetoothLeScanner by rememberSaveable { mutableStateOf(false) }
    var permissionsGiven by rememberSaveable { mutableStateOf(false) }

    val permissionsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        permissionsGiven = it.values.all { value -> value }
    }

    val enableBluetoothLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            if (spaceId != null) {
                measureSpaceViewModel.connectDevice(spaceId)
            }
        }
    }
    val bluetoothEnabled by measureSpaceViewModel.isBluetoothEnabled().asLiveData().observeAsState()

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val ruuviConnectionState by measureSpaceViewModel.ruuviConnectionState.observeAsState()
    LaunchedEffect(ruuviConnectionState) {
        if (ruuviConnectionState == ConnectionState.CONNECTION_FAILED) {
            scope.launch {
                val result = scaffoldState
                    .snackbarHostState
                    .showSnackbar(
                        message = context.getString(R.string.ruuvi_connection_failed),
                        actionLabel = context.getString(R.string.retry),
                        duration = SnackbarDuration.Indefinite
                    )
                if (result == SnackbarResult.ActionPerformed) {
                    if (spaceId != null) {
                        measureSpaceViewModel.connectDevice(spaceId)
                    }
                }
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = {
            SnackbarHost(it) { data ->
                Snackbar(
                    snackbarData = data
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.measure)) },
                actions = {
                    IconButton(
                        onClick = {
                            if (permissionsGiven) {
                                measureSpaceViewModel.scanDevices()
                                showBluetoothLeScanner = true
                            } else {
                                permissionsLauncher.launch(arrayOf(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION))
                            }
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.BluetoothSearching, contentDescription = null)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Filled.NavigateBefore, contentDescription = null)
                    }
                }
            )
        },
        content = {
            if (showBluetoothLeScanner) {
                var selectedOption by rememberSaveable { mutableStateOf<Int?>(null) }
                val ruuviTagDevices = measureSpaceViewModel.ruuviTagDevices.observeAsState()
                Dialog(
                    onDismissRequest = { showBluetoothLeScanner = false },
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
                                ruuviTagDevices.value?.let {
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
                                                        onClick = { selectedOption = index },
                                                        role = Role.RadioButton
                                                    )
                                                    .padding(horizontal = 16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = (selectedOption == index),
                                                    onClick = null
                                                )
                                                Text(
                                                    text = device.name,
                                                    style = MaterialTheme.typography.body1.merge(),
                                                    modifier = Modifier.padding(start = 16.dp)
                                                )
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
                                    TextButton(onClick = { showBluetoothLeScanner = false }) {
                                        Text(text = stringResource(id = R.string.cancel))
                                    }
                                    TextButton(
                                        onClick = {
                                            showBluetoothLeScanner = false
                                            if (spaceId != null && selectedOption != null) {
                                                ruuviTagDevices.value?.let {
                                                    measureSpaceViewModel.addDeviceAndConnect(
                                                        spaceId,
                                                        it[selectedOption!!]
                                                    )
                                                }
                                            }
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
        }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
        } else {
            permissionsGiven = true
        }

        // Try to connect to RuuviTag
        if (spaceId != null && bluetoothEnabled == true) {
            measureSpaceViewModel.connectDevice(spaceId)
        }
    }

    LaunchedEffect(bluetoothEnabled) {
        bluetoothEnabled?.let {
            if (!it) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                enableBluetoothLauncher.launch(enableBtIntent)
            }
        }
    }
}