package fi.metropolia.intellicircumstances.view.measure

import android.app.Application
import android.util.Log
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.bluetooth.BluetoothService

@Composable
fun MeasureSpaceView(
    navController: NavController,
    spaceId: Long?,
    btService: BluetoothService
) {
    val measureViewModel: MeasureViewModel = MeasureViewModel(LocalContext.current.applicationContext as Application, btService, spaceId)
    val tagData = measureViewModel.tagData.observeAsState()

    Scaffold(
        topBar = {
            TopAppBar() {

            }
        },
        content = {

            Text("${tagData.value?.temperature}")
        }
    )
}