package com.maheswara660.filora.screen.viewer.video

import android.net.Uri
import androidx.activity.compose.setContent
import com.maheswara660.filora.screen.viewer.ViewerActivity
import com.maheswara660.filora.screen.viewer.ViewerInstance
import com.maheswara660.filora.screen.viewer.video.ui.VideoPlayerScreen
import com.maheswara660.filora.theme.FiloraTheme

class VideoPlayerActivity : ViewerActivity() {
    override fun onCreateNewInstance(
        uri: Uri,
        uid: String
    ): ViewerInstance {
        return VideoPlayerInstance(uri, uid)
    }

    override fun onReady(instance: ViewerInstance) {
        val videoPlayerInstance = instance as VideoPlayerInstance
        setContent {
            FiloraTheme {
                VideoPlayerScreen(
                    videoUri = videoPlayerInstance.uri,
                    videoPlayerInstance = videoPlayerInstance,
                    onBackPressed = { finish() }
                )
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPictureInPictureMode(android.app.PictureInPictureParams.Builder().build())
    }
}