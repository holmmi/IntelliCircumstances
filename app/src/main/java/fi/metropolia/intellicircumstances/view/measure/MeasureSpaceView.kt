package fi.metropolia.intellicircumstances.view.measure

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BluetoothSearching
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.bluetooth.ConnectionState
import fi.metropolia.intellicircumstances.component.RuuviTagSearcher
import fi.metropolia.intellicircumstances.extension.round
import fi.metropolia.intellicircumstances.navigation.NavigationRoutes
import fi.metropolia.intellicircumstances.ui.theme.Red100
import fi.metropolia.intellicircumstances.util.PermissionUtil
import kotlinx.coroutines.launch


@Composable
fun MeasureSpaceView(
    navController: NavController,
    spaceId: Long?,
    measureSpaceViewModel: MeasureSpaceViewModel = viewModel()
) {
    val context = LocalContext.current
    var showBluetoothLeScanner by rememberSaveable { mutableStateOf(false) }
    var permissionsGiven by rememberSaveable { mutableStateOf(false) }

    val permissionsLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            permissionsGiven = it.values.all { value -> value }
        }

    val enableBluetoothLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                if (spaceId != null) {
                    measureSpaceViewModel.connectDevice(spaceId)
                }
            }
        }
    val bluetoothEnabled by measureSpaceViewModel.isBluetoothEnabled().asLiveData().observeAsState()

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

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
        } else if (ruuviConnectionState == ConnectionState.CONNECTED) {
            Toast.makeText(context, R.string.connected_to_ruuvi, Toast.LENGTH_SHORT).show()
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
                                permissionsLauncher.launch(
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
                            navController.navigate(
                                NavigationRoutes.SCHEDULES.replace(
                                    "{spaceId}",
                                    spaceId.toString()
                                )
                            )
                        }
                    ) {
                        Icon(imageVector = Icons.Filled.Schedule, contentDescription = null)
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
            val ruuviTagDevices = measureSpaceViewModel.ruuviTagDevices.observeAsState()
            var selectedOption by rememberSaveable { mutableStateOf<Int?>(null) }
            if (showBluetoothLeScanner) {
                RuuviTagSearcher(
                    ruuviTagDevices = ruuviTagDevices.value,
                    onDismissRequest = { showBluetoothLeScanner = false },
                    onConnect = {
                        if (spaceId != null && selectedOption != null) {
                            ruuviTagDevices.value?.let {
                                measureSpaceViewModel.addDeviceAndConnect(
                                    spaceId,
                                    it[selectedOption!!]
                                )
                            }
                        }
                    },
                    onSelect = { selectedOption = it }
                )
            }

            if (ruuviConnectionState == ConnectionState.CONNECTED) {
                val sensorData = measureSpaceViewModel.sensorData.observeAsState()

                var tabIndex by remember { mutableStateOf(0) }
                val tabTitles = listOf(
                    stringResource(id = R.string.temp),
                    stringResource(R.string.humid),
                    stringResource(R.string.pressure)
                )
                Column() {
                    TabRow(selectedTabIndex = tabIndex) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(selected = tabIndex == index,
                                onClick = { tabIndex = index },
                                text = { Text(text = title) })
                        }
                    }
                    Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        when (tabIndex) {
                            0 -> {
                                Text(
                                    "${stringResource(id = R.string.temp)} ${
                                        sensorData.value?.temperature?.round(
                                            2
                                        )
                                    } C"
                                )
                                ShowGraph(measureSpaceViewModel, MeasureType.TEMPERATURE)
                            }
                            1 -> {
                                Text(
                                    "${stringResource(id = R.string.humid)} ${
                                        sensorData.value?.humidity?.round(
                                            2
                                        )
                                    } %"
                                )
                                ShowGraph(measureSpaceViewModel, MeasureType.HUMIDITY)
                            }

                            2 -> {
                                Text(
                                    "${stringResource(id = R.string.pressure)} ${
                                        sensorData.value?.airPressure?.round(
                                            2
                                        )
                                    } hPa"
                                )
                                ShowGraph(measureSpaceViewModel, MeasureType.AIRPRESSURE)
                            }
                        }
                    }
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionsGiven =
            PermissionUtil.checkPerms(context, onCheckPerms = { permissionsLauncher.launch(it) })

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

@Composable
fun ShowGraph(viewModel: MeasureSpaceViewModel, type: MeasureType) {
    val data = viewModel.sensorDataList.observeAsState()
    var points by rememberSaveable { mutableStateOf<List<DataPoint>?>(null) }

    if (type == MeasureType.TEMPERATURE) {
        points = data.value?.map {
            DataPoint(it.first.toFloat(), it.second.temperature?.toFloat() ?: 0.0F)
        }
    }
    if (type == MeasureType.HUMIDITY) {
        points = data.value?.map {
            DataPoint(it.first.toFloat(), it.second.humidity?.toFloat() ?: 0.0F)
        }
    }
    if (type == MeasureType.AIRPRESSURE) {
        points = data.value?.map {
            DataPoint(it.first.toFloat(), it.second.airPressure?.toFloat() ?: 0.0F)
        }
    }
    if (points != null) {
        LineGraph(
            plot = LinePlot(
                listOf(
                    LinePlot.Line(
                        points!!,
                        LinePlot.Connection(color = MaterialTheme.colors.primary),
                        null,
                        null,
                    )
                ),
                grid = LinePlot.Grid(MaterialTheme.colors.secondaryVariant, steps = 4),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            onSelection = { xLine, points ->

            }
        )
    }
    TextButton(onClick = { viewModel.clearSensorDataList() }) {
        Text(text = stringResource(id = R.string.clear_graph))
    }
}

enum class MeasureType(val value: String) {
    TEMPERATURE("temperature"),
    HUMIDITY("humidity"),
    AIRPRESSURE("airPressure"),
}