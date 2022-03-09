package fi.metropolia.intellicircumstances.view.measure

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.util.Log
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
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.bluetooth.ConnectionState
import fi.metropolia.intellicircumstances.component.RuuviTagSearcher
import fi.metropolia.intellicircumstances.component.animation.ShowAnimation
import fi.metropolia.intellicircumstances.extension.round
import fi.metropolia.intellicircumstances.navigation.NavigationRoutes
import fi.metropolia.intellicircumstances.ui.theme.Red100
import fi.metropolia.intellicircumstances.ui.theme.Red300
import fi.metropolia.intellicircumstances.ui.theme.Red500
import fi.metropolia.intellicircumstances.util.PermissionUtil
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun MeasureSpaceView(
    navController: NavController,
    spaceId: Long?,
    spaceName: String?,
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
    var showNoPermsAlert by rememberSaveable { mutableStateOf(false) }

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
            scope.launch {
                scaffoldState
                    .snackbarHostState
                    .showSnackbar(
                        message = context.getString(R.string.connected_to_ruuvi)
                    )
            }
        } else if (ruuviConnectionState == ConnectionState.DISCONNECTED) {
            scope.launch {
                scaffoldState
                    .snackbarHostState
                    .showSnackbar(
                        message = context.getString(R.string.disconnected_from_ruuvi)
                    )
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
                title = {
                    Text(
                        text = stringResource(id = R.string.measure),
                        modifier = Modifier.semantics { heading() })
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (permissionsGiven) {
                                measureSpaceViewModel.startScan()
                                showBluetoothLeScanner = true
                            } else {
                                showNoPermsAlert = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.BluetoothSearching,
                            contentDescription = stringResource(
                                id = R.string.contentdesc_add_tag, spaceName ?: stringResource(
                                    id = R.string.this_space
                                )
                            )
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
                        Icon(
                            imageVector = Icons.Filled.Schedule,
                            contentDescription = stringResource(
                                id = R.string.add_new, stringResource(id = R.string.schedule)
                            )
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.NavigateBefore,
                            contentDescription = stringResource(
                                id = R.string.back_to, stringResource(id = R.string.space_selection)
                            )
                        )
                    }
                }
            )
        },
        content = {
            val ruuviTagDevices by measureSpaceViewModel.ruuviTagDevices.observeAsState()
            var selectedOption by rememberSaveable { mutableStateOf<Int?>(null) }

            if (showBluetoothLeScanner) {
                RuuviTagSearcher(
                    ruuviTagDevices = ruuviTagDevices,
                    onDismissRequest = {
                        showBluetoothLeScanner = false
                        measureSpaceViewModel.stopScan()
                    },
                    onConnect = {
                        if (spaceId != null && selectedOption != null) {
                            ruuviTagDevices?.let {
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

            if (showNoPermsAlert) {
                Dialog(onDismissRequest = {
                    showNoPermsAlert = false
                    PermissionUtil.checkBluetoothPermissions(
                        context,
                        onCheckPermissions = { permissionsLauncher.launch(it) })
                },
                    content = {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.81f)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(10.dp),
                            ) {
                                Row(modifier = Modifier.padding(12.dp)) {
                                    Text(stringResource(id = R.string.missing_perms))
                                }
                                Row {
                                    ShowAnimation("animations/14651-error-animation.json")
                                }
                                Row(
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    TextButton(onClick = {
                                        showNoPermsAlert = false
                                        PermissionUtil.checkBluetoothPermissions(
                                            context,
                                            onCheckPermissions = { permissionsLauncher.launch(it) })
                                    }) {
                                        Text(text = stringResource(id = R.string.cancel))
                                    }
                                }
                            }
                        }
                    }
                )
            }

            if (ruuviConnectionState == ConnectionState.CONNECTED) {
                val sensorData = measureSpaceViewModel.sensorData.observeAsState()

                var tabIndex by remember { mutableStateOf(0) }
                val tabTitles = listOf(
                    stringResource(id = R.string.temp),
                    stringResource(id = R.string.humid),
                    stringResource(id = R.string.pressure)
                )
                val units = stringArrayResource(id = R.array.units)
                val pagerState = rememberPagerState()
                val coroutineScope = rememberCoroutineScope()

                Column {
                    TabRow(selectedTabIndex = tabIndex) {
                        tabTitles.forEachIndexed { index, title ->
                            Tab(selected = tabIndex == index,
                                onClick = {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                },
                                text = { Text(text = title) })
                        }
                    }
                    HorizontalPager(
                        count = 3,
                        state = pagerState,
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {


                            tabIndex = pagerState.currentPage
                            var selectedValue: Double? = null
                            when (tabIndex) {
                                0 -> {
                                    selectedValue = sensorData.value?.temperature
                                }
                                1 -> {
                                    selectedValue = sensorData.value?.humidity
                                }
                                2 -> {
                                    selectedValue = sensorData.value?.airPressure
                                }
                            }
                            Text(
                                text = "${tabTitles[tabIndex]} ${selectedValue?.round(2) ?: "-"} ${units[tabIndex]}",
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            ShowGraph(measureSpaceViewModel, MeasureType.values()[tabIndex])
                        }
                    }
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    ShowAnimation("animations/14651-error-animation.json")
                    Text(
                        text = stringResource(id = R.string.no_connection),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.body1
                    )
                }
            }

            LaunchedEffect(Unit) {
                permissionsGiven =
                    PermissionUtil.checkBluetoothPermissions(
                        context,
                        onCheckPermissions = { permissionsLauncher.launch(it) }
                    )

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
        })
}


@Composable
private fun ShowGraph(viewModel: MeasureSpaceViewModel, type: MeasureType) {
    val points = viewModel.points.observeAsState()

    val ySteps = 6
    Log.d("DBG", "${points.value}")
    if (points.value != null) {
        LineGraph(
            plot = LinePlot(
                listOf(
                    LinePlot.Line(
                        points.value!!.toList()[type.value],
                        connection = LinePlot.Connection(color = Red300),
                        intersection = LinePlot.Intersection(color = Red500),
                        null,
                    )
                ),
                xAxis = LinePlot.XAxis(unit = 1F, roundToInt = true),
                yAxis = LinePlot.YAxis(
                    steps = ySteps,
                    roundToInt = false,
                    content = { min, offset, max ->
                        Text(text = min.toDouble().round(2).toString())
                        for (step in 1 until ySteps - 1) {
                            Text(
                                text = min.toDouble().plus(offset * step).round(2)
                                    .toString()
                            )
                        }
                        Text(text = max.toDouble().round(2).toString())
                    }
                ),
                grid = LinePlot.Grid(color = Red100, steps = ySteps),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
        )
    }

    TextButton(onClick = {
        viewModel.clearGraph()
    }) {
        Text(text = stringResource(id = R.string.clear_graph))
    }
}

enum class MeasureType(val value: Int) {
    TEMPERATURE(0),
    HUMIDITY(1),
    AIRPRESSURE(2),
}