package com.maheswara660.filora.screen.main.tab.home.data

import com.maheswara660.filora.App.Companion.globalClass
import com.maheswara660.filora.R
import kotlinx.serialization.Serializable

object HomeSectionIds {
    const val RECENT_FILES = "recent_files"
    const val CATEGORIES = "categories"
    const val STORAGE = "storage"
    const val PINNED_FILES = "pinned_files"
    const val OTHERS = "others"
    const val RECYCLE_BIN = "recycle_bin"
    const val JUMP_TO_PATH = "jump_to_path"
    const val BOOKMARKS = "bookmarks"
}

fun getDefaultHomeLayout(minimalLayout: Boolean = false) = HomeLayout(
    listOf(
        HomeSectionConfig(
            id = HomeSectionIds.RECENT_FILES,
            type = HomeSectionType.RECENT_FILES,
            title = globalClass.getString(R.string.recent_files),
            isEnabled = !minimalLayout,
            order = 0
        ),
        HomeSectionConfig(
            id = HomeSectionIds.CATEGORIES,
            type = HomeSectionType.CATEGORIES,
            title = globalClass.getString(R.string.categories),
            isEnabled = !minimalLayout,
            order = 1
        ),
        HomeSectionConfig(
            id = HomeSectionIds.STORAGE,
            type = HomeSectionType.STORAGE,
            title = globalClass.getString(R.string.storage),
            isEnabled = true,
            order = 2
        ),
        HomeSectionConfig(
            id = HomeSectionIds.OTHERS,
            type = HomeSectionType.OTHERS,
            title = globalClass.getString(R.string.others),
            isEnabled = !minimalLayout,
            order = 3
        ),
        HomeSectionConfig(
            id = HomeSectionIds.PINNED_FILES,
            type = HomeSectionType.PINNED_FILES,
            title = globalClass.getString(R.string.pinned_files),
            isEnabled = !minimalLayout,
            order = 4
        )
    )
)

@Serializable
data class HomeLayout(
    private val sections: List<HomeSectionConfig>
) {
    // Adds missing sections for backward compatibility with older saved layouts
    fun getSections(): List<HomeSectionConfig> {
        var updatedSections = sections.toMutableList()
        
        // Remove legacy sections that are now part of OTHERS
        updatedSections.removeAll { it.id == HomeSectionIds.RECYCLE_BIN || it.id == HomeSectionIds.JUMP_TO_PATH || it.id == HomeSectionIds.BOOKMARKS }

        // Add Pinned Files if missing
        if (updatedSections.find { it.id == HomeSectionIds.PINNED_FILES } == null) {
            updatedSections.add(
                HomeSectionConfig(
                    id = HomeSectionIds.PINNED_FILES,
                    type = HomeSectionType.PINNED_FILES,
                    title = globalClass.getString(R.string.pinned_files),
                    isEnabled = true,
                    order = updatedSections.maxOfOrNull { it.order }?.plus(1) ?: 0
                )
            )
        }

        // Add Others if missing
        if (updatedSections.find { it.id == HomeSectionIds.OTHERS } == null) {
            updatedSections.add(
                HomeSectionConfig(
                    id = HomeSectionIds.OTHERS,
                    type = HomeSectionType.OTHERS,
                    title = globalClass.getString(R.string.others),
                    isEnabled = true,
                    order = updatedSections.maxOfOrNull { it.order }?.plus(1) ?: 0
                )
            )
        }

        return updatedSections
    }
}

@Serializable
data class HomeSectionConfig(
    val id: String,
    val type: HomeSectionType,
    val title: String,
    val isEnabled: Boolean,
    var order: Int
)

@Serializable
enum class HomeSectionType {
    RECENT_FILES,
    CATEGORIES,
    STORAGE,
    BOOKMARKS,
    RECYCLE_BIN,
    JUMP_TO_PATH,
    PINNED_FILES,
    OTHERS
}