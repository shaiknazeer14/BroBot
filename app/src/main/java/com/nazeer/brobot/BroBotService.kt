package com.nazeer.brobot

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class BroBotService : Service() {

    private var wakeWordDetector: WakeWordDetector? = null
    private var audioRecorder: AudioRecorder? = null
    private var apiClient: ApiClient? = null
    private var notificationHelper: NotificationHelper? = null
    private var volumeController: VolumeController? = null

    companion object {
        const val CHANNEL_ID = "BroBotChannel"
        const val NOTIFICATION_ID = 1
        const val TAG = "BroBotService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "BroBotService onCreate called")
        createNotificationChannel()
        try {
            startForeground(NOTIFICATION_ID, buildNotification())
            Log.d(TAG, "startForeground successful")
        } catch (e: Exception) {
            Log.e(TAG, "startForeground failed: ${e.message}")
        }

        // Create in order
        notificationHelper = NotificationHelper(this)
        Log.d(TAG, "NotificationHelper created")

        volumeController = VolumeController(this)
        Log.d(TAG, "VolumeController created")

        apiClient = ApiClient()
        Log.d(TAG, "ApiClient created")

        audioRecorder = AudioRecorder(this)
        Log.d(TAG, "AudioRecorder created")

        wakeWordDetector = WakeWordDetector(
            this,
            audioRecorder!!,
            apiClient!!,
            notificationHelper!!,
            volumeController!!
        )
        Log.d(TAG, "WakeWordDetector created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand called")
        try {
            startForeground(NOTIFICATION_ID, buildNotification())
        } catch (e: Exception) {
            Log.e(TAG, "onStartCommand foreground failed: ${e.message}")
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "BroBotService onDestroy called")
        wakeWordDetector?.release()
        wakeWordDetector = null
        audioRecorder?.release()
        audioRecorder = null
        apiClient = null
        notificationHelper = null
        volumeController = null
        Log.d(TAG, "All resources released")
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "BroBot Listener",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
        Log.d(TAG, "Notification channel created")
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("BroBot is listening...")
            .setContentText("Say 'Hey Bro' to activate")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}