package com.maheswara660.filora.screen.viewer.image

import android.net.Uri
import com.maheswara660.filora.screen.viewer.ViewerInstance

class ImageViewerInstance(
    override val uri: Uri,
    override val id: String
) : ViewerInstance {
    override fun onClose() {

    }
}