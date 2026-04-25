package com.maheswara660.filora.screen.main.startup

import com.maheswara660.filora.common.emptyString
import java.util.UUID

data class StartupTabs(
    val tabs: List<StartupTab>
) {
    companion object {
        fun default() = StartupTabs(arrayListOf(StartupTab(StartupTabType.HOME)))
    }
}

data class StartupTab(
    val type: StartupTabType,
    val extra: String = emptyString,
    var id: UUID = UUID.randomUUID()
)

enum class StartupTabType {
    HOME,
    APPS,
    FILES
}