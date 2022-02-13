package fi.metropolia.intellicircumstances.view.measure

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.navigation.NavigationRoutes

@Composable
fun MeasureSpaceView(navController: NavController, spaceId: Long?) {
    Scaffold(
        content = {
            Text(text = NavigationRoutes.MEASURE)
        }
    )
}