package com.example.vaccinationapp.ui.reminder.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.example.vaccinationapp.R

/**
 * NotificationService class is a Service class that creates a notification channel
 */
class NotificationService : Service() {

    /**
     * onCreate method is called when the service is created
     */
    override fun onCreate() {
        super.onCreate()

        val channelId = getString(R.string.default_notification_channel_id)
        val channelName = getString(R.string.default_notification_channel_name)
        val channelDescription = getString(R.string.default_notification_channel_description)

        val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = channelDescription
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    /**
     * onBind method is called when the service is bound
     * @param intent The intent that was used to bind the service
     * @return null
     */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}
