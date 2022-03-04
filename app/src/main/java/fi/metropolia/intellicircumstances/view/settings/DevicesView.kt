package fi.metropolia.intellicircumstances.view.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.component.TabContent
import fi.metropolia.intellicircumstances.component.TabsWithSwiping
import fi.metropolia.intellicircumstances.ui.theme.Red100

@ExperimentalPagerApi
@Composable
fun DevicesView(
    navController: NavController,
    devicesViewModel: DevicesViewModel = viewModel()
) {
    val propertiesWithSpaces = devicesViewModel.propertiesWithSpaces.observeAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Filled.NavigateBefore,
                            contentDescription = stringResource(
                                id = R.string.back_to, stringResource(id = R.string.settings)
                            )
                        )
                    }
                }
            )
        },
        content = {
            LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
                items(propertiesWithSpaces.value ?: listOf()) { property ->
                    Text(
                        text = "${stringResource(id = R.string.property)} ${property.property.name}",
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier.padding(32.dp)
                    )
                    val tabContent = property.spaces.map { space ->
                        TabContent(space.name, content = {
                            val tag =
                                space.id?.let { id ->
                                    devicesViewModel.getTag(id).observeAsState()
                                }
                            Card(
                                backgroundColor = Red100,
                                elevation = 8.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .height(100.dp)
                                ) {


                                    if (tag?.value != null) {
                                        Column() {
                                            Text(
                                                text = tag.value!!.name ?: stringResource(
                                                    id = R.string.unnamed
                                                )
                                            )
                                            Text(text = tag.value!!.macAddress)
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        IconButton(onClick = { /*TODO*/ }) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = stringResource(
                                                    id = R.string.contentdesc_delete_device,
                                                    property.property.name,
                                                    space.name
                                                )
                                            )
                                        }
                                    } else {
                                        Text(text = stringResource(id = R.string.no_devices))
                                    }
                                }
                            }
                        })
                    }
                    TabsWithSwiping(tabs = tabContent)
                }
            }
        }
    )
}