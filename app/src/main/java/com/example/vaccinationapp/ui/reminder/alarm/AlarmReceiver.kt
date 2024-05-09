package com.example.vaccinationapp.ui.reminder.alarm

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.example.vaccinationapp.R

/**
 * Broadcast receiver for the alarm.
 */
class AlarmReceiver : BroadcastReceiver() {

    /**
     * Receives the alarm broadcast.
     * @param context The context.
     * @param intent The intent.
     */
    override fun onReceive(context: Context, intent: Intent) {
        val appointmentDate = intent.getStringExtra("appointment_date")
        val appointmentTime = intent.getStringExtra("appointment_time")

        val reminderMessage =
            "Appointment Reminder: Date - $appointmentDate, Time - $appointmentTime"

        showNotification(context, reminderMessage)
        playAlarm(context)
    }

    /**
     * Shows a notification.
     * @param context The context.
     * @param message The message to be displayed in the notification.
     */
    private fun showNotification(context: Context, message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val smallIcon = R.drawable.syringe_logo_with_name
        val notification = NotificationCompat.Builder(context, "channel_id")
            .setSmallIcon(smallIcon)
            .setContentTitle("Appointment Reminder")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)
    }

    /**
     * Plays the alarm sound.
     * @param context The context.
     */
    private fun playAlarm(context: Context) {
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone = RingtoneManager.getRingtone(context, alarmSound)
        ringtone.play()
    }
}
