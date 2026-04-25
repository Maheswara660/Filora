package com.maheswara660.filora.screen.viewer.audio

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.C.TIME_UNSET
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.core.content.FileProvider
import com.maheswara660.filora.App.Companion.globalClass
import com.maheswara660.filora.App.Companion.logger
import com.maheswara660.filora.R
import com.maheswara660.filora.common.isNot
import com.maheswara660.filora.screen.viewer.ViewerInstance
import com.maheswara660.filora.screen.viewer.audio.model.AudioMetadata
import com.maheswara660.filora.screen.viewer.audio.model.AudioPlayerColorScheme
import com.maheswara660.filora.screen.viewer.audio.model.PlayerState
import com.maheswara660.filora.screen.viewer.audio.ui.extractColorsFromBitmap
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
import java.io.File

class AudioPlayerInstance(
    override val uri: Uri,
    override val id: String
) : ViewerInstance {
    private val _playerState = MutableStateFlow(PlayerState())
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    private val _metadata = MutableStateFlow(AudioMetadata())
    val metadata: StateFlow<AudioMetadata> = _metadata.asStateFlow()

    private val _isEqualizerVisible = MutableStateFlow(false)
    val isEqualizerVisible: StateFlow<Boolean> = _isEqualizerVisible.asStateFlow()

    private val _isVolumeVisible = MutableStateFlow(false)
    val isVolumeVisible: StateFlow<Boolean> = _isVolumeVisible.asStateFlow()

    private val _colorScheme = MutableStateFlow(AudioPlayerColorScheme())
    val audioPlayerColorScheme: StateFlow<AudioPlayerColorScheme> = _colorScheme.asStateFlow()

    private val _sleepTimerRemaining = MutableStateFlow<Long?>(null)
    val sleepTimerRemaining: StateFlow<Long?> = _sleepTimerRemaining.asStateFlow()

    private val _queue = MutableStateFlow<List<File>>(emptyList())
    val queue: StateFlow<List<File>> = _queue.asStateFlow()

    private var positionTrackingJob: Job? = null
    private var sleepTimerJob: Job? = null

    private var controllerFuture: com.google.common.util.concurrent.ListenableFuture<androidx.media3.session.MediaController>? = null
    private var controller: androidx.media3.session.MediaController? = null

    @OptIn(UnstableApi::class)
    suspend fun initializePlayer(context: Context, uri: Uri) {
        if (controller != null) return

        val sessionToken = androidx.media3.session.SessionToken(
            context,
            android.content.ComponentName(context, com.maheswara660.filora.screen.viewer.audio.service.AudioPlayerService::class.java)
        )

        controllerFuture = androidx.media3.session.MediaController.Builder(context, sessionToken).buildAsync()
        
        withContext(Dispatchers.Main) {
            controllerFuture?.addListener({
                try {
                    val controller = controllerFuture?.get()
                    this@AudioPlayerInstance.controller = controller
                    
                    if (controller != null) {
                        setupController(controller, uri)
                    }
                } catch (e: Exception) {
                    logger.logError(e)
                }
            }, context.mainExecutor)
        }
    }

    private fun setupController(controller: androidx.media3.session.MediaController, uri: Uri) {
        controller.addListener(object : Player.Listener {
            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val currentUri = mediaItem?.localConfiguration?.uri
                if (currentUri != null) {
                    CoroutineScope(Dispatchers.Main).launch {
                        extractMetadata(globalClass, currentUri)
                    }
                }
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _playerState.update { 
                    it.copy(
                        isPlaying = controller.isPlaying,
                        isLoading = playbackState == Player.STATE_BUFFERING
                    )
                }
                
                if (playbackState == Player.STATE_READY) {
                    _playerState.update { it.copy(duration = controller.duration) }
                }
            }
            
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _playerState.update { it.copy(isPlaying = isPlaying) }
            }
        })

        val currentPath = resolvePath(globalClass, uri) ?: uri.path ?: return
        val requestedUri = Uri.fromFile(File(currentPath))
        
        val currentMediaItem = controller.currentMediaItem
        val currentPlayingUri = currentMediaItem?.localConfiguration?.uri

        if (currentPlayingUri == null || currentPlayingUri.toString() != requestedUri.toString()) {
            // New song or nothing playing, reload queue
            controller.stop()
            controller.clearMediaItems()
            
            loadQueue(requestedUri)
            
            val mediaItems = _queue.value.map { MediaItem.fromUri(Uri.fromFile(it)) }
            controller.addMediaItems(mediaItems)
            
            val currentIndex = _queue.value.indexOfFirst { it.absolutePath == currentPath }
            if (currentIndex >= 0) {
                controller.seekTo(currentIndex, 0)
            }
            controller.prepare()
            controller.play()
        } else {
            // Same song is already playing, just ensure UI is synced
            CoroutineScope(Dispatchers.Main).launch {
                extractMetadata(globalClass, requestedUri)
            }
            _playerState.update { 
                it.copy(
                    isPlaying = controller.isPlaying,
                    duration = controller.duration,
                    currentPosition = controller.currentPosition
                )
            }
        }
        startPositionTracking()
    }

    private fun loadQueue(uri: Uri) {
        val filesToLoad = globalClass.pendingAudioPlaylist ?: run {
            val currentFile = File(resolvePath(globalClass, uri) ?: return)
            val parentDir = currentFile.parentFile ?: return
            parentDir.listFiles { file ->
                file.isFile && globalClass.supportedAudioExtensions.contains(file.extension.lowercase())
            }?.sortedBy { it.name } ?: emptyList()
        }

        // Important: clear the pending playlist after reading it
        globalClass.pendingAudioPlaylist = null
        
        _queue.value = filesToLoad
    }


    fun setDefaultColorScheme(colorScheme: AudioPlayerColorScheme) {
        _colorScheme.value = colorScheme
    }

    private suspend fun extractMetadata(context: Context, uri: Uri) {
        withContext(Dispatchers.IO) {
            try {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, uri)

                val title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                    ?: globalClass.getString(R.string.unknown_title)
                val artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                    ?: globalClass.getString(R.string.unknown_artist)
                val album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                    ?: globalClass.getString(R.string.unknown_album)
                val durationStr =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val duration = durationStr?.toLongOrNull() ?: 0L

                // Extract album art
                val albumArtData = retriever.embeddedPicture
                val albumArt = albumArtData?.let { data ->
                    BitmapFactory.decodeByteArray(data, 0, data.size)
                }

                val metadata = AudioMetadata(
                    title = title,
                    artist = artist,
                    album = album,
                    duration = duration,
                    albumArt = albumArt
                )

                _metadata.value = metadata

                _metadata.value = metadata

                // Extract colors from album art if available
                albumArt?.let { bitmap ->
                    val colorScheme = extractColorsFromBitmap(bitmap, _colorScheme.value)
                    _colorScheme.value = colorScheme
                }

                retriever.release()
            } catch (e: Exception) {
                logger.logError(e)
                val fallbackMetadata = AudioMetadata(
                    title = uri.lastPathSegment ?: globalClass.getString(R.string.unknown_title)
                )
                _metadata.value = fallbackMetadata
                

            }
        }
    }

    private fun startPositionTracking() {
        positionTrackingJob?.cancel()
        positionTrackingJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                controller?.let {
                    _playerState.update { state ->
                        state.copy(currentPosition = it.currentPosition)
                    }
                }
                delay(500)
            }
        }
    }

    fun playPause() {
        controller?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun seekTo(position: Long) {
        controller?.let {
            it.seekTo(position)
            _playerState.update { state -> state.copy(currentPosition = it.currentPosition) }
        }
    }

    fun skipNext() {
        controller?.let {
            it.seekToNext()
            _playerState.update { state -> state.copy(currentPosition = it.currentPosition) }
        }
    }

    fun skipPrevious() {
        controller?.let {
            it.seekToPrevious()
            _playerState.update { state -> state.copy(currentPosition = it.currentPosition) }
        }
    }



    fun setPlaybackSpeed(speed: Float) {
        controller?.setPlaybackSpeed(speed)
        _playerState.update { it.copy(playbackSpeed = speed) }
    }

    fun toggleRepeatMode() {
        val newMode = when (_playerState.value.repeatMode) {
            Player.REPEAT_MODE_OFF -> Player.REPEAT_MODE_ALL
            else -> Player.REPEAT_MODE_OFF
        }
        controller?.repeatMode = newMode
        _playerState.update { it.copy(repeatMode = newMode) }
    }

    fun setVolume(volume: Float) {
        controller?.volume = volume
        _playerState.update { it.copy(volume = volume) }
    }

    fun toggleEqualizer() {
        _isEqualizerVisible.value = !_isEqualizerVisible.value
    }

    fun toggleVolume() {
        _isVolumeVisible.value = !_isVolumeVisible.value
    }

    fun startSleepTimer(minutes: Int) {
        sleepTimerJob?.cancel()
        if (minutes == 0) {
            _sleepTimerRemaining.value = null
            return
        }
        
        val durationMillis = minutes * 60 * 1000L
        _sleepTimerRemaining.value = durationMillis
        
        sleepTimerJob = CoroutineScope(Dispatchers.Main).launch {
            var remaining = durationMillis
            while (remaining > 0) {
                delay(1000)
                remaining -= 1000
                _sleepTimerRemaining.value = remaining
            }
            controller?.pause()
            _sleepTimerRemaining.value = null
        }
    }

    fun cancelSleepTimer() {
        sleepTimerJob?.cancel()
        _sleepTimerRemaining.value = null
    }

    private fun resolvePath(context: Context, uri: Uri): String? {
        if (uri.scheme == "file") return uri.path
        if (uri.scheme == "content") {
            try {
                // If it's our own FileProvider
                if (uri.authority == context.packageName + ".provider") {
                    // Extract the path from the content URI
                    // FileProvider URIs usually look like content://com.maheswara660.filora.provider/storage_root/path/to/file
                    // We can decode storage_root/ to /
                    val path = uri.path?.replaceFirst("/storage_root/", "/")
                    if (path != null) return path
                }
            } catch (e: Exception) {
                logger.logError(e)
            }
        }
        return uri.path
    }

    override fun onClose() {
        positionTrackingJob?.cancel()
        controllerFuture?.let {
            androidx.media3.session.MediaController.releaseFuture(it)
        }
        controller = null
        controllerFuture = null
    }
}