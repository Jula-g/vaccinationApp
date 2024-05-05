package com.example.vaccinationapp.DAO

import com.example.vaccinationapp.entities.Appointments

interface AppointmentsDAO {
    fun addAppointment(appointment: Appointments): Boolean
    fun getAppointment(id: Int): Appointments?
    fun getAllAppointmentsForUserId(id: Int): Set<Appointments>?
    fun getAppointmentId(date: String, time: String): Int?
    fun updateAppointment(id: Int, appointment: Appointments): Boolean
    fun deleteAppointment(id: Int): Boolean
    fun getAllAppointments(): Set<Appointments>?
    fun getAllAppointmentsForDate(date: String): List<String>?
}