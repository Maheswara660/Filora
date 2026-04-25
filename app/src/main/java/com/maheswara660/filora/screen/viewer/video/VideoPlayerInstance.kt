package com.maheswara660.filora.screen.viewer.video

import android.content.Context
import android.net.Uri
import androidx.media3.common.C.TIME_UNSET
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import com.maheswara660.filora.App.Companion.globalClass
import com.maheswara660.filora.R
import com.maheswara660.filora.common.isNot
import com.maheswara660.filora.common.name
import com.maheswara660.filora.screen.viewer.ViewerInstance
import com.maheswara660.filora.screen.viewer.video.model.VideoPlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VideoPlayerInstance(
    override val uri: Uri,
    override val id: String
) : ViewerInstance {
    private val _playerState = MutableStateFlow(VideoPlayerState())
    val playerState: StateFlow<VideoPlayerState> = _playerState.asStateFlow()
    private var exoPlayer: ExoPlayer? = null
    private var positionTrackingJob: Job? = null
    private var hideControlsJob: Job? = null

    suspend fun initializePlayer(context: Context, uri: Uri) {
        _playerState.update {
            it.copy(
                isLoading = true,
                isReady = false
            )
        }

        withContext(Dispatchers.Main) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.Builder()
                    .setUri(uri)
                    .build()

                setMediaItem(mediaItem)
                prepare()

                volume = 1f

                _playerState.update { currentState ->
                    currentState.copy(
                    title = uri.name ?: globalClass.getString(R.string.unknown)
                    )
                }

                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _playerState.update { currentState -> currentState.copy(isPlaying = isPlaying) }
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        _playerState.update { currentState ->
                            currentState.copy(
                            isLoading = playbackState == Player.STATE_BUFFERING
                            )
                        }

                        if (playbackState == Player.STATE_READY) {
                            _playerState.update { currentState ->
                                currentState.copy(
                                    duration = duration,
                                    isLoading = false,
                                    isReady = true
                                )
                            }
                            updateTracks()
                        }
                    }

                    override fun onTracksChanged(tracks: androidx.media3.common.Tracks) {
                        updateTracks()
                    }
                })
            }
        }
        startPositionTracking()
    }

    private fun updateTracks() {
        exoPlayer?.let { player ->
            val tracks = player.currentTracks
            val subtitleTracks = mutableListOf<String>()
            val audioTracks = mutableListOf<String>()
            
            tracks.groups.forEach { group ->
                when (group.type) {
                    C.TRACK_TYPE_TEXT -> {
                        for (i in 0 until group.length) {
                            val format = group.getTrackFormat(i)
                            subtitleTracks.add(format.language ?: "Unknown Language")
                        }
                    }
                    C.TRACK_TYPE_AUDIO -> {
                        for (i in 0 until group.length) {
                            val format = group.getTrackFormat(i)
                            audioTracks.add(format.language ?: format.label ?: "Audio ${audioTracks.size + 1}")
                        }
                    }
                }
            }
            
            _playerState.update { it.copy(subtitles = subtitleTracks, audioTracks = audioTracks) }
        }
    }

    fun selectAudioTrack(index: Int) {
        exoPlayer?.let { player ->
            val parametersBuilder = player.trackSelectionParameters.buildUpon()
            val tracks = player.currentTracks
            var count = 0
            var targetLanguage: String? = null
            
            tracks.groups.forEach { group ->
                if (group.type == C.TRACK_TYPE_AUDIO) {
                    for (i in 0 until group.length) {
                        if (count == index) {
                            targetLanguage = group.getTrackFormat(i).language
                        }
                        count++
                    }
                }
            }
            
            targetLanguage?.let {
                parametersBuilder.setPreferredAudioLanguage(it)
            }
            player.trackSelectionParameters = parametersBuilder.build()
            _playerState.update { it.copy(selectedAudioIndex = index) }
        }
    }

    fun selectSubtitle(index: Int) {
        exoPlayer?.let { player ->
            val parametersBuilder = player.trackSelectionParameters.buildUpon()
            if (index == -1) {
                // Disable subtitles
                parametersBuilder.setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
            } else {
                parametersBuilder.setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                // In a real scenario, you'd select the specific track group.
                // For simplicity, we can use preferred language if languages are unique,
                // or use override if we want to be precise.
                
                // Let's use preferred language as a simple way
                val tracks = player.currentTracks
                var count = 0
                var targetLanguage: String? = null
                tracks.groups.forEach { group ->
                    if (group.type == C.TRACK_TYPE_TEXT) {
                        for (i in 0 until group.length) {
                            if (count == index) {
                                targetLanguage = group.getTrackFormat(i).language
                            }
                            count++
                        }
                    }
                }
                targetLanguage?.let {
                    parametersBuilder.setPreferredTextLanguage(it)
                }
            }
            player.trackSelectionParameters = parametersBuilder.build()
            _playerState.update { it.copy(selectedSubtitleIndex = index) }
        }
    }


    private fun startPositionTracking() {
        positionTrackingJob?.cancel()
        positionTrackingJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                exoPlayer?.let { player ->
                    _playerState.update { currentState ->
                        currentState.copy(
                        currentPosition = player.currentPosition,
                            duration = player.duration.takeIf { it isNot TIME_UNSET } ?: 0L
                        )
                    }
                }
                delay(100)
            }
        }
    }

    private fun startAutoHideTimer() {
        hideControlsJob?.cancel()
        hideControlsJob = CoroutineScope(Dispatchers.Main).launch {
            delay(3000)
            _playerState.update { it.copy(showControls = false) }
        }
    }

    fun playPause() {
        exoPlayer?.let {
            if (it.isPlaying) it.pause() else it.play()
            _playerState.update { state -> state.copy(isPlaying = it.isPlaying) }
            startAutoHideTimer()
        }
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
        startAutoHideTimer()
    }

    fun setPlaybackSpeed(speed: Float) {
        exoPlayer?.setPlaybackSpeed(speed)
        _playerState.update { it.copy(playbackSpeed = speed) }
    }

    fun toggleMute() {
        exoPlayer?.let {
            it.volume = if (it.volume == 0f) 1f else 0f
            _playerState.update { state -> state.copy(isMuted = it.volume == 0f) }
            startAutoHideTimer()
        }
    }

    fun toggleControls() {
        _playerState.update { it.copy(showControls = !it.showControls) }
        if (_playerState.value.showControls) {
            startAutoHideTimer()
        }
    }

    fun toggleRepeatMode() {
        val newMode = when (_playerState.value.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        exoPlayer?.repeatMode = newMode
        _playerState.update { it.copy(repeatMode = newMode) }
    }

    fun toggleLock() {
        _playerState.update { it.copy(isLocked = !it.isLocked) }
    }

    fun toggleResizeMode() {
        val nextMode = when (_playerState.value.resizeMode) {
            0 -> 3 // Fit -> Zoom
            3 -> 4 // Zoom -> Fill
            else -> 0 // Fill -> Fit
        }
        _playerState.update { it.copy(resizeMode = nextMode) }
    }

    fun getPlayer() = exoPlayer

    override fun onClose() {
        positionTrackingJob?.cancel()
        exoPlayer?.release()
        exoPlayer = null
    }
}