package com.example.servicestest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.*

class MyForegroundService : Service() {
    private val scope = CoroutineScope(Dispatchers.Main)

    private val notificationBuilder by lazy {
        NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Foreground Work")
            .setContentText("Foreground Service is Working")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOnlyAlertOnce(true)
            .setProgress(PROGRESS_MAX_VAL, PROGRESS_INIT_VAL, false)
    }

    private val notificationManager by lazy {
        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
        createNotificationChannel()
        startForeground(
            NOTIFICATION_FOREGROUND_ID,
            notificationBuilder.build()
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        log("onStartCommand")
        scope.launch {
            countDown()
        }
        return START_STICKY
    }

    fun interface OnProgressChange {
        fun onProgressChange(progress: Int)
    }

    var onProgressChange: OnProgressChange? = null

    override fun onBind(intent: Intent?): IBinder {
        log("onBind")
        return LocalBinder()
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
        for (i in 0 until 100 step 5) {
            log("timer is: $i")
            delay(1000)
            onProgressChange?.onProgressChange(i)
            val notification = notificationBuilder
                .setProgress(PROGRESS_MAX_VAL, i, false)
                .build()
            notificationManager.notify(NOTIFICATION_FOREGROUND_ID, notification)
        }
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Foreground channel",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    inner class LocalBinder : Binder() {
        fun getService() = this@MyForegroundService
    }

    companion object {

        private const val NOTIFICATION_FOREGROUND_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "1"
        private const val PROGRESS_MAX_VAL = 100
        private const val PROGRESS_INIT_VAL = 0

        fun newInstance(context: Context): Intent {
            return Intent(context, MyForegroundService::class.java)
        }
    }

}