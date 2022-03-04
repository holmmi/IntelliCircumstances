package fi.metropolia.intellicircumstances.view.home

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun SharedScheduleView(
    navController: NavController,
    uuid: String?,
    sharedScheduleViewModel: SharedScheduleViewModel = viewModel()
) {
    val sharedSchedule by sharedScheduleViewModel.sharedSchedule.observeAsState()

    LaunchedEffect(Unit) {
        uuid?.let { sharedScheduleViewModel.getScheduleByUuid(it) }
    }

    Scaffold(
        topBar = {
             TopAppBar(
                 navigationIcon = {
                     IconButton(onClick = { navController.navigateUp() }) {
                         Icon(imageVector = Icons.Filled.NavigateBefore, contentDescription = null)
                     }
                 },
                 title = { Text(text = sharedSchedule?.name ?: "") }
             )
        },
        content = {

        }
    )
}