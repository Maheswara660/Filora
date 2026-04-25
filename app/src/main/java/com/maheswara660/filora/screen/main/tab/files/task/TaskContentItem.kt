package com.maheswara660.filora.screen.main.tab.files.task

import com.maheswara660.filora.screen.main.tab.files.holder.ContentHolder

data class TaskContentItem(
    val content: ContentHolder,
    val relativePath: String,
    var status: TaskContentStatus
)