package com.maheswara660.filora.screen.main.tab.files.holder

data class StorageDevice(
    val contentHolder: ContentHolder,
    val title: String,
    val totalSize: Long,
    val usedSize: Long,
    val type: Int
)