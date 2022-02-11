package fi.metropolia.intellicircumstances.view.spaces

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.NavigateNext
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.ui.theme.Typography

@Composable
fun PropertiesView(navController: NavController, propertiesViewModel: PropertiesViewModel = viewModel()) {
    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var propertyName by rememberSaveable { mutableStateOf("") }
    var propertyNameIsEmpty by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var selectedProperty by rememberSaveable { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
             TopAppBar(title = { Text(text = stringResource(id = R.string.properties)) })
        },
        content = {
            if (showAddDialog) {
                AlertDialog(
                    onDismissRequest = { showAddDialog = false },
                    title = { Text(text = stringResource(id = R.string.add_property)) },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = propertyName,
                                onValueChange = { propertyName = it },
                                maxLines = 1,
                                label = { Text(text = stringResource(id = R.string.property_name)) },
                                isError = propertyNameIsEmpty,
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .fillMaxWidth()
                            )
                            if (propertyNameIsEmpty) {
                                Text(
                                    text = stringResource(id = R.string.error_empty_field),
                                    color = MaterialTheme.colors.error,
                                    style = MaterialTheme.typography.caption,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (propertyName.isEmpty()) {
                                    propertyNameIsEmpty = true
                                } else {
                                    showAddDialog = false
                                    propertyNameIsEmpty = false
                                    propertiesViewModel.addProperty(propertyName)
                                    propertyName = ""
                                }
                            }
                        ) {
                            Text(text = stringResource(id = R.string.add))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddDialog = false }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                )
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(text = stringResource(id = R.string.are_you_sure)) },
                    text = { Text(text = stringResource(id = R.string.delete_property)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                propertiesViewModel.deleteProperty(selectedProperty!!)
                                showDeleteDialog = false
                            }
                        ) {
                            Text(text = stringResource(id = R.string.confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text(text = stringResource(id = R.string.cancel))
                        }
                    }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)) {
                val properties = propertiesViewModel.properties.observeAsState()
                properties.value?.let {
                    if (it.isEmpty()) {
                        Text(
                            text = stringResource(id = R.string.no_properties),
                            style = Typography.h5,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    } else {
                        LazyColumn(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(it) { property ->
                                Card(
                                    backgroundColor = Color.White,
                                    elevation = 8.dp,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = property.name,
                                                style = Typography.h6
                                            )
                                        }
                                        Spacer(modifier = Modifier.weight(1.0f))
                                        Row {
                                            IconButton(
                                                onClick = {
                                                    selectedProperty = property.id
                                                    showDeleteDialog = true
                                                }
                                            ) {
                                                Icon(Icons.Outlined.Delete, null)
                                            }
                                            IconButton(onClick = { }) {
                                                Icon(Icons.Outlined.NavigateNext, null)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            }
        }
    )
}