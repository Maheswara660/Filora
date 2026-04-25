package com.maheswara660.filora.screen.viewer.audio

import android.net.Uri
import androidx.activity.compose.setContent
import com.maheswara660.filora.App.Companion.globalClass
import com.maheswara660.filora.screen.viewer.ViewerActivity
import com.maheswara660.filora.screen.viewer.ViewerInstance
import com.maheswara660.filora.screen.viewer.audio.ui.MusicPlayerScreen
import com.maheswara660.filora.theme.FiloraTheme

class AudioPlayerActivity : ViewerActivity() {
    override fun onCreateNewInstance(
        uri: Uri,
        uid: String
    ): ViewerInstance {
        globalClass.viewersManager.releaseViewersOfType(AudioPlayerInstance::class.java)
        return AudioPlayerInstance(uri, uid)
    }

    override fun onReady(instance: ViewerInstance) {
        setContent {
            FiloraTheme {
                MusicPlayerScreen(
                    audioPlayerInstance = instance as AudioPlayerInstance,
                    onClosed = { onBackPressedDispatcher.onBackPressed() }
                )
            }
        }
    }
}