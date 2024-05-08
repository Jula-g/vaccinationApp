package com.example.vaccinationapp.ui.reminder.alarm

import java.time.LocalDateTime

data class Alarm(
    val time: LocalDateTime,
    val message: String
)