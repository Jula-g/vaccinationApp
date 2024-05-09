package com.example.vaccinationapp.DB.entities

/**
 * Data class representing an appointment in the database.
 * @property date The date of the appointment.
 * @property time The time of the appointment.
 * @property userId The ID of the user who has the appointment.
 * @property vaccinationId The ID of the vaccination for which the appointment is scheduled.
 *
 */
data class Appointments(
    val date : java.sql.Date? = null,
    val time : java.sql.Time? = null,
    val userId : Int? = null,
    val vaccinationId : Int? = null
)