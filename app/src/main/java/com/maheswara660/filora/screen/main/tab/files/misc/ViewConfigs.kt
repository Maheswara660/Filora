package com.maheswara660.filora.screen.main.tab.files.misc

import com.maheswara660.filora.App.Companion.globalClass

data class ViewConfigs(
    val viewType: ViewType = ViewType.LIST,
    val columnCount: Int = 1,
    val cropThumbnails: Boolean = false,
    val galleryMode: Boolean = false,
    val hideMediaNames: Boolean = false,
    val itemSize: Int = globalClass.preferencesManager.itemSize
)