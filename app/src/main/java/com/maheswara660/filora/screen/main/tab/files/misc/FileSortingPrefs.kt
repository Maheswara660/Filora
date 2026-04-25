package com.maheswara660.filora.screen.main.tab.files.misc

import com.maheswara660.filora.screen.main.tab.files.misc.SortingMethod.SORT_BY_NAME

data class FileSortingPrefs(
    val sortMethod: Int = SORT_BY_NAME,
    val showFoldersFirst: Boolean = true,
    val reverseSorting: Boolean = false,
    val applyForThisFileOnly: Boolean = true
)