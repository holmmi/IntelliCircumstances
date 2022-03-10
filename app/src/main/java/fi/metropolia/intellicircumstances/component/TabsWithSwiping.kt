package fi.metropolia.intellicircumstances.component

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import fi.metropolia.intellicircumstances.R
import fi.metropolia.intellicircumstances.component.resultview.MeasurementTab
import fi.metropolia.intellicircumstances.component.resultview.TabContent
import fi.metropolia.intellicircumstances.database.Circumstance
import fi.metropolia.intellicircumstances.database.Schedule
import kotlinx.coroutines.launch

@ExperimentalPagerApi
@Composable
fun TabsWithSwiping(
    measurementTabs: List<MeasurementTab>,
    circumstances: List<Circumstance>?,
    schedule: Schedule?,
    dateFormatter: (Long) -> String
) {
    var selectedTab by rememberSaveable { mutableStateOf(1) }
    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TabRow(selectedTabIndex = selectedTab,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(
                        pagerState,
                        tabPositions
                    )
                )
            }) {
            measurementTabs.forEachIndexed { index, measurementTab ->
                val i = index + 1
                Tab(
                    selected = selectedTab == i,
                    onClick = {
                        selectedTab = i
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(text = stringResource(id = measurementTab.tabName)) },
                )
            }
        }
        HorizontalPager(
            count = measurementTabs.size,
            state = pagerState,
        ) {
            selectedTab = pagerState.currentPage + 1
            Column(modifier = Modifier.padding(10.dp)) {
                schedule?.let {
                    Text(
                        text = String.format(
                            stringResource(R.string.schedule_date),
                            dateFormatter(it.startDate),
                            dateFormatter(it.endDate)
                        ),
                        style = MaterialTheme.typography.subtitle1,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 10.dp, bottom = 10.dp)
                    )
                }
                circumstances?.let {
                    TabContent(
                        measurementTab = measurementTabs[selectedTab - 1],
                        circumstances = it,
                        dateFormatter = dateFormatter
                    )
                }
            }
        }
    }
}