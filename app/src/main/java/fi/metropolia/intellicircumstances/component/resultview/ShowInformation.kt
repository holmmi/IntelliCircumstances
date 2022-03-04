package fi.metropolia.intellicircumstances.component.resultview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.ui.theme.Red200

@Composable
fun ShowInformation(xCoordinate: Float, value: Float, unit: String, date: String) {
    var xSubtraction by rememberSaveable { mutableStateOf(0) }
    Surface(
        color = Red200,
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .height(60.dp)
            .width(200.dp)
            .onGloballyPositioned {
                xSubtraction = it.size.width / 2
            }
            .graphicsLayer(translationX = xCoordinate - xSubtraction)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = String.format(
                    stringResource(R.string.value_at),
                    value,
                    unit,
                    date
                ),
                style = MaterialTheme.typography.caption
            )
        }
    }
}