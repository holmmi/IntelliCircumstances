package fi.metropolia.intellicircumstances.view.measure

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.navigation.NavigationRoutes

@Composable
fun MeasureSpaceView(navController: NavController, spaceId: Long?) {
    Scaffold(
        content = {
            Text(stringResource(R.string.choose_property))
        }
    )
}