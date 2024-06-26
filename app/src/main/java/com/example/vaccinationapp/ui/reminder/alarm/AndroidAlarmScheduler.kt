package com.example.vaccinationapp.ui.reminder.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.time.ZoneId

/**
 * Android implementation of the alarm scheduler.
 * @property context The context.
 */
class AndroidAlarmScheduler(private val context: Context) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    /**
     * Schedules an alarm.
     * @param alarm The alarm to be scheduled.
     */
    override fun schedule(alarm: Alarm) {
        val intent = Intent(context, AlarmReceiver::class.java)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarm.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            PendingIntent.getBroadcast(
                context,
                alarm.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    /**
     * Cancels an alarm.
     * @param alarm The alarm to be cancelled.
     */
    override fun cancel(alarm: Alarm) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                alarm.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}