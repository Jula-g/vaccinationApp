package com.example.vaccinationapp.ui.reminder.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "appointment_reminder_channel"
        const val CHANNEL_NAME = "Appointment Reminders"
        const val CHANNEL_DESCRIPTION = "Channel for appointment reminders"
    }

    fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = CHANNEL_DESCRIPTION
        }

        val notificationManager =
            context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}