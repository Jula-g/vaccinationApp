package com.example.vaccinationapp.DAO

import com.example.vaccinationapp.entities.Appointments

interface AppointmentsDAO {
    fun addAppointment(appointment: Appointments)
    fun getAppointment(id: Int): Appointments?
    fun updateAppointment(appointment: Appointments)
    fun deleteAppointment(id: Int)
    fun getAllAppointments(): List<Appointments>
}