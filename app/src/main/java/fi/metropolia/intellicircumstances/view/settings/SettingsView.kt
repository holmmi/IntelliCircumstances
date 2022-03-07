package fi.metropolia.intellicircumstances.view.settings

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Devices
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.navigation.NavigationRoutes

@Composable
fun SettingsView(navController: NavController) {
    val context = LocalContext.current
    val keys = stringArrayResource(id = R.array.pref_keys)
    val settingsViewModel = SettingsViewModel(
        context.applicationContext as Application, keys
    )
    val languages = stringArrayResource(id = R.array.languages)
    var language by remember { mutableStateOf(context.resources.getString(R.string.choose_language)) }
    var expanded by remember { mutableStateOf(false) }
    val isDarkMode = settingsViewModel.isDarkMode.observeAsState()


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.settings)) },
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = {
                            settingsViewModel.toggleDarkMode(keys[0])
                        })
                        .padding(bottom = 16.dp, top = 16.dp),
                ) {
                    Text(text = stringResource(id = R.string.dark_mode))
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(checked = isDarkMode.value!!, onCheckedChange = {
                        settingsViewModel.toggleDarkMode(keys[0])
                    })
                }
                Divider()
                Column() {
                    Text(
                        text = stringResource(id = R.string.language),
                        style = MaterialTheme.typography.caption
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = { expanded = true })
                            .padding(bottom = 16.dp, top = 16.dp),
                    ) {
                        Text(text = language)
                        Spacer(modifier = Modifier.weight(1f))
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            languages.forEach {
                                DropdownMenuItem(onClick = { /* Change lang */
                                    language = it
                                    expanded = false
                                }) {
                                    Text(it)
                                }
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = stringResource(
                                id = R.string.contentdesc_lang_dropdown
                            )
                        )
                    }
                }
                Divider()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(BorderStroke(1.dp, SolidColor(Color.LightGray)))
                        .padding(bottom = 16.dp, top = 16.dp)
                        .clickable(onClick = { navController.navigate(NavigationRoutes.DEVICES) }),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Devices,
                        tint = MaterialTheme.colors.secondary,
                        contentDescription = stringResource(id = R.string.contentdesc_added_devices)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = stringResource(id = R.string.added_devices))
                }
            }
        }
    )
}