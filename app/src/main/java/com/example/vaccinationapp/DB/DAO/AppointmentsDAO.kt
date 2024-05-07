package com.example.vaccinationapp.DB.DAO

import com.example.vaccinationapp.DB.entities.Appointments
import java.sql.Time
import java.sql.Date

interface AppointmentsDAO {
    fun addAppointment(appointment: Appointments): Boolean
    fun getAppointment(id: Int): Appointments?
    fun getAllAppointmentsForUserId(id: Int): Set<Appointments>?
    fun updateAppointment(id: Int, appointment: Appointments): Boolean
    fun deleteAppointment(id: Int): Boolean
    fun getAllAppointments(): Set<Appointments>?

    fun getAppointmentId(date: String, time: String): Int?
    fun getAllAppointmentsForDate(date: String): List<String>?
}