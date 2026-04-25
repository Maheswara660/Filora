package com.maheswara660.filora.screen.main.tab.files.ui

import androidx.compose.foundation.layout.ColumnScope
import com.maheswara660.filora.App.Companion.globalClass
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.maheswara660.filora.screen.main.tab.files.FilesTab
import com.maheswara660.filora.screen.main.tab.files.holder.LocalFileHolder
import com.maheswara660.filora.screen.main.tab.files.ui.BreadcrumbBar
import com.maheswara660.filora.screen.main.tab.files.ui.FilesList
import com.maheswara660.filora.screen.main.tab.files.ui.BottomOptionsBar
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.ApkPreviewDialog
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.BookmarksDialog
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.CreateNewFileDialog
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.DeleteConfirmationDialog
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.FileCompressionDialog
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.FileOptionsMenuDialog
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.FilePropertiesDialog
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.FileSortingMenuDialog
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.FileViewConfigDialog
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.ImportPrefsDialog
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.OpenWithAppListDialog
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.RenameDialog
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.SearchDialog
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.TaskConflictDialog
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.TaskPanel
import com.maheswara660.filora.screen.main.tab.files.ui.dialog.TaskRunningDialog

@Composable
fun ColumnScope.FilesTabContentView(tab: FilesTab) {
    Dialogs(tab)
    val isRecycleBin = tab.activeFolder.uniquePath.startsWith(globalClass.recycleBinDir.uniquePath)

    val categoryPagerState = if (tab.showCategories && globalClass.preferencesManager.enableCategorySwiping) {
        rememberPagerState(
            initialPage = tab.selectedCategory?.let { tab.categories.indexOf(it) + 1 } ?: 0
        ) {
            tab.categories.size + 1
        }
    } else null

    if (!isRecycleBin) {
        BreadcrumbBar(tab, categoryPagerState)
        HorizontalDivider(modifier = Modifier, thickness = 1.dp)
    }

    if (tab.showCategories && categoryPagerState != null && globalClass.preferencesManager.enableCategorySwiping) {
        LaunchedEffect(categoryPagerState.currentPage) {
            val newCategory = if (categoryPagerState.currentPage == 0) null else tab.categories[categoryPagerState.currentPage - 1]
            if (tab.selectedCategory != newCategory) {
                tab.selectedCategory = newCategory
                tab.reloadFiles()
            }
        }

        HorizontalPager(
            state = categoryPagerState,
            modifier = Modifier.weight(1f)
        ) {
            FilesList(tab)
        }
    } else {
        FilesList(tab)
    }
    
    BottomOptionsBar(tab)
}


@Composable
fun Dialogs(tab: FilesTab) {
    val dialogsState = tab.dialogsState.collectAsState()

    ApkPreviewDialog(
        show = dialogsState.value.showApkDialog && tab.targetFile != null && tab.targetFile is LocalFileHolder,
        tab = tab,
        onDismissRequest = { tab.toggleApkDialog(null) }
    )

    OpenWithAppListDialog(
        show = dialogsState.value.showOpenWithDialog && tab.targetFile != null && tab.targetFile!! is LocalFileHolder,
        tab = tab,
        onDismissRequest = { tab.toggleOpenWithDialog(false) }
    )

    BookmarksDialog(
        show = dialogsState.value.showBookmarkDialog,
        tab = tab,
        onDismissRequest = { tab.toggleBookmarksDialog(false) }
    )

    SearchDialog(
        show = dialogsState.value.showSearchPenal,
        tab = tab,
        onDismissRequest = { tab.toggleSearchPenal(false) }
    )

    FileSortingMenuDialog(
        show = dialogsState.value.showSortingMenu,
        tab = tab,
        onDismissRequest = {
            tab.toggleSortingMenu(false)
            tab.reloadFiles()
        }
    )

    FileViewConfigDialog(
        show = dialogsState.value.showViewConfigDialog,
        tab = tab,
        onDismissRequest = {
            tab.toggleViewConfigDialog(false)
            tab.updateDisplayConfig()
        }
    )

    DeleteConfirmationDialog(
        show = dialogsState.value.showConfirmDeleteDialog,
        tab = tab,
        onDismissRequest = { tab.toggleDeleteConfirmationDialog(false) }
    )

    CreateNewFileDialog(
        show = dialogsState.value.showCreateNewFileDialog,
        tab = tab,
        onDismissRequest = { tab.toggleCreateNewFileDialog(false) }
    )

    RenameDialog(
        show = dialogsState.value.showRenameDialog && tab.selectedFiles.isNotEmpty(),
        tab = tab,
        onDismissRequest = { tab.toggleRenameDialog(false) }
    )

    FileCompressionDialog(
        show = dialogsState.value.showNewZipFileDialog && tab.compressTaskHolder != null,
        tab = tab,
        onDismissRequest = { tab.toggleCompressTaskDialog(null) }
    )

    FileOptionsMenuDialog(
        show = dialogsState.value.showFileOptionsDialog && tab.targetFile != null,
        tab = tab,
        onDismissRequest = { tab.toggleFileOptionsMenu(null, false) }
    )

    FilePropertiesDialog(
        show = dialogsState.value.showFileProperties,
        tab = tab,
        onDismissRequest = { tab.toggleFilePropertiesDialog(false) }
    )

    TaskPanel(
        show = dialogsState.value.showTasksPanel,
        tab = tab,
        onDismissRequest = { tab.toggleTasksPanel(false) }
    )

    ImportPrefsDialog(
        show = dialogsState.value.showImportPrefsDialog,
        tab = tab,
        onDismissRequest = { tab.toggleImportPrefsDialog(null) }
    )

    TaskRunningDialog()

    TaskConflictDialog()
}