package fi.metropolia.intellicircumstances.view.measure

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.navigation.NavigationRoutes

@Composable
fun MeasureSpaceView(
    navController: NavController,
    spaceId: Long?,
    measureViewModel: MeasureViewModel = viewModel()
) {
    val deviceValues = measureViewModel.getDevice()
    Scaffold(
        topBar = {
            TopAppBar() {

            }
        },
        content = {
            Text(stringResource(R.string.choose_property))
        }
    )
}