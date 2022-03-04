package fi.metropolia.intellicircumstances.component.resultview

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.madrapps.plot.line.DataPoint
import com.madrapps.plot.line.LineGraph
import com.madrapps.plot.line.LinePlot
import fi.metropolia.intellicircumstances.ui.theme.Red100
import fi.metropolia.intellicircumstances.ui.theme.Red300
import fi.metropolia.intellicircumstances.ui.theme.Red500

@Composable
fun ShowDataGraph(
    values: List<Double>,
    onSelectionStart: () -> Unit,
    onSelectionEnd: () -> Unit,
    onSelection: (Float, DataPoint) -> Unit,
) {
    LineGraph(
        plot = LinePlot(
            lines = listOf(
                LinePlot.Line(
                    dataPoints = values.mapIndexed { index, value ->
                        DataPoint(index.toFloat(), value.toFloat())
                    },
                    connection = LinePlot.Connection(color = Red300),
                    intersection = LinePlot.Intersection(color = Red500)
                )
            ),
            yAxis = LinePlot.YAxis(steps = 10, roundToInt = false),
            grid = LinePlot.Grid(color = Red100, steps = 8)
        ),
        onSelectionStart = onSelectionStart,
        onSelectionEnd = onSelectionEnd,
        onSelection = { x, dataPoints ->
            onSelection(x, dataPoints.last())
        },
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f)
    )
}