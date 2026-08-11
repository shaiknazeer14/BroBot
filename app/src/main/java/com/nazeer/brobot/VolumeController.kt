package com.nazeer.brobot

import android.content.Context
import android.media.AudioManager
import android.util.Log

class VolumeController(private val context: Context) {

    companion object {
        const val TAG = "VolumeController"
    }

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun handleVolumeCommand(text: String): Boolean {
        val lower = text.lowercase()

        return when {
            lower.contains("volume up") -> {
                val percent = extractPercent(lower)
                increaseVolume(percent)
                true
            }
            lower.contains("volume down") -> {
                val percent = extractPercent(lower)
                decreaseVolume(percent)
                true
            }
            lower.contains("mute") -> {
                mute()
                true
            }
            lower.contains("unmute") || lower.contains("unmute") -> {
                unmute()
                true
            }
            else -> false
        }
    }

    private fun extractPercent(text: String): Int {
        val regex = Regex("(\\d+)\\s*%")
        val match = regex.find(text)
        return match?.groupValues?.get(1)?.toIntOrNull() ?: 20
    }

    private fun increaseVolume(percent: Int) {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val increaseBy = (maxVolume * percent / 100)
        val newVolume = (currentVolume + increaseBy).coerceAtMost(maxVolume)
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            newVolume,
            AudioManager.FLAG_SHOW_UI
        )
        Log.d(TAG, "Volume increased by $percent% → new volume: $newVolume/$maxVolume")
    }

    private fun decreaseVolume(percent: Int) {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val decreaseBy = (maxVolume * percent / 100)
        val newVolume = (currentVolume - decreaseBy).coerceAtLeast(0)
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            newVolume,
            AudioManager.FLAG_SHOW_UI
        )
        Log.d(TAG, "Volume decreased by $percent% → new volume: $newVolume/$maxVolume")
    }

    private fun mute() {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_MUTE,
            AudioManager.FLAG_SHOW_UI
        )
        Log.d(TAG, "Volume muted")
    }

    private fun unmute() {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_UNMUTE,
            AudioManager.FLAG_SHOW_UI
        )
        Log.d(TAG, "Volume unmuted")
    }
}