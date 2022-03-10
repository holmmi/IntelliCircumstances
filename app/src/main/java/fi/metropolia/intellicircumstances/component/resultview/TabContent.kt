package fi.metropolia.intellicircumstances.component.resultview

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.madrapps.plot.line.DataPoint
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.database.Circumstance

@Composable
fun TabContent(
    measurementTab: MeasurementTab,
    circumstances: List<Circumstance>,
    dateFormatter: (Long) -> String
) {
    var showInformation by rememberSaveable { mutableStateOf(false) }
    var xCoordinate by rememberSaveable { mutableStateOf<Float?>(null) }
    var dataPoint by remember { mutableStateOf<DataPoint?>(null) }

    val data = circumstances.mapNotNull {
        when (measurementTab) {
            MeasurementTab.HumidityTab -> it.humidity
            MeasurementTab.PressureTab -> it.airPressure
            MeasurementTab.TemperatureTab -> it.temperature
        }
    }
    if (data.isNotEmpty()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
        ) {
            val unit = when (measurementTab) {
                MeasurementTab.HumidityTab -> "%"
                MeasurementTab.PressureTab -> "hPa"
                MeasurementTab.TemperatureTab -> "\u00B0C"
            }

            Box {
                ShowDataGraph(
                    values = data,
                    onSelectionStart = { showInformation = true },
                    onSelectionEnd = { showInformation = false },
                    onSelection = { x, dataP ->
                        xCoordinate = x
                        dataPoint = dataP
                    }
                )
                if (showInformation && dataPoint != null) {
                    ShowInformation(
                        xCoordinate = xCoordinate!!,
                        value = dataPoint!!.y,
                        unit = unit,
                        date = dateFormatter(circumstances[dataPoint!!.x.toInt()].time!!)
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp)
            ) {
                Text(
                    text = String.format(stringResource(R.string.min_value), data.minOrNull(), unit),
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = String.format(stringResource(R.string.max_value), data.maxOrNull(), unit),
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(8.dp)
                )
                Text(
                    text = String.format(stringResource(R.string.avg_value), data.average(), unit),
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}