package com.maheswara660.filora.screen.main.tab.home.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowOutward
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.History
import com.maheswara660.filora.common.ui.EmptyStateView
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import java.io.File
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.cheonjaeung.compose.grid.SimpleGridCells
import com.cheonjaeung.compose.grid.VerticalGrid
import com.google.gson.Gson
import android.os.Bundle
import com.maheswara660.filora.common.findActivity
import androidx.compose.ui.layout.*
import androidx.core.app.ActivityOptionsCompat
import com.maheswara660.filora.App.Companion.globalClass
import com.maheswara660.filora.App.Companion.logger
import com.maheswara660.filora.R
import com.maheswara660.filora.common.ui.Space
import com.maheswara660.filora.screen.main.MainActivityManager
import com.maheswara660.filora.screen.main.tab.files.FilesTab
import com.maheswara660.filora.screen.main.tab.files.coil.canUseCoil
import com.maheswara660.filora.screen.main.tab.files.holder.LocalFileHolder
import com.maheswara660.filora.screen.main.tab.files.holder.StorageDevice
import com.maheswara660.filora.screen.main.tab.files.holder.VirtualFileHolder
import com.maheswara660.filora.screen.main.tab.files.holder.VirtualFileHolder.Companion.BOOKMARKS
import com.maheswara660.filora.screen.main.tab.files.holder.VirtualFileHolder.Companion.RECENT
import com.maheswara660.filora.screen.main.tab.files.provider.StorageProvider
import com.maheswara660.filora.screen.main.tab.files.ui.FileContentIcon
import com.maheswara660.filora.screen.main.tab.home.HomeTab
import com.maheswara660.filora.screen.main.tab.home.data.HomeLayout
import com.maheswara660.filora.screen.main.tab.home.data.HomeSectionConfig
import com.maheswara660.filora.screen.main.tab.home.data.HomeSectionType
import com.maheswara660.filora.screen.main.tab.home.data.getDefaultHomeLayout
import com.maheswara660.filora.screen.main.ui.SimpleNewTabViewItem
import com.maheswara660.filora.screen.main.ui.StorageDeviceView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.HomeTabContentView(tab: HomeTab) {
    val context = LocalContext.current
    val mainActivityManager = globalClass.mainActivityManager
    val scope = rememberCoroutineScope()
    val enabledSections = remember { mutableStateListOf<HomeSectionConfig>() }

    LaunchedEffect(tab.id) {
        withContext(Dispatchers.IO) {
            async {
                tab.fetchRecentFiles()
            }
            async {
                tab.getPinnedFiles()
            }
            async {
                val config = try {
                    Gson().fromJson(
                        globalClass.preferencesManager.homeTabLayout,
                        HomeLayout::class.java
                    )
                } catch (e: Exception) {
                    logger.logError(e)
                    getDefaultHomeLayout()
                }.getSections().filter { it.isEnabled }.sortedBy { it.order }

                enabledSections.addAll(config)
            }
        }
    }

    if (tab.showCustomizeHomeTabDialog) {
        HomeLayoutSettingsScreen { sections ->
            tab.showCustomizeHomeTabDialog = false
            var isAllDisabled = false

            // Prevent disabling all sections
            if (sections.all { !it.isEnabled }) {
                isAllDisabled = true
            }

            sections.forEachIndexed { index, config ->
                config.order = index
            }

            enabledSections.apply {
                clear()
                if (isAllDisabled) {
                    addAll(getDefaultHomeLayout(true).getSections().filter { it.isEnabled }
                        .sortedBy { it.order })
                } else {
                    addAll(sections.filter { it.isEnabled }.sortedBy { it.order })
                }

            }

            scope.launch {
                if (isAllDisabled) {
                    globalClass.preferencesManager.homeTabLayout = Gson().toJson(
                        getDefaultHomeLayout(true)
                    )
                } else {
                    globalClass.preferencesManager.homeTabLayout = Gson().toJson(
                        HomeLayout(sections)
                    )
                }
            }
        }
    }

    androidx.compose.foundation.lazy.LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(enabledSections, key = { it.type.name }, contentType = { it.type }) { section ->
            when (section.type) {
                HomeSectionType.RECENT_FILES -> {
                    RecentFilesSection(tab = tab, mainActivityManager = mainActivityManager)
                }

                HomeSectionType.CATEGORIES -> {
                    CategoriesSection(tab = tab)
                }

                HomeSectionType.STORAGE -> {
                    StorageSection(mainActivityManager = mainActivityManager)
                }

                HomeSectionType.PINNED_FILES -> {
                    PinnedFilesSection(tab = tab, mainActivityManager = mainActivityManager)
                }

                HomeSectionType.OTHERS -> {
                    OthersSection(mainActivityManager = mainActivityManager)
                }

                else -> {}
            }
        }
        
        item {
            Spacer(modifier = Modifier.navigationBarsPadding())
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Composable
fun PinnedFilesSection(
    tab: HomeTab,
    mainActivityManager: MainActivityManager
) {
    val context = LocalContext.current
    val pinnedFiles = remember {
        mutableStateListOf<LocalFileHolder>().apply {
            addAll(tab.pinnedFiles)
        }
    }

    if (pinnedFiles.isNotEmpty()) {
        // Pinned files
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = 12.dp, bottom = 6.dp),
            text = stringResource(R.string.pinned_files),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier
                .animateContentSize()
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(24.dp)
                )
                .clip(RoundedCornerShape(24.dp))
        ) {
            pinnedFiles.forEachIndexed { index, it ->
                var itemBounds by remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }
                var showDeleteOption by remember(it.uid) { mutableStateOf(false) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .combinedClickable(
                                onClick = {
                                    if (showDeleteOption) {
                                        showDeleteOption = false
                                    } else {
                                        val options = if (globalClass.preferencesManager.showPopAnimation) {
                                            itemBounds?.let { bounds ->
                                                ActivityOptionsCompat.makeScaleUpAnimation(
                                                    context.findActivity()?.window?.decorView ?: return@let null,
                                                    bounds.left.toInt(),
                                                    bounds.top.toInt(),
                                                    bounds.width.toInt(),
                                                    bounds.height.toInt()
                                                ).toBundle()
                                            }
                                        } else null
                                        
                                        if (it.isFile()) {
                                            it.open(
                                                context = context,
                                                anonymous = false,
                                                skipSupportedExtensions = !globalClass.preferencesManager.useBuiltInViewer,
                                                customMimeType = null,
                                                options = options
                                            )
                                        } else {
                                            mainActivityManager.replaceCurrentTabWith(FilesTab(it))
                                        }
                                    }
                                },
                                onLongClick = {
                                    showDeleteOption = !showDeleteOption
                                }
                            )
                            .onGloballyPositioned {
                                itemBounds = it.boundsInWindow()
                            }
                            .padding(12.dp)
                            .padding(end = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var canUseCoil by remember(it.uid) {
                            mutableStateOf(canUseCoil(it))
                        }
                        if (canUseCoil) {
                            AsyncImage(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                model = ImageRequest
                                    .Builder(globalClass)
                                    .data(it)
                                    .build(),
                                filterQuality = FilterQuality.Low,
                                contentScale = ContentScale.Fit,
                                contentDescription = null,
                                onError = { canUseCoil = false }
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                FileContentIcon(it)
                            }
                        }
                        Space(size = 8.dp)
                        Text(text = it.displayName)
                    }
                    AnimatedVisibility(visible = showDeleteOption) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(60.dp)
                                .background(color = MaterialTheme.colorScheme.errorContainer)
                                .clickable {
                                    pinnedFiles.remove(it)
                                    tab.removePinnedFile(it)
                                },
                        ) {
                            Icon(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .align(Alignment.Center),
                                imageVector = Icons.Rounded.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
                if (index != tab.pinnedFiles.lastIndex) HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun RecentFilesSection(
    tab: HomeTab,
    mainActivityManager: MainActivityManager
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(top = 12.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.recent_files),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        TextButton(
            onClick = {
                mainActivityManager.replaceCurrentTabWith(
                    FilesTab(VirtualFileHolder(RECENT))
                )
            }
        ) {
            Text(text = stringResource(R.string.more))
        }
    }

    if (tab.recentFiles.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            EmptyStateView(
                icon = Icons.Rounded.History,
                message = stringResource(R.string.no_recent_files),
                modifier = Modifier.padding(16.dp)
            )
        }
    } else {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item { Space(6.dp) }

            items(tab.recentFiles, key = { it.path }) {
                var itemBounds by remember { mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }
                Column(
                    modifier = Modifier
                        .size(160.dp, 200.dp)
                        .padding(horizontal = 8.dp)
                        .combinedClickable(
                            onClick = {
                                val options = if (globalClass.preferencesManager.showPopAnimation) {
                                    itemBounds?.let { bounds ->
                                        ActivityOptionsCompat.makeScaleUpAnimation(
                                            context.findActivity()?.window?.decorView ?: return@let null,
                                            bounds.left.toInt(),
                                            bounds.top.toInt(),
                                            bounds.width.toInt(),
                                            bounds.height.toInt()
                                        ).toBundle()
                                    }
                                } else null
                                
                                it.file.open(
                                    context = context,
                                    anonymous = false,
                                    skipSupportedExtensions = !globalClass.preferencesManager.useBuiltInViewer,
                                    customMimeType = null,
                                    options = options
                                )
                            },
                            onLongClick = {
                                mainActivityManager.replaceCurrentTabWith(
                                    FilesTab(it.file)
                                )
                            }
                        )
                        .onGloballyPositioned {
                            if (itemBounds == null) {
                                itemBounds = it.boundsInWindow()
                            }
                        }
                ) {
                    var useCoil by remember(it.file.uid) {
                        mutableStateOf(canUseCoil(it.file))
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant,
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        if (useCoil) {
                            AsyncImage(
                                modifier = Modifier.fillMaxSize(),
                                model = ImageRequest
                                    .Builder(globalClass)
                                    .data(it.file)
                                    .size(300, 300) // Constrain size for performance
                                    .crossfade(true)
                                    .build(),
                                filterQuality = FilterQuality.Low,
                                contentScale = ContentScale.Crop,
                                contentDescription = null,
                                onError = { useCoil = false }
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                FileContentIcon(it.file)
                            }
                        }
                    }
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                    ) {
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = File(it.path).parentFile?.name ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            item { Space(6.dp) }

            item { Space(6.dp) }
        }
    }
}

@Composable
private fun CategoriesSection(
    tab: HomeTab
) {
    // Quick access tiles
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(top = 12.dp, bottom = 6.dp),
        text = stringResource(R.string.categories),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold
    )

    VerticalGrid(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        columns = SimpleGridCells.Fixed(2)
    ) {
        tab.getMainCategories().forEach {
            Row(
                Modifier
                    .padding(6.dp)
                    .background(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { it.onClick() }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = it.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    modifier = Modifier.basicMarquee(),
                    text = it.name,
                    maxLines = 1,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun StorageSection(
    mainActivityManager: MainActivityManager
) {
    val storageList = remember { mutableStateListOf<StorageDevice>() }

    LaunchedEffect(Unit) {
        storageList.clear()
        storageList.addAll(StorageProvider.getStorageDevices(globalClass))
    }

    if (storageList.isNotEmpty()) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = 24.dp, bottom = 6.dp),
            text = stringResource(R.string.storage),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(24.dp)
                )
                .clip(RoundedCornerShape(24.dp))
        ) {
            storageList.forEachIndexed { index, device ->
                StorageDeviceView(storageDevice = device) {
                    mainActivityManager.replaceCurrentTabWith(FilesTab(device.contentHolder))
                }
                if (index != storageList.lastIndex) HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun OthersSection(
    mainActivityManager: MainActivityManager
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(top = 24.dp, bottom = 6.dp),
        text = stringResource(R.string.others),
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Bold
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(24.dp)
            )
            .clip(RoundedCornerShape(24.dp))
    ) {
        // Bookmarks (if any)
        if (globalClass.preferencesManager.bookmarks.isNotEmpty()) {
            SimpleNewTabViewItem(
                title = stringResource(R.string.bookmarks),
                imageVector = Icons.Rounded.Bookmark
            ) {
                mainActivityManager.replaceCurrentTabWith(
                    FilesTab(VirtualFileHolder(BOOKMARKS))
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )
        }

        // Recycle Bin
        SimpleNewTabViewItem(
            title = stringResource(R.string.recycle_bin),
            imageVector = Icons.Rounded.DeleteSweep
        ) {
            mainActivityManager.replaceCurrentTabWith(FilesTab(globalClass.recycleBinDir), true)
        }

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // Jump to Path
        SimpleNewTabViewItem(
            title = stringResource(R.string.jump_to_path),
            imageVector = Icons.Rounded.ArrowOutward
        ) {
            mainActivityManager.toggleJumpToPathDialog(true)
        }
    }
}