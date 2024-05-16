package com.example.vaccinationapp.DB.DAO

import com.example.vaccinationapp.DB.entities.Appointments
import java.sql.Time
import java.sql.Date

interface AppointmentsDAO {
    fun addAppointment(appointment: Appointments, nextDose: Date?): Boolean
    fun getAppointment(id: Int): Appointments?
    fun getAllAppointmentsForUserId(id: Int): Set<Appointments>?
    fun getAppointmentsForUserAndVaccine(userId: Int, vaccineId: Int): Set<Appointments>?
    fun getAppointmentId(date: String, time: String): Int?
    fun updateAppointment(id: Int, nextDose: Date?, appointment: Appointments): Boolean
    fun deleteAppointment(id: Int): Boolean
    fun getAllAppointments(): Set<Appointments>?
    fun getAllAppointmentsForDate(date: String): List<String>?
}