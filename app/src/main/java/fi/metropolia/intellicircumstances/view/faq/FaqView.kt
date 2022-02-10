package fi.metropolia.intellicircumstances.view.faq

import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.navigation.NavigationRoutes

@Composable
fun FaqView(navController: NavController) {
    Scaffold(
        topBar = { TopBar(navController) },
        content = {
            Text(NavigationRoutes.FAQ)
        }
    )
}

@Composable
private fun TopBar(navController: NavController) {
    TopAppBar(
        title = { Text(stringResource(R.string.faq)) },
        navigationIcon = {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(imageVector = Icons.Filled.NavigateBefore, contentDescription = null)
            }
        }
    )
}