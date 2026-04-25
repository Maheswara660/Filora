package com.maheswara660.filora.screen.main.tab.apps.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Search
import com.maheswara660.filora.common.ui.CustomSegmentedControl
import com.maheswara660.filora.common.ui.CustomLoader
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.maheswara660.filora.R
import com.maheswara660.filora.common.block
import com.maheswara660.filora.common.emptyString
import com.maheswara660.filora.common.isNot
import com.maheswara660.filora.common.ui.Space
import com.maheswara660.filora.screen.main.tab.apps.AppsTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsTabContentView(tab: AppsTab) {
    var showSortMenu by remember { mutableStateOf(false) }

    LaunchedEffect(tab.id) {
        if (tab.appsList.isEmpty()) {
            tab.fetchInstalledApps()
        }
    }

    LaunchedEffect(tab.selectedChoice, tab.sortOption) {
        tab.updateAppsList()
    }

    // App Info Dialog
    if (tab.previewAppDialog isNot null) {
        AppInfoBottomSheet(
            app = tab.previewAppDialog!!,
            onDismiss = { tab.previewAppDialog = null }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            // Apps List
            LazyColumn {
                item {
                    Space(8.dp)
                }

                items(tab.appsList, key = { it.packageName }) { app ->
                    AppListItem(
                        app = app,
                        onClick = { tab.previewAppDialog = app }
                    )
                }

                item {
                    Space(size = 150.dp)
                }
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            Color.Transparent,
                            MaterialTheme.colorScheme.surfaceContainerLowest
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.End
        ) {
            AnimatedVisibility(!tab.isSearchPanelOpen) {
                FloatingActionButton(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = {
                        tab.isSearchPanelOpen = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null
                    )
                }
            }

            // Filter and Sort Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Filter Segments
                // Filter Segments
                val options = listOf(
                    stringResource(R.string.user_apps),
                    stringResource(R.string.system_apps),
                    stringResource(R.string.all)
                )
                CustomSegmentedControl(
                    items = options,
                    defaultSelectedItemIndex = tab.selectedChoice,
                    onItemSelection = { index ->
                        tab.selectedChoice = index
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                )

                Space(size = 8.dp)

                // Sort Button
                Box {
                    FloatingActionButton(
                        modifier = Modifier.padding(16.dp),
                        onClick = {
                            showSortMenu = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.Sort,
                            contentDescription = null
                        )
                    }

                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.name)) },
                            onClick = {
                                tab.sortOption = AppsTab.SortOption.NAME
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.size)) },
                            onClick = {
                                tab.sortOption = AppsTab.SortOption.SIZE
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.install_date)) },
                            onClick = {
                                tab.sortOption = AppsTab.SortOption.INSTALL_DATE
                                showSortMenu = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.update_date)) },
                            onClick = {
                                tab.sortOption = AppsTab.SortOption.UPDATE_DATE
                                showSortMenu = false
                            }
                        )
                    }
                }
            }

            AnimatedVisibility(tab.isSearchPanelOpen) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp)
                        .height(54.dp)
                        .clip(RoundedCornerShape(99.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { tab.isSearchPanelOpen = false }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextField(
                        modifier = Modifier.weight(1f),
                        value = tab.searchQuery,
                        onValueChange = { tab.searchQuery = it },
                        placeholder = {
                            Text(
                                modifier = Modifier.alpha(0.6f),
                                text = stringResource(R.string.search_query),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = MaterialTheme.colorScheme.primary
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { tab.performSearch() })
                    )

                    AnimatedVisibility(visible = tab.searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                if (tab.isSearching) {
                                    tab.isSearching = false
                                } else {
                                    tab.searchQuery = ""
                                    tab.isSearching = false
                                    tab.performSearch()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (tab.isSearching) Icons.Rounded.Pause
                                else Icons.Rounded.Cancel,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Loading Indicator
        AnimatedVisibility(
            visible = tab.isLoading || tab.isSearching,
            modifier = Modifier.align(Alignment.Center)
        ) {
            CustomLoader()
        }
    }
}