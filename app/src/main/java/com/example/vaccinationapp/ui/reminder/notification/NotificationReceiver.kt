package com.example.vaccinationapp.ui.reminder.notification

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.vaccinationapp.R


class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val appointmentDate = intent.getStringExtra("appointment_date")
        val appointmentTime = intent.getStringExtra("appointment_time")

        val notificationMessage = "Appointment Reminder: Date - $appointmentDate, Time - $appointmentTime"

        showNotification(context, notificationMessage)
    }

    private fun showNotification(context: Context, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setContentTitle("Appointment Reminder")
            .setContentText(message)
            .setSmallIcon(R.drawable.syringe_logo_with_name)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)
    }
}
