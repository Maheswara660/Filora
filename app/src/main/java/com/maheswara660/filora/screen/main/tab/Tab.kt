package com.maheswara660.filora.screen.main.tab

import com.maheswara660.filora.App.Companion.globalClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class Tab {
    var isCreated = false
    var parentTab: Tab? = null

    abstract val id: Int
    abstract val header: String

    open fun onTabRemoved() {}
    open fun onTabStopped() {}
    open fun onTabResumed() {}
    open fun onTabStarted() {
        isCreated = true
    }

    abstract suspend fun getTitle(): String
    abstract suspend fun getSubtitle(): String

    open fun onBackPressed(): Boolean = false

    fun requestHomeToolbarUpdate() {
        if (globalClass.mainActivityManager.getActiveTab() == this) {
            CoroutineScope(Dispatchers.IO).launch {
                globalClass.mainActivityManager.updateHomeToolbar(
                    title = getTitle(),
                    subtitle = getSubtitle()
                )
            }
        }
    }
}