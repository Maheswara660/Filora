package com.maheswara660.filora.screen.viewer

import com.maheswara660.filora.screen.viewer.audio.AudioPlayerInstance

class ViewersManager {
    val instances = mutableListOf<ViewerInstance>()

    fun releaseAll() {
        instances.toList().forEach { it.onClose() }
        instances.clear()
    }

    fun releaseViewersOfType(clazz: java.lang.Class<out ViewerInstance>) {
        instances.filter { clazz.isInstance(it) }.forEach { 
            it.onClose()
            instances.remove(it)
        }
    }
}