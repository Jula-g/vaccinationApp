package com.example.vaccinationapp.ui.reminder.alarm

/**
 * Interface for the alarm scheduler.
 */
interface AlarmScheduler {

    /**
     * Schedules an alarm.
     * @param alarm The alarm to be scheduled.
     */
    fun schedule(alarm: Alarm)

    /**
     * Cancels an alarm.
     * @param alarm The alarm to be cancelled.
     */
    fun cancel(alarm: Alarm)
}