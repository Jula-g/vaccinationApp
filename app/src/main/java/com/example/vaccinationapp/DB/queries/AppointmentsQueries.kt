package com.example.vaccinationapp.DB.queries

import android.annotation.SuppressLint
import com.example.vaccinationapp.DB.DAO.AppointmentsDAO
import com.example.vaccinationapp.DB.entities.Appointments
import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet
import java.sql.Time
import java.text.SimpleDateFormat

/**
 * Class that contains the queries for the Appointments table in the database.
 * @property connection The connection to the database.
 */
class AppointmentsQueries(private val connection: Connection) : AppointmentsDAO {

    /**
     * Adds an appointment to the database.
     * @param appointment The appointment to be added.
     * @return True if the appointment was added successfully, false otherwise.
     */
    override fun addAppointment(appointment: Appointments, nextDose: Date?): Boolean {
        val query = "{CALL addAppointment(?, ?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setDate(1, appointment.date!!)
        statement.setTime(2, appointment.time!!)
        statement.setInt(3, appointment.userId!!)
        statement.setInt(4, appointment.vaccinationId!!)
        if (appointment.recordId != null) {
            statement.setInt(5, appointment.recordId)
        } else {
            statement.setNull(5, java.sql.Types.INTEGER)
        }
        statement.setDate(6,nextDose)
        val result = !statement.execute()
        statement.close()
        return result
    }

    /**
     * Gets an appointment from the database.
     * @param id The id of the appointment to be retrieved.
     * @return The appointment if it exists, null otherwise.
     */
    override fun getAppointment(id: Int): Appointments? {
        val query = "{CALL getAppointment(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) {
            mapResultSetToAppointment(resultSet)
        } else {
            null
        }
    }

    /**
     * Updates an appointment in the database.
     * @param id The id of the appointment to be updated.
     * @param appointment The new appointment.
     * @return True if the appointment was updated successfully, false otherwise.
     */

    override fun updateAppointment(id: Int, nextDose: Date?, appointment: Appointments): Boolean {
        val query = "{CALL updateAppointment(?, ?, ?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setDate(1, appointment.date!!)
        statement.setTime(2, appointment.time!!)
        statement.setInt(3, appointment.vaccinationId!!)
        statement.setInt(4, appointment.userId!!)
        statement.setInt(5, appointment.recordId!!)
        statement.setInt(6, id)
        statement.setDate(7, nextDose)
        return statement.executeUpdate() > 0
    }

    /**
     * Deletes an appointment from the database.
     * @param id The id of the appointment to be deleted.
     * @return True if the appointment was deleted successfully, false otherwise.
     */
    override fun deleteAppointment(id: Int): Boolean {
        val query = "{CALL deleteAppointment(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        return statement.executeUpdate() > 0
    }

    /**
     * Gets all appointments from the database.
     * @return A set of appointments if there are any, null otherwise.
     */
    override fun getAllAppointments(): Set<Appointments>? {
        val query = "{CALL getAllAppointments()}"
        val statement = connection.prepareCall(query)
        val resultSet = statement.executeQuery()
        val appointments = mutableSetOf<Appointments>()
        while (resultSet.next()) {
            appointments.add(mapResultSetToAppointment(resultSet))
        }
        val appointmentsFinal = appointments.toSet()
        return if (appointments.isEmpty()) null else appointmentsFinal
    }

    /**
     * Gets all appointments for a specific user from the database.
     * @param id The id of the user.
     * @return A set of appointments if there are any, null otherwise.
     */
    override fun getAllAppointmentsForUserId(id: Int): Set<Appointments>? {
        val query = "{CALL getAllAppointmentsForUserId(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()
        val appointments = mutableSetOf<Appointments>()
        while (resultSet.next()) {
            appointments.add(mapResultSetToAppointment(resultSet))
        }
        val appointmentsFinal = appointments.toSet()
        return if (appointments.isEmpty()) null else appointmentsFinal
    }

    override fun getAppointmentsForUserAndVaccine(userId: Int, vaccineId: Int): Set<Appointments>?{
        val query = "{CALL getAppointmentsForUserAndVaccine(?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, userId)
        statement.setInt(2, vaccineId)
        val result = statement.executeQuery()
        val appointments = mutableListOf<Appointments>()
        while (result.next()){
            appointments.add(mapResultSetToAppointment(result))
        }
        val appointmentsFinal = appointments.toSet()
        return if (appointments.isEmpty()) null else appointmentsFinal
    }

    /**
     * Gets all appointments for a specific date from the database.
     * @param date The date of the appointments.
     * @return A list of appointments if there are any, null otherwise.
     */
    @SuppressLint("SimpleDateFormat")
    override fun getAllAppointmentsForDate(date: String): List<String>? {
        val sqlDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val javaSqlDate = Date(sqlDateFormat.parse(date)!!.time)

        val query = "{CALL getAllAppointmentsForDate(?)}"
        val statement = connection.prepareCall(query)
        statement.setDate(1, javaSqlDate)
        val resultSet = statement.executeQuery()
        val hours = mutableListOf<String>()
        while (resultSet.next()) {
            hours.add(resultSet.getString("time"))
        }
        val hoursFinal = hours.toList()
        return if (hours.isEmpty()) null else hoursFinal
    }

    /**
     * Gets the id of an appointment from the database.
     * @param date The date of the appointment.
     * @param time The time of the appointment.
     * @return The id of the appointment if it exists, null otherwise.
     */
    @SuppressLint("SimpleDateFormat")
    override fun getAppointmentId(date: String, time: String): Int? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val timeFormat = SimpleDateFormat("HH:mm")

        val fdate = Date(dateFormat.parse(date)!!.time)
        val ftime = Time(timeFormat.parse(time)!!.time)

        val query = "{CALL getAppointmentId(?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setDate(1, fdate)
        statement.setTime(2, ftime)
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) {
            resultSet.getInt("id")
        } else {
            null
        }
    }

    /**
     * Maps a ResultSet to an Appointments object.
     * @param resultSet The ResultSet to be mapped.
     * @return The Appointments object.
     */
    private fun mapResultSetToAppointment(resultSet: ResultSet): Appointments {
        return Appointments(
            date = resultSet.getDate("date"),
            time = resultSet.getTime("time"),
            userId = resultSet.getInt("user_id"),
            vaccinationId = resultSet.getInt("vaccine_id"),
            recordId = resultSet.getInt("record_id")
        )
    }
}