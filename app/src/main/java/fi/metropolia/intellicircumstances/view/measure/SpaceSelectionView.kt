package fi.metropolia.intellicircumstances.view.measure

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NavigateNext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.component.animation.ShowAnimation
import fi.metropolia.intellicircumstances.navigation.NavigationRoutes

@Composable
fun SpaceSelectionView(
    navController: NavController,
    spaceSelectionViewModel: SpaceSelectionViewModel = viewModel()
) {
    val propertiesWithSpaces = spaceSelectionViewModel.propertiesWithSpaces.observeAsState()
    var selectedProperty by rememberSaveable { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.space_selection)) }
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                propertiesWithSpaces.value?.let {
                    Column(
                        Modifier
                            .selectableGroup()
                            .padding(bottom = 16.dp)
                    ) {
                        if (it.isNotEmpty()) {
                            it.forEach { property ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .selectable(
                                            selected = (selectedProperty == property.property.id),
                                            onClick = { selectedProperty = property.property.id },
                                            role = Role.RadioButton
                                        )
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (selectedProperty == property.property.id),
                                        onClick = null
                                    )
                                    Text(
                                        text = property.property.name,
                                        style = MaterialTheme.typography.body1.merge(),
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                        } else {
                            Column() {
                                Text(
                                    text = stringResource(id = R.string.no_properties),
                                    style = MaterialTheme.typography.h5,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                )
                                ShowAnimation("animations/55213-blue-house.json")
                            }
                        }
                    }
                    if (selectedProperty != null) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            val spaces =
                                it
                                    .first { withSpaces -> withSpaces.property.id == selectedProperty }
                                    .spaces

                            if (spaces.isNotEmpty()) {
                                item {
                                    Text(
                                        text = stringResource(id = R.string.choose_space)
                                    )
                                }
                                items(spaces) { space ->
                                    Column {
                                        Row(
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                        ) {
                                            Text(
                                                text = space.name,
                                                style = MaterialTheme.typography.h6
                                            )
                                            IconButton(
                                                onClick = {
                                                    navController.navigate(
                                                        NavigationRoutes.MEASURE_SPACE.replace(
                                                            "{spaceId}",
                                                            space.id.toString()
                                                        )
                                                    )
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Outlined.NavigateNext,
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                        Divider()
                                    }
                                }
                            } else {
                                item {
                                    Text(text = stringResource(id = R.string.no_spaces))
                                    ShowAnimation("animations/97507-room-2.json")
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}