package com.nazeer.brobot
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class BroBotService : Service() {
    private var wakeWordDetector: WakeWordDetector?=null
    companion object {
        const val CHANNEL_ID = "BroBotChannel"
        const val NOTIFICATION_ID = 1
    }
    override fun onCreate(){
        super.onCreate()
        createNotificationChannel()
        startForegroundService(NOTIFICATION_ID,buildNotification()))
        wakeWordDetector=WakeWordDetector(this)
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onDestroy()
        wakeWordDetector?.stopListening()
        wakeWordDetector=null

    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    private fun createNotificationChannel(){
        val channel=NotificationChannel(
            CHANNEL_ID,
            "BroBot Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager=getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

    }
    private fun buildNotification():Notification{
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("BroBot is listening")
            .setContentText("Say 'Hey Bro' to activate me")
            .setSmallIcon(android.R.drawble.ic_btn_speak_now)
            .build()

    }

}