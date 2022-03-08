package fi.metropolia.intellicircumstances.view.faq

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.R

@Composable
fun FaqView(navController: NavController) {
    val qList = stringArrayResource(id = R.array.questions)
    val aList = stringArrayResource(id = R.array.answers)
    Scaffold(
        topBar = { TopBar(navController) },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                qList.forEachIndexed { index, q ->
                    var selected by remember { mutableStateOf(false) }
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = {
                                selected = !selected
                            })
                        ) {
                            Text(
                                text = q,
                                style = MaterialTheme.typography.h5
                            )
                        }
                        if (selected) {
                            Row(modifier = Modifier.padding(top = 8.dp)) {
                                Text(
                                    text = aList[index],
                                    style = MaterialTheme.typography.body1,
                                )
                            }
                        }
                    }
                    Divider()
                }
            }
        }
    )
}

@Composable
private fun TopBar(navController: NavController) {
    TopAppBar(
        title = { Text(stringResource(R.string.faq)) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    imageVector = Icons.Filled.NavigateBefore, contentDescription = stringResource(
                        id = R.string.back_to, stringResource(id = R.string.home)
                    )
                )
            }
        }
    )
}