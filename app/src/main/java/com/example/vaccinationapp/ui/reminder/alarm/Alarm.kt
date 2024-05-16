package com.example.vaccinationapp.ui.reminder.alarm

import java.time.LocalDateTime

/**
 * Data class for an alarm.
 * @property time The time the alarm is set for.
 * @property message The message to be displayed when the alarm goes off.
 */
data class Alarm(
    val time: LocalDateTime,
    val message: String
)