package com.example.vaccinationapp.queries

import android.annotation.SuppressLint
import com.example.vaccinationapp.DAO.AppointmentsDAO
import com.example.vaccinationapp.entities.Appointments
import java.sql.Connection
import java.sql.Date
import java.sql.ResultSet
import java.text.SimpleDateFormat

class AppointmentsQueries(private val connection: Connection) : AppointmentsDAO {
    override fun addAppointment(appointment: Appointments): Boolean {
        val query = "{CALL addAppointment(?, ?, ?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setDate(1, appointment.date!!)
        statement.setTime(2, appointment.time!!)
        statement.setInt(3, appointment.userId!!)
        statement.setInt(4, appointment.vaccinationId!!)
        val result = !statement.execute()
        statement.close()
        return result
    }

    override fun getAppointment(id: Int): Appointments? {
        val query = "{CALL getAppointment(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) {
           mapResultSetToAppointment(resultSet)
        }else {
            null
        }
    }

    override fun updateAppointment(id: Int, appointment: Appointments): Boolean {
        val query = "{CALL updateAppointment(?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setDate(1, appointment.date!!)
        statement.setTime(2, appointment.time!!)
        statement.setInt(3, appointment.vaccinationId!!)
        statement.setInt(4, appointment.userId!!)
        statement.setInt(5, id)
        return statement.executeUpdate() > 0
    }

    override fun deleteAppointment(id: Int): Boolean {
        val query = "{CALL deleteAppointment(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        return statement.executeUpdate() > 0
    }

    override fun getAllAppointments(): Set<Appointments>? {
        val query = "{CALL getAllAppointments()}"
        val statement = connection.prepareCall(query)
        val resultSet = statement.executeQuery()
        val appointments = mutableSetOf<Appointments>()
        while (resultSet.next()) {
            appointments.add(mapResultSetToAppointment(resultSet))
        }
        val appointmentsFinal = appointments.filterNotNull().toSet()
        return if (appointments.isEmpty()) null else appointmentsFinal
    }

    @SuppressLint("SimpleDateFormat")
    override fun getAllAppointmentsForDate(date: String): List<String>? {
        val sqlDateFormat = SimpleDateFormat("yyyy:MM:dd")
        val javaSqlDate = Date(sqlDateFormat.parse(date)!!.time)    //possible null exception

        val query = "{CALL getAllAppointmentsForDate(?)}"
        val statement = connection.prepareCall(query)
        statement.setDate(1,javaSqlDate)
        val resultSet = statement.executeQuery()
        val hours = mutableListOf<String>()
        while (resultSet.next()) {
            hours.add(resultSet.getString("time"))
        }
        val hoursFinal = hours.toList()
        return if (hours.isEmpty()) null else hoursFinal
    }

    private fun mapResultSetToAppointment(resultSet: ResultSet): Appointments {
        return Appointments(
            date = resultSet.getDate("date"),
            time = resultSet.getTime("time"),
            userId = resultSet.getInt("user_id"),
            vaccinationId = resultSet.getInt("vaccine_id")
        )
    }
}