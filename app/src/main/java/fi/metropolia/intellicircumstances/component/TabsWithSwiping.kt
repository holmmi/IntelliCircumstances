package fi.metropolia.intellicircumstances.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState

//from this guide: https://www.rockandnull.com/jetpack-compose-swipe-pager/
@ExperimentalPagerApi
@Composable
fun TabsWithSwiping(tabs: List<TabContent>) {
    var tabIndex by remember { mutableStateOf(0) }
    val tabTitles = tabs.map { it.tabName }
    val pagerState = rememberPagerState()
    Column {
        TabRow(selectedTabIndex = tabIndex,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(
                        pagerState,
                        tabPositions
                    )
                )
            }) {
            tabTitles.forEachIndexed { index, title ->
                Tab(selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    text = { Text(text = title) })
            }
        }
        HorizontalPager(
            count = tabs.size,
            state = pagerState,
        ) { tabIndex ->
            tabs[tabIndex].content()
        }
    }
}

data class TabContent(
    val tabName: String,
    val content: @Composable() () -> Unit,
    val tabId: Long? = null,
)