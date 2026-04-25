package com.maheswara660.filora.screen.viewer.video.ui

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.PictureInPictureModeChangedInfo
import androidx.core.util.Consumer
import androidx.media3.ui.PlayerView
import com.maheswara660.filora.common.toFormattedTime
import com.maheswara660.filora.common.ui.CustomLoader
import com.maheswara660.filora.screen.viewer.video.VideoPlayerInstance
import com.maheswara660.filora.screen.viewer.video.model.VideoPlayerState
import kotlinx.coroutines.*
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import kotlin.math.abs

@Composable
fun VideoPlayerScreen(
    videoUri: Uri,
    videoPlayerInstance: VideoPlayerInstance,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val playerState by videoPlayerInstance.playerState.collectAsState()
    val activity = context as? ComponentActivity
    
    var isInPipMode by remember { mutableStateOf(activity?.isInPictureInPictureMode ?: false) }
    
    DisposableEffect(activity) {
        val observer = Consumer<PictureInPictureModeChangedInfo> { info ->
            isInPipMode = info.isInPictureInPictureMode
        }
        activity?.addOnPictureInPictureModeChangedListener(observer)
        onDispose { activity?.removeOnPictureInPictureModeChangedListener(observer) }
    }

    var showBrightnessIndicator by remember { mutableStateOf(false) }
    var showVolumeIndicator by remember { mutableStateOf(false) }
    var hideIndicatorsJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(videoUri) {
        videoPlayerInstance.initializePlayer(context, videoUri)
    }

    // Manage status bar visibility and icons
    LaunchedEffect(playerState.showControls, playerState.isLoading, isInPipMode) {
        val showBar = (playerState.showControls || playerState.isLoading) && !isInPipMode
        activity?.window?.let { window ->
            val controller = androidx.core.view.WindowInsetsControllerCompat(window, window.decorView)
            if (showBar) {
                controller.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            } else {
                controller.hide(androidx.core.view.WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            controller.isAppearanceLightStatusBars = false // Always light icons on dark video background
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            videoPlayerInstance.onClose()
            // Restore UI visibility on exit
            activity?.window?.let { window ->
                val controller = androidx.core.view.WindowInsetsControllerCompat(window, window.decorView)
                controller.show(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(playerState.isLocked) {
                    if (!playerState.isLocked) {
                        detectVerticalDragGestures(
                            onDragStart = { },
                            onVerticalDrag = { change, dragAmount ->
                                val isLeft = change.position.x < size.width / 2
                                if (isLeft) {
                                    val currentBrightness =
                                        activity?.window?.attributes?.screenBrightness ?: 0.5f
                                    val newBrightness =
                                        (currentBrightness - dragAmount / 500f).coerceIn(0f, 1f)
                                    activity?.window?.let { window ->
                                        val params = window.attributes
                                        params.screenBrightness = newBrightness
                                        window.attributes = params
                                    }
                                    showBrightnessIndicator = true
                                    showVolumeIndicator = false
                                } else {
                                    val audioManager =
                                        context.getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager
                                    val maxVolume =
                                        audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC)
                                    val currentVolume =
                                        audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC)
                                    val delta = (dragAmount / 500f * maxVolume).toInt()
                                    val newVolume = (currentVolume - delta).coerceIn(0, maxVolume)
                                    audioManager.setStreamVolume(
                                        android.media.AudioManager.STREAM_MUSIC,
                                        newVolume,
                                        0
                                    )
                                    showVolumeIndicator = true
                                    showBrightnessIndicator = false
                                }

                                hideIndicatorsJob?.cancel()
                                hideIndicatorsJob = CoroutineScope(Dispatchers.Main).launch {
                                    delay(1500)
                                    showBrightnessIndicator = false
                                    showVolumeIndicator = false
                                }
                            }
                        )
                    }
                }
        ) {
            // Video Player View
            if (playerState.isReady) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = videoPlayerInstance.getPlayer()
                                useController = false
                                setBackgroundColor(android.graphics.Color.BLACK)
                                resizeMode = playerState.resizeMode
                            }
                        },
                        update = { view ->
                            view.resizeMode = playerState.resizeMode
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .zoomable(
                                zoomState = rememberZoomState(),
                                onTap = {
                                    if (!playerState.isLocked) {
                                        videoPlayerInstance.toggleControls()
                                    } else {
                                        videoPlayerInstance.toggleControls()
                                    }
                                }
                            )
                    )

                    if (!playerState.isLocked) {
                        Row(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onDoubleTap = {
                                                val newPos = (playerState.currentPosition - 10000).coerceAtLeast(0)
                                                videoPlayerInstance.seekTo(newPos)
                                            },
                                            onTap = { videoPlayerInstance.toggleControls() }
                                        )
                                    }
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .pointerInput(Unit) {
                                        detectTapGestures(
                                            onDoubleTap = {
                                                val newPos = (playerState.currentPosition + 10000).coerceAtMost(playerState.duration)
                                                videoPlayerInstance.seekTo(newPos)
                                            },
                                            onTap = { videoPlayerInstance.toggleControls() }
                                        )
                                    }
                            )
                        }
                    }
                }
            }

            // Indicators
            if (showBrightnessIndicator) {
                GestureIndicator(
                    icon = Icons.Default.BrightnessMedium,
                    value = activity?.window?.attributes?.screenBrightness ?: 0.5f,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 48.dp)
                )
            }
            if (showVolumeIndicator) {
                val audioManager = context.getSystemService(android.content.Context.AUDIO_SERVICE) as android.media.AudioManager
                val current = audioManager.getStreamVolume(android.media.AudioManager.STREAM_MUSIC).toFloat()
                val max = audioManager.getStreamMaxVolume(android.media.AudioManager.STREAM_MUSIC).toFloat()
                GestureIndicator(
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    value = current / max,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 48.dp)
                )
            }

            // Controls Overlay
            AnimatedVisibility(
                visible = playerState.showControls && !playerState.isLoading && !isInPipMode,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                VideoControls(
                    state = playerState,
                    onPlayPause = { videoPlayerInstance.playPause() },
                    onSeekForward = {
                        val newPos =
                            (playerState.currentPosition + 10000).coerceAtMost(playerState.duration)
                        videoPlayerInstance.seekTo(newPos)
                    },
                    onSeekBackward = {
                        val newPos = (playerState.currentPosition - 10000).coerceAtLeast(0)
                        videoPlayerInstance.seekTo(newPos)
                    },
                    onSeek = { position -> videoPlayerInstance.seekTo(position) },
                    onBackPressed = onBackPressed,
                    onToggleMute = { videoPlayerInstance.toggleMute() },
                    onSpeedChange = { videoPlayerInstance.setPlaybackSpeed(it) },
                    onSubtitleClick = { videoPlayerInstance.selectSubtitle(it) },
                    onAudioTrackClick = { videoPlayerInstance.selectAudioTrack(it) },
                    onToggleLock = { videoPlayerInstance.toggleLock() },
                    onToggleResize = { videoPlayerInstance.toggleResizeMode() },
                    onOrientationToggle = {
                        activity?.let {
                            it.requestedOrientation =
                                if (it.requestedOrientation == android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                                    android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                } else {
                                    android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                }
                        }
                    }
                )
            }

            // Loading indicator
            if (playerState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CustomLoader()
                }
            }
        }
    }
}

@Composable
fun VideoControls(
    state: VideoPlayerState,
    onToggleMute: () -> Unit,
    onPlayPause: () -> Unit,
    onSeekForward: () -> Unit,
    onSeekBackward: () -> Unit,
    onSeek: (Long) -> Unit,
    onBackPressed: () -> Unit,
    onSpeedChange: (Float) -> Unit,
    onOrientationToggle: () -> Unit,
    onSubtitleClick: (Int) -> Unit,
    onAudioTrackClick: (Int) -> Unit,
    onToggleLock: () -> Unit,
    onToggleResize: () -> Unit
) {
    var showSubtitleDialog by remember { mutableStateOf(false) }
    var showAudioTrackDialog by remember { mutableStateOf(false) }

    if (state.isLocked) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            IconButton(
                onClick = onToggleLock,
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Unlock",
                    tint = Color.White
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.6f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.6f)
                        )
                    )
                )
        ) {
            // Top bar
            TopBar(
                playerState = state,
                onBackPressed = onBackPressed,
                onToggleMute = onToggleMute,
                onOrientationToggle = onOrientationToggle,
                onSubtitleClick = {
                    if (state.subtitles.size <= 1) {
                        // Simple toggle if only one track or none
                        val newIndex = if (state.selectedSubtitleIndex == -1) 0 else -1
                        onSubtitleClick(newIndex)
                    } else {
                        showSubtitleDialog = true
                    }
                },
                onAudioTrackClick = {
                    if (state.audioTracks.size > 1) {
                        showAudioTrackDialog = true
                    }
                },
                onToggleLock = onToggleLock,
                onToggleResize = onToggleResize,
                modifier = Modifier.align(Alignment.TopStart)
            )

            if (showSubtitleDialog) {
                SelectionDialog(
                    title = "Subtitles",
                    options = state.subtitles,
                    selectedIndex = state.selectedSubtitleIndex,
                    onDismiss = { showSubtitleDialog = false },
                    onSelect = onSubtitleClick,
                    allowOff = true
                )
            }

            if (showAudioTrackDialog) {
                SelectionDialog(
                    title = "Audio Tracks",
                    options = state.audioTracks,
                    selectedIndex = state.selectedAudioIndex,
                    onDismiss = { showAudioTrackDialog = false },
                    onSelect = onAudioTrackClick,
                    allowOff = false
                )
            }

            // Center controls
            CenterControls(
                isPlaying = state.isPlaying,
                onPlayPause = onPlayPause,
                onSeekForward = onSeekForward,
                onSeekBackward = onSeekBackward,
                modifier = Modifier.align(Alignment.Center)
            )

            // Bottom controls
            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                SpeedSelector(
                    currentSpeed = state.playbackSpeed,
                    onSpeedChange = onSpeedChange,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                BottomControls(
                    currentPosition = state.currentPosition,
                    duration = state.duration,
                    onSeek = onSeek
                )
            }
        }
    }
}

@Composable
fun TopBar(
    playerState: VideoPlayerState,
    onBackPressed: () -> Unit,
    onToggleMute: () -> Unit,
    onOrientationToggle: () -> Unit,
    onSubtitleClick: () -> Unit,
    onAudioTrackClick: () -> Unit,
    onToggleLock: () -> Unit,
    onToggleResize: () -> Unit,
    modifier: Modifier,
) {
    Row(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackPressed,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = playerState.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.White,
            modifier = Modifier.weight(1f),
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (playerState.audioTracks.size > 1) {
            IconButton(
                onClick = onAudioTrackClick,
            ) {
                Icon(
                    imageVector = Icons.Default.Audiotrack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        IconButton(
            onClick = onSubtitleClick,
        ) {
            Icon(
                imageVector = Icons.Default.Subtitles,
                contentDescription = null,
                tint = if (playerState.selectedSubtitleIndex != -1) Color.Yellow else Color.White
            )
        }

        IconButton(
            onClick = onToggleResize,
        ) {
            Icon(
                imageVector = Icons.Default.AspectRatio,
                contentDescription = null,
                tint = Color.White
            )
        }

        IconButton(
            onClick = onOrientationToggle,
        ) {
            Icon(
                imageVector = Icons.Default.ScreenRotation,
                contentDescription = null,
                tint = Color.White
            )
        }

        IconButton(
            onClick = onToggleMute,
        ) {
            Icon(
                imageVector = if (playerState.isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = null,
                tint = Color.White
            )
        }

        IconButton(
            onClick = onToggleLock,
        ) {
            Icon(
                imageVector = Icons.Default.LockOpen,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

@Composable
fun GestureIndicator(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(64.dp, 120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            LinearProgressIndicator(
                progress = { value },
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .graphicsLayer {
                        rotationZ = -90f
                    },
                color = Color.White,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
fun SpeedSelector(
    currentSpeed: Float,
    onSpeedChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val speeds = listOf(0.25f, 0.5f, 1.0f, 1.25f, 1.5f, 2.0f)
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        speeds.forEach { speed ->
            Text(
                text = "${speed}x",
                color = if (currentSpeed == speed) Color.White else Color.White.copy(alpha = 0.5f),
                modifier = Modifier
                    .clickable { onSpeedChange(speed) }
                    .padding(8.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (currentSpeed == speed) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
fun CenterControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onSeekForward: () -> Unit,
    onSeekBackward: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onSeekBackward,
            modifier = Modifier
                .size(56.dp)
                .background(
                    colorScheme.surface.copy(alpha = 0.6f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Replay10,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        IconButton(
            onClick = onPlayPause,
            modifier = Modifier
                .size(72.dp)
                .background(
                    colorScheme.primary,
                    CircleShape
                )
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                tint = colorScheme.onPrimary,
                modifier = Modifier.size(36.dp)
            )
        }

        IconButton(
            onClick = onSeekForward,
            modifier = Modifier
                .size(56.dp)
                .background(
                    colorScheme.surface.copy(alpha = 0.6f),
                    CircleShape
                )
        ) {
            Icon(
                imageVector = Icons.Default.Forward10,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomControls(
    currentPosition: Long,
    duration: Long,
    onSeek: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(bottom = 24.dp),
        ) {
            var isDragging by remember { mutableStateOf(false) }
            var pendingSeekPosition by remember { mutableLongStateOf(0L) }
            var hasUncommittedSeek by remember { mutableStateOf(false) }

            val displayPosition = when {
                isDragging || hasUncommittedSeek -> pendingSeekPosition
                else -> currentPosition
            }

            val progress = if (duration > 0) displayPosition.toFloat() / duration.toFloat() else 0f

            LaunchedEffect(currentPosition) {
                if (hasUncommittedSeek && abs(currentPosition - pendingSeekPosition) < 1000) {
                    hasUncommittedSeek = false
                }
            }

            Slider(
                value = progress,
                onValueChange = { value ->
                    isDragging = true
                    pendingSeekPosition = (value * duration).toLong()
                },
                onValueChangeFinished = {
                    hasUncommittedSeek = true
                    onSeek(pendingSeekPosition)
                    isDragging = false
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = displayPosition.toFormattedTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
                Text(
                    text = duration.toFormattedTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun SelectionDialog(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit,
    allowOff: Boolean = true
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.DarkGray.copy(alpha = 0.95f)
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (allowOff) {
                    Surface(
                        onClick = { onSelect(-1); onDismiss() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedIndex == -1) Color.White.copy(alpha = 0.1f) else Color.Transparent
                    ) {
                        Text(
                            text = "Off",
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                options.forEachIndexed { index, lang ->
                    Surface(
                        onClick = { onSelect(index); onDismiss() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = if (selectedIndex == index) Color.White.copy(alpha = 0.1f) else Color.Transparent
                    ) {
                        Text(
                            text = lang,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}