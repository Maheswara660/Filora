package com.maheswara660.filora.screen.main

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import com.maheswara660.filora.common.ui.CustomLoader
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.maheswara660.filora.App.Companion.globalClass
import com.maheswara660.filora.R
import com.maheswara660.filora.base.BaseActivity
import com.maheswara660.filora.common.isNot
import com.maheswara660.filora.common.showMsg
import com.maheswara660.filora.common.toJson
import com.maheswara660.filora.common.ui.SafeSurface
import com.maheswara660.filora.screen.main.tab.apps.AppsTab
import com.maheswara660.filora.screen.main.tab.apps.ui.AppsTabContentView
import com.maheswara660.filora.screen.main.tab.files.FilesTab
import com.maheswara660.filora.screen.main.tab.files.holder.LocalFileHolder
import com.maheswara660.filora.screen.main.tab.files.ui.FilesTabContentView
import com.maheswara660.filora.screen.main.tab.home.HomeTab
import com.maheswara660.filora.screen.main.tab.home.ui.HomeTabContentView
import com.maheswara660.filora.screen.main.ui.AppInfoDialog
import com.maheswara660.filora.screen.main.ui.JumpToPathDialog
import com.maheswara660.filora.screen.main.ui.SaveTextEditorFilesDialog
import com.maheswara660.filora.screen.main.ui.StartupTabsSettingsScreen
import com.maheswara660.filora.screen.main.ui.TabLayout
import com.maheswara660.filora.screen.main.ui.Toolbar
import com.maheswara660.filora.screen.main.ui.SearchHeader
import com.maheswara660.filora.screen.main.ui.FiloraHeader
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.SearchDialog
import android.os.Environment
import com.maheswara660.filora.theme.FiloraTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Intent
import com.maheswara660.filora.screen.about.AboutActivity
import java.io.File
import kotlin.math.abs
import kotlin.math.exp

class MainActivity : BaseActivity() {
    private val HOME_SCREEN_SHORTCUT_EXTRA_KEY = "filePath"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkPermissions()
    }

    override fun onPermissionGranted() {
        setContent {
            FiloraTheme {
                SafeSurface {
                    val coroutineScope = rememberCoroutineScope()
                    val mainActivityManager = globalClass.mainActivityManager
                    val mainActivityState by mainActivityManager.state.collectAsState()
                    var backPressedOnce by remember { mutableStateOf(false) }

                    BackHandler {
                        coroutineScope.launch {
                            if (mainActivityManager.canExit()) {
                                if (!globalClass.preferencesManager.confirmBeforeAppClose || backPressedOnce) {
                                    finish()
                                } else {
                                    backPressedOnce = true
                                    showMsg(R.string.press_back_again)
                                    launch {
                                        delay(2000)
                                        backPressedOnce = false
                                    }
                                }
                            }
                        }
                    }

                    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                        mainActivityManager.onResume()
                    }

                    LifecycleEventEffect(Lifecycle.Event.ON_STOP) {
                        mainActivityManager.onStop()
                    }

                    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
                        if (globalClass.preferencesManager.rememberLastSession)
                            mainActivityManager.saveSession()
                    }

                    LaunchedEffect(Unit) {
                        mainActivityManager.checkForUpdate()
                        if (hasIntent()) {
                            handleIntent()
                        } else {
                            if (mainActivityState.tabs.isEmpty()) {
                                mainActivityManager.loadStartupTabs()
                            }
                        }
                    }

                    JumpToPathDialog(
                        show = mainActivityState.showJumpToPathDialog,
                        onDismiss = { mainActivityManager.toggleJumpToPathDialog(false) }
                    )


                    SaveTextEditorFilesDialog(
                        show = mainActivityState.showSaveEditorFilesDialog,
                        isSaving = mainActivityState.isSavingFiles,
                        onDismiss = { mainActivityManager.toggleSaveEditorFilesDialog(false) },
                        onIgnore = {
                            mainActivityManager.ignoreTextEditorFiles()
                            finish()
                        },
                        onSave = { mainActivityManager.saveTextEditorFiles { finish() } }
                    )

                    StartupTabsSettingsScreen(mainActivityState.showStartupTabsDialog) {
                        mainActivityManager.toggleStartupTabsDialog(false)
                        globalClass.preferencesManager.startupTabs = it.toJson()
                    }

                    val universalSearchTab = remember {
                        FilesTab(LocalFileHolder(Environment.getExternalStorageDirectory()))
                    }

                    SearchDialog(
                        show = mainActivityState.showSearchDialog,
                        tab = universalSearchTab,
                        onDismissRequest = { mainActivityManager.toggleSearchDialog(false) }
                    )

                    Column(Modifier.fillMaxSize()) {
                        val activeTab = mainActivityManager.getActiveTab()
                        if (activeTab is HomeTab) {
                            SearchHeader(
                                onSearchClick = {
                                    mainActivityManager.toggleSearchDialog(true)
                                },
                                onBackClick = {
                                    coroutineScope.launch {
                                        mainActivityManager.canExit()
                                    }
                                },
                                onAddNewTab = { mainActivityManager.addTabAndSelect(HomeTab()) },
                                onMenuClick = {}, // Handled by MoreOptionsButton
                                hasNewUpdate = mainActivityState.hasNewUpdate,
                                bottomPadding = if (mainActivityState.tabs.size <= 1) 4.dp else 0.dp
                            )

                        } else {
                            FiloraHeader(
                                title = mainActivityState.title,
                                onBackClick = { 
                                    coroutineScope.launch {
                                        mainActivityManager.canExit() 
                                    }
                                },
                                onAddNewTab = { mainActivityManager.addTabAndSelect(HomeTab()) },
                                onMenuClick = {}, // Handled by MoreOptionsButton
                                hasNewUpdate = mainActivityState.hasNewUpdate
                            )
                        }

                        AnimatedVisibility(visible = mainActivityState.tabs.size > 1) {
                            TabLayout(
                                tabLayoutState = mainActivityState.tabLayoutState,
                                tabs = mainActivityState.tabs,
                                selectedTabIndex = mainActivityState.selectedTabIndex,
                                onReorder = { from, to -> mainActivityManager.reorderTabs(from, to) }
                            )
                        }

                        TabsPager(mainActivityState)

                    }
                }
            }
        }
    }

    @Composable
    fun ColumnScope.TabsPager(state: MainActivityState) {
        val manager = globalClass.mainActivityManager

        if (state.tabs.isEmpty()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomLoader()
            }
        } else {
            val pagerState = rememberPagerState(initialPage = state.selectedTabIndex) {
                state.tabs.size
            }

            LaunchedEffect(state.selectedTabIndex) {
                if (pagerState.currentPage isNot state.selectedTabIndex) {
                    pagerState.scrollToPage(state.selectedTabIndex)
                }
            }

            LaunchedEffect(pagerState.currentPage) {
                snapshotFlow { pagerState.currentPage }.collect { page ->
                    if (page isNot state.selectedTabIndex) {
                        manager.selectTabAt(page, true)
                    }
                    state.tabLayoutState.animateScrollToItem(page)
                }
            }

            var overscrollAmount by remember { mutableFloatStateOf(0f) }
            val threshold = 100f
            val animationScope = rememberCoroutineScope()
            var isAnimatingBack by remember { mutableStateOf(false) }

            fun applyExponentialTension(current: Float, addition: Float, threshold: Float): Float {
                return if (current < threshold) {
                    current + addition
                } else {
                    val excess = current - threshold
                    val decayFactor = exp(-excess / threshold * 2f)
                    current + (addition * decayFactor)
                }
            }

            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        val isLastPage = pagerState.currentPage == pagerState.pageCount - 1

                        // If we're overscrolling and user drags back (positive x),
                        // consume the scroll and reduce overscroll manually
                        if (isLastPage && overscrollAmount > 0 && available.x > 0 && !isAnimatingBack) {
                            val consumeAmount = minOf(available.x, overscrollAmount)
                            overscrollAmount = maxOf(0f, overscrollAmount - consumeAmount)

                            // Consume exactly what we used to reduce overscroll
                            return Offset(consumeAmount, 0f)
                        }

                        return Offset.Zero
                    }

                    override fun onPostScroll(
                        consumed: Offset,
                        available: Offset,
                        source: NestedScrollSource
                    ): Offset {
                        val isLastPage = pagerState.currentPage == pagerState.pageCount - 1

                        // Only handle overscroll if we're on last page and scrolling right (negative x)
                        if (isLastPage && available.x < 0 && source == NestedScrollSource.UserInput && !isAnimatingBack) {
                            val availableAmount = abs(available.x)

                            overscrollAmount = applyExponentialTension(
                                overscrollAmount,
                                availableAmount,
                                threshold
                            )

                            return Offset(available.x, 0f) // Consume the scroll
                        }

                        return Offset.Zero
                    }

                    override suspend fun onPreFling(available: Velocity): Velocity {
                        val isLastPage = pagerState.currentPage == pagerState.pageCount - 1

                        if (isLastPage && overscrollAmount > 0 && !isAnimatingBack) {
                            // Trigger action when releasing overscroll
                            if (overscrollAmount > threshold) {
                                manager.addTabAndSelect(HomeTab())
                            }

                            // Animate overscroll back to 0
                            isAnimatingBack = true
                            animationScope.launch {
                                animate(
                                    initialValue = overscrollAmount,
                                    targetValue = 0f,
                                    animationSpec = tween(
                                        durationMillis = 200,
                                        easing = FastOutSlowInEasing
                                    )
                                ) { value, _ ->
                                    overscrollAmount = value
                                }
                                isAnimatingBack = false
                            }

                            // Consume all velocity to prevent pager interference
                            return Velocity.Zero
                        }

                        return Velocity.Zero
                    }
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .weight(1f)
                    .nestedScroll(nestedScrollConnection),
                key = { state.tabs[it].id }
            ) { index ->
                key(index) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .graphicsLayer {
                                translationX = -overscrollAmount
                            },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (state.tabs.isNotEmpty()) {
                            val currentTab = state.tabs[index]
                            when (currentTab) {
                                is FilesTab -> {
                                    FilesTabContentView(currentTab)
                                }

                                is HomeTab -> {
                                    HomeTabContentView(currentTab)
                                }

                                is AppsTab -> {
                                    AppsTabContentView(currentTab)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        globalClass.viewersManager.releaseAll()
        globalClass.performCleanOnExit()
    }

    private fun hasIntent(): Boolean {
        return intent isNot null && intent!!.hasExtra(HOME_SCREEN_SHORTCUT_EXTRA_KEY)
    }

    private fun handleIntent() {
        intent?.let {
            if (it.hasExtra(HOME_SCREEN_SHORTCUT_EXTRA_KEY)) {
                globalClass.mainActivityManager.jumpToFile(
                    file = LocalFileHolder(File(it.getStringExtra(HOME_SCREEN_SHORTCUT_EXTRA_KEY)!!)),
                    context = this
                )
                intent = null
            }
        }
    }
}