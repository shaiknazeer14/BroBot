package com.nazeer.brobot

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    companion object {
        const val TAG = "NotificationHelper"
        const val CHANNEL_ID = "BroBotResultChannel"
        const val NOTIFICATION_ID = 2
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "BroBot Results",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Shows BroBot action results"
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
        Log.d(TAG, "Result notification channel created")
    }

    fun showSuccess(message: String, note: String = "") {
        val content = if (note.isNotEmpty()) note else message
        show("✅ BroBot Done!", content)
    }

    fun showError(error: String) {
        show("❌ BroBot Failed", error)
    }

    fun showListening() {
        show("🎙️ BroBot is listening...", "Say your command now")
    }

    fun showRecording() {
        show("⏺️ Recording...", "Speak your command — 5 seconds")
    }

    fun showProcessing() {
        show("🧠 Processing...", "BroBot is understanding your command")
    }

    private fun show(title: String, content: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, notification)
        Log.d(TAG, "Notification shown: $title")
    }
}