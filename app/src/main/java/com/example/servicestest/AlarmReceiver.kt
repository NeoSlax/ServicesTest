package com.example.servicestest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val notificationManager =
                getSystemService(it, NotificationManager::class.java) as NotificationManager
            createNotificationChannel(notificationManager)
            val notification = NotificationCompat.Builder(it, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Alarm")
                .setContentText("30 seconds have passed")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .build()
            notificationManager.notify(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {

        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "1213"
        private const val NOTIFICATION_CHANNEL_NAME = "Alarm channel"
        private const val PROGRESS_MAX_VAL = 100
        private const val PROGRESS_INIT_VAL = 0

        fun newInstance(context: Context): Intent {
            return Intent(context, AlarmReceiver::class.java)
        }
    }
}