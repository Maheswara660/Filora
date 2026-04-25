package com.maheswara660.filora.screen.main.tab.files.holder

import android.graphics.Bitmap
import com.maheswara660.filora.common.randomString

data class OpenWithActivityHolder(
    val id: String = String.randomString(8),
    val label: String,
    val name: String,
    val packageName: String,
    val icon: Bitmap?
)