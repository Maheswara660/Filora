package com.maheswara660.filora.screen.viewer.video.model

import androidx.media3.common.Player

data class VideoPlayerState(
    val isReady: Boolean = false,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = true,
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val playbackSpeed: Float = 1.0f,
    val repeatMode: Int = Player.REPEAT_MODE_OFF,
    val isMuted: Boolean = false,
    val showControls: Boolean = true,
    val title: String = "",
    val isPipMode: Boolean = false,
    val brightness: Float = 0.5f,
    val gestureVolume: Float = 1.0f,
    val subtitles: List<String> = emptyList(),
    val selectedSubtitleIndex: Int = -1,
    val audioTracks: List<String> = emptyList(),
    val selectedAudioIndex: Int = 0,
    val isLocked: Boolean = false,
    val resizeMode: Int = 0 // AspectRatioFrameLayout.RESIZE_MODE_FIT
)
