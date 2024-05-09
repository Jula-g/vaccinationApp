package com.example.vaccinationapp.ui.reminder.notification

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.vaccinationapp.R

/**
 * NotificationReceiver class is a BroadcastReceiver class that sends notifications to the user
 */
class NotificationReceiver : BroadcastReceiver() {

    /**
     * onReceive method is called when the BroadcastReceiver receives a broadcast
     * @param context The context in which the receiver is running
     * @param intent The intent being received
     */
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        val appointmentDate = intent.getStringExtra("appointment_date")
        val appointmentTime = intent.getStringExtra("appointment_time")

        val notificationMessage =
            "Appointment Reminder: Date - $appointmentDate, Time - $appointmentTime"

        showNotification(context, notificationMessage)
    }

    /**
     * showNotification method is used to show the notification to the user
     * @param context The context in which the receiver is running
     * @param message The message to be displayed in the notification
     */
    private fun showNotification(context: Context, message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

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
