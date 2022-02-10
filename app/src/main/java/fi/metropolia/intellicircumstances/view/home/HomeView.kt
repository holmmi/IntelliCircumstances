package fi.metropolia.intellicircumstances.view.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.navigation.NavigationRoutes

@Composable
fun HomeView(navController: NavController) {
    Scaffold(
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
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