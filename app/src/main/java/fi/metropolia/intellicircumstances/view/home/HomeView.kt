package fi.metropolia.intellicircumstances.view.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.navigation.NavigationRoutes

@Composable
fun HomeView(navController: NavController, homeViewModel: HomeViewModel = viewModel()) {
    val firebaseSchedules by homeViewModel.getSharedSchedules().observeAsState()

    Scaffold(
        topBar = {
             TopAppBar(
                 title = { Text(text = stringResource(id = R.string.home)) }
             )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                firebaseSchedules?.let { schedules ->
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.75f)
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.community_data),
                                style = MaterialTheme.typography.h4
                            )
                        }

                        items(schedules) { schedule ->
                            Column(Modifier.fillMaxWidth()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text(
                                            text = schedule.name ?: "",
                                            style = MaterialTheme.typography.subtitle1,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                        Text(
                                            text = String.format(
                                                stringResource(R.string.schedule_between),
                                                homeViewModel.getFormattedDate(schedule.startDate ?: 0),
                                                homeViewModel.getFormattedDate(schedule.endDate ?: 0)
                                            ),
                                            style = MaterialTheme.typography.subtitle2
                                        )
                                    }
                                    IconButton(onClick = { /*TODO: Navigate to view shared results*/ }) {
                                        Icon(imageVector = Icons.Filled.NavigateNext, contentDescription = null)
                                    }
                                }
                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1.0f))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp)
                ) {
                    TextButton(onClick = { navController.navigate(NavigationRoutes.FAQ) }) {
                        Text(stringResource(R.string.need_assistance))
                    }
                }
            }
        }
    )
}