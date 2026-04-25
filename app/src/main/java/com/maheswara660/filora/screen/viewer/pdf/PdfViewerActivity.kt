package com.maheswara660.filora.screen.viewer.pdf

import android.net.Uri
import androidx.activity.compose.setContent
import com.maheswara660.filora.App.Companion.globalClass
import com.maheswara660.filora.R
import com.maheswara660.filora.common.ui.SafeSurface
import com.maheswara660.filora.screen.viewer.ViewerActivity
import com.maheswara660.filora.screen.viewer.ViewerInstance
import com.maheswara660.filora.screen.viewer.pdf.ui.PdfViewerContent
import com.maheswara660.filora.theme.FiloraTheme
import net.engawapg.lib.zoomable.ExperimentalZoomableApi

class PdfViewerActivity : ViewerActivity() {
    override fun onCreateNewInstance(uri: Uri, uid: String): ViewerInstance {
        return PdfViewerInstance(uri, uid)
    }

    @OptIn(ExperimentalZoomableApi::class)
    override fun onReady(instance: ViewerInstance) {
        if (instance is PdfViewerInstance) {
            setContent {
                FiloraTheme {
                    SafeSurface(false) {
                        PdfViewerContent(
                            instance = instance,
                            onBackPress = { onBackPressedDispatcher.onBackPressed() }
                        )
                    }
                }
            }
        } else {
            globalClass.showMsg(getString(R.string.invalid_pdf))
            finish()
        }
    }
}