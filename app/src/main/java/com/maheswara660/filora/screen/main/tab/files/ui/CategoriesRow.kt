package com.maheswara660.filora.screen.main.tab.files.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.pager.PagerState
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.maheswara660.filora.R
import com.maheswara660.filora.common.limitLength
import com.maheswara660.filora.screen.main.tab.files.FilesTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesRow(tab: FilesTab, pagerState: PagerState? = null) {
    val coroutineScope = rememberCoroutineScope()
    val selectedIndex = pagerState?.currentPage ?: 0

    PrimaryScrollableTabRow(
        selectedTabIndex = selectedIndex,
        edgePadding = 0.dp,
        divider = {},
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Tab(
            selected = selectedIndex == 0,
            onClick = {
                if (pagerState != null) {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                } else {
                    tab.selectedCategory = null
                    tab.reloadFiles()
                }
            },
            text = {
                Text(text = stringResource(R.string.all))
            }
        )
        tab.categories.forEachIndexed { index, category ->
            Tab(
                selected = selectedIndex == index + 1,
                onClick = {
                    if (pagerState != null) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index + 1)
                        }
                    } else {
                        tab.selectedCategory = category
                        tab.reloadFiles()
                    }
                },
                text = {
                    Text(text = category.name.limitLength(18))
                }
            )
        }
    }
}