package fi.metropolia.intellicircumstances.view.main

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import fi.metropolia.intellicircumstances.navigation.Navigation
import fi.metropolia.intellicircumstances.ui.theme.IntelliCircumstancesTheme

@ExperimentalAnimationApi
@ExperimentalPagerApi
@Composable
fun MainView(mainViewModel: MainViewModel = viewModel()) {
    val settings by mainViewModel.getSettings().observeAsState()

    IntelliCircumstancesTheme(darkTheme = settings?.darkMode ?: isSystemInDarkTheme()) {
        Navigation()
    }
}