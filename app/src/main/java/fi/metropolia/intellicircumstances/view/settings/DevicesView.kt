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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.component.animation.ShowAnimation
import fi.metropolia.intellicircumstances.ui.theme.Red100
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun DevicesView(
    navController: NavController,
    devicesViewModel: DevicesViewModel = viewModel()
) {
    val propertiesWithSpaces = devicesViewModel.propertiesWithSpaces.observeAsState()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var selectedSpace: Long? by rememberSaveable { mutableStateOf(null) }
    var selectedTag: Long? by rememberSaveable { mutableStateOf(null) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.added_devices)) },
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
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    text = {
                        Text(text = stringResource(id = R.string.delete_device))
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                selectedTag?.let { tag -> devicesViewModel.deleteDevice(tag) }
                            }
                        ) {
                            Text(text = stringResource(id = R.string.delete))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                )
            }
            if (propertiesWithSpaces.value.isNullOrEmpty()) {
                Column() {
                    Text(
                        text = stringResource(
                            id = R.string.no_properties,
                        ),
                        style = MaterialTheme.typography.h5,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    )
                    ShowAnimation("animations/55213-blue-house.json")
                }
            } else {
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
                                            IconButton(onClick = {
                                                selectedSpace = space.id
                                                showDeleteDialog = true
                                                selectedTag = tag.value!!.id
                                            }) {
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
                        if (property.spaces.isNullOrEmpty()) {
                            Column() {
                                Text(stringResource(id = R.string.no_spaces))
                            }
                        } else {
                            SpaceTabs(tabs = tabContent)
                        }
                    }
                }
            }
        }
    )
}

//made with help from this guide: https://www.rockandnull.com/jetpack-compose-swipe-pager/
@ExperimentalPagerApi
@Composable
private fun SpaceTabs(tabs: List<TabContent>) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = tabs.map { it.tabName }
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    Column {
        TabRow(selectedTabIndex = selectedTab,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(
                        pagerState,
                        tabPositions
                    )
                )
            }) {
            tabTitles.forEachIndexed { index, title ->
                Tab(selected = selectedTab == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                        selectedTab = index
                    },
                    text = { Text(text = title) })
            }
        }
        HorizontalPager(
            count = tabs.size,
            state = pagerState,
        ) { tabIndex ->
            selectedTab = tabIndex
            tabs[selectedTab].content()
        }
    }
}


private data class TabContent(
    val tabName: String,
    val content: @Composable() () -> Unit,
    val tabId: Long? = null,
)
