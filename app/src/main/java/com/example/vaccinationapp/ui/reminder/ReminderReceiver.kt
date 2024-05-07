package com.example.vaccinationapp.ui.reminder

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.vaccinationapp.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.vaccinationapp.SHOW_NOTIFICATION") {
            val reminderMessage = intent.getStringExtra("reminder_message")
            showNotification(context, reminderMessage ?: "Reminder", "It's time for your appointment!")
        }
    }

    private fun showNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.syringe_logo_with_name)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        notificationManager.notify(0, builder.build())
    }
}
