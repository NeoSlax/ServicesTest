package com.example.servicestest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class MyForegroundService : Service() {
    private val scope = CoroutineScope(Dispatchers.Main)
    override fun onCreate() {
        super.onCreate()
        log("onCreate")
        startForeground(
            NOTIFICATION_FOREGROUND_ID,
            createNotification()
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            countDown()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO()
    }


    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
        scope.cancel()
    }

    private fun log(text: String) {
        Log.d("TEST_TAG", "MyForegroundService: $text")
    }

    private suspend fun countDown() {
        for (i in 0 until 4) {
            log("timer is: $i")
            delay(1000)
        }
        stopSelf()
    }

    private fun createNotification(): Notification {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Foreground channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Foreground Work")
            .setContentText("Foreground Service is Working")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .build()

    }

    companion object {

        private const val NOTIFICATION_FOREGROUND_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "1"
        fun newInstance(context: Context): Intent {
            return Intent(context, MyForegroundService::class.java)
        }
    }

}