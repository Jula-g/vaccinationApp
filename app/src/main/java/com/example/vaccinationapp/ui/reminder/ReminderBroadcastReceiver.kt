package com.example.vaccinationapp.ui.reminder

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat

class ReminderBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val appointmentDate = intent.getStringExtra("appointment_date")
        val appointmentTime = intent.getStringExtra("appointment_time")

        val reminderMessage = "Appointment Reminder: Date - $appointmentDate, Time - $appointmentTime"

        val notification = NotificationCompat.Builder(context, "channel_id")
            .setContentTitle("Appointment Reminder")
            .setContentText(reminderMessage)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notification)

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone = RingtoneManager.getRingtone(context, alarmSound)
        ringtone.play()
    }
}
