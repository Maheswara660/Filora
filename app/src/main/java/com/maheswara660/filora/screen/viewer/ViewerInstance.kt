package com.maheswara660.filora.screen.viewer

import android.net.Uri

interface ViewerInstance {
    val uri: Uri
    val id: String

    fun onClose()
}