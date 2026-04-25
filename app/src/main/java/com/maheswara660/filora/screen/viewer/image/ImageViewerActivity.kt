package com.maheswara660.filora.screen.viewer.image

import android.net.Uri
import androidx.activity.compose.setContent
import com.maheswara660.filora.common.ui.SafeSurface
import com.maheswara660.filora.screen.viewer.ViewerActivity
import com.maheswara660.filora.screen.viewer.ViewerInstance
import com.maheswara660.filora.screen.viewer.image.ui.ImageViewerScreen
import com.maheswara660.filora.theme.FiloraTheme

class ImageViewerActivity : ViewerActivity() {
    override fun onCreateNewInstance(uri: Uri, uid: String): ViewerInstance {
        return ImageViewerInstance(uri, uid)
    }

    override fun onReady(instance: ViewerInstance) {
        setContent {
            FiloraTheme {
                SafeSurface(enableStatusBarsPadding = false) {
                    ImageViewerScreen(instance)
                }
            }
        }
    }
}