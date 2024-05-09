package com.example.vaccinationapp.ui

import android.util.Log
import com.example.vaccinationapp.DB.DBconnection
import com.example.vaccinationapp.DB.entities.Appointments
import com.example.vaccinationapp.DB.entities.HealthcareUnits
import com.example.vaccinationapp.DB.entities.Vaccinations
import com.example.vaccinationapp.DB.queries.AppointmentsQueries
import com.example.vaccinationapp.DB.queries.HealthcareUnitsQueries
import com.example.vaccinationapp.DB.queries.UsersQueries
import com.example.vaccinationapp.DB.queries.VaccinationsQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Queries class is a utility class that provides methods to interact with the database.
 */
class Queries {

    /**
     * addAppointment method is called to add an appointment to the database.
     * @param appointment The appointment to be added
     * @return A boolean value that indicates if the appointment was added successfully
     */
    suspend fun addAppointment(appointment: Appointments): Boolean {
        return withContext(Dispatchers.IO) {
            val conn = DBconnection.getConnection()
            Log.d("DATABASE", "appointment connected")
            val appQueries = AppointmentsQueries(conn)
            val result = appQueries.addAppointment(appointment)
            Log.d("DATABASE", "appointment added: $result")
            conn.close()
            result
        }
    }

    /**
     * addHealthcareUnit method is called to add a healthcare unit to the database.
     * @param healthcareUnit The healthcare unit to be added
     * @return A boolean value that indicates if the healthcare unit was added successfully
     */
    suspend fun getAllVaccines(): Set<Vaccinations>? {
        return withContext(Dispatchers.IO) {
            val connection = DBconnection.getConnection()
            Log.d("DATABASE", "vaccines connected")
            val vaccineQueries = VaccinationsQueries(connection)
            val result = vaccineQueries.getAllVaccinations()
            Log.d("DATABASE", "vaccines: $result")
            connection.close()
            result
        }
    }

    /**
     * addHealthcareUnit method is called to add a healthcare unit to the database.
     * @param healthcareUnit The healthcare unit to be added
     * @return A boolean value that indicates if the healthcare unit was added successfully
     */
    suspend fun getUserId(email: String): Int? {
        return withContext(Dispatchers.IO) {
            val conn = DBconnection.getConnection()
            val userQueries = UsersQueries(conn)
            val result = userQueries.getUserId(email)
            Log.d("DATABASE", "user ID: $result")
            conn.close()
            result
        }
    }

    /**
     * addHealthcareUnit method is called to add a healthcare unit to the database.
     * @param healthcareUnit The healthcare unit to be added
     * @return A boolean value that indicates if the healthcare unit was added successfully
     */
    suspend fun getVaccinationId(name: String, healthcareUnitId: Int): Int? {
        return withContext(Dispatchers.IO) {
            val conn = DBconnection.getConnection()
            val vaccQueries = VaccinationsQueries(conn)
            val result = vaccQueries.getVaccinationId(name, healthcareUnitId)
            conn.close()
            result
        }
    }

    /**
     * addHealthcareUnit method is called to add a healthcare unit to the database.
     * @param healthcareUnit The healthcare unit to be added
     * @return A boolean value that indicates if the healthcare unit was added successfully
     */
    suspend fun getHealthcareUnitId(name: String): Int? {
        return withContext(Dispatchers.IO) {
            val conn = DBconnection.getConnection()
            val unitQueries = HealthcareUnitsQueries(conn)
            val result = unitQueries.getHealthcareUnitId(name)
            conn.close()
            result
        }
    }

    /**
     * addHealthcareUnit method is called to add a healthcare unit to the database.
     * @param healthcareUnit The healthcare unit to be added
     * @return A boolean value that indicates if the healthcare unit was added successfully
     */
    suspend fun getHealthcareUnit(id: Int): HealthcareUnits? {
        return withContext(Dispatchers.IO) {
            val conn = DBconnection.getConnection()
            val HUqueries = HealthcareUnitsQueries(conn)
            val result = HUqueries.getHealthcareUnit(id)
            Log.d("DATABASE", "healthcare units: $result")
            conn.close()
            result
        }
    }

    /**
     * addHealthcareUnit method is called to add a healthcare unit to the database.
     * @param healthcareUnit The healthcare unit to be added
     * @return A boolean value that indicates if the healthcare unit was added successfully
     */
    suspend fun getAllAppointmentsForDate(date: String): List<String>? {
        return withContext(Dispatchers.IO) {
            val connection = DBconnection.getConnection()
            Log.d("DATABASE", "connected with date: $date")
            val appointmentQueries = AppointmentsQueries(connection)
            val result = appointmentQueries.getAllAppointmentsForDate(date)
            Log.d("DATABASE", "result: $result")
            connection.close()
            result
        }
    }

    /**
     * addHealthcareUnit method is called to add a healthcare unit to the database.
     * @param healthcareUnit The healthcare unit to be added
     * @return A boolean value that indicates if the healthcare unit was added successfully
     */
    suspend fun updateAppointment(id: Int, appointment: Appointments): Boolean {
        return withContext(Dispatchers.IO) {
            val conn = DBconnection.getConnection()
            val queries = AppointmentsQueries(conn)
            val result = queries.updateAppointment(id, appointment)
            conn.close()
            result
        }
    }

    /**
     * addHealthcareUnit method is called to add a healthcare unit to the database.
     * @param healthcareUnit The healthcare unit to be added
     * @return A boolean value that indicates if the healthcare unit was added successfully
     */
    suspend fun getAllAppointmentsForUserId(id: Int): Set<Appointments>? {
        return withContext(Dispatchers.IO) {
            val conn = DBconnection.getConnection()
            val appQueries = AppointmentsQueries(conn)
            val result = appQueries.getAllAppointmentsForUserId(id)
            conn.close()
            result
        }
    }

    /**
     * addHealthcareUnit method is called to add a healthcare unit to the database.
     * @param healthcareUnit The healthcare unit to be added
     * @return A boolean value that indicates if the healthcare unit was added successfully
     */
    suspend fun getAppointment(id: Int): Appointments? {
        return withContext(Dispatchers.IO) {
            val conn = DBconnection.getConnection()
            val query = AppointmentsQueries(conn)
            val result = query.getAppointment(id)
            conn.close()
            result
        }
    }

    /**
     * addHealthcareUnit method is called to add a healthcare unit to the database.
     * @param healthcareUnit The healthcare unit to be added
     * @return A boolean value that indicates if the healthcare unit was added successfully
     */
    suspend fun deleteAppointment(id: Int): Boolean {
        return withContext(Dispatchers.IO) {
            val conn = DBconnection.getConnection()
            val queries = AppointmentsQueries(conn)
            val result = queries.deleteAppointment(id)
            conn.close()
            result
        }
    }

    /**
     * addHealthcareUnit method is called to add a healthcare unit to the database.
     * @param healthcareUnit The healthcare unit to be added
     * @return A boolean value that indicates if the healthcare unit was added successfully
     */
    suspend fun getVaccination(id: Int): Vaccinations? {
        return withContext(Dispatchers.IO) {
            val conn = DBconnection.getConnection()
            val vaccQueries = VaccinationsQueries(conn)
            val result = vaccQueries.getVaccination(id)
            conn.close()
            result
        }
    }

    /**
     * addHealthcareUnit method is called to add a healthcare unit to the database.
     * @param healthcareUnit The healthcare unit to be added
     * @return A boolean value that indicates if the healthcare unit was added successfully
     */
    suspend fun getAppointmentId(date: String, time: String): Int? {
        return withContext(Dispatchers.IO) {
            val conn = DBconnection.getConnection()
            val queries = AppointmentsQueries(conn)
            val result = queries.getAppointmentId(date, time)
            conn.close()
            result
        }
    }
}