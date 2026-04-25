package com.maheswara660.filora.screen.viewer.audio.model

import android.graphics.Bitmap
import com.maheswara660.filora.App.Companion.globalClass
import com.maheswara660.filora.R

data class AudioMetadata(
    val title: String = globalClass.getString(R.string.unknown_title),
    val artist: String = globalClass.getString(R.string.unknown_artist),
    val album: String = globalClass.getString(R.string.unknown_album),
    val duration: Long = 0L,
    val albumArt: Bitmap? = null
)