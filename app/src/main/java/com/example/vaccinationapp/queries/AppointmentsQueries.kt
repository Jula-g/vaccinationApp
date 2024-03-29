package com.example.vaccinationapp.queries

import com.example.vaccinationapp.DAO.AppointmentsDAO
import com.example.vaccinationapp.entities.Appointments
import java.sql.Connection

class AppointmentsQueries(private val connection: Connection) : AppointmentsDAO {
    override fun addAppointment(appointment: Appointments) {
        val query = "{CALL addAppointment(?, ?, ?, ?, ?)}"
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setInt(1, appointment.id!!)
        preparedStatement.setInt(2, appointment.userId!!)
        preparedStatement.setInt(3, appointment.vaccinationId!!)
        preparedStatement.setDate(4, appointment.date!!)
        preparedStatement.setTime(5, appointment.time!!)
        preparedStatement.executeUpdate()
    }

    override fun getAppointment(id: Int): Appointments? {
        val query = "{CALL getAppointment(?)}"
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setInt(1, id)
        val resultSet = preparedStatement.executeQuery()
        if (resultSet.next()) {
            return Appointments(
                resultSet.getInt("id"),
                resultSet.getInt("userId"),
                resultSet.getInt("vaccinationId"),
                resultSet.getDate("date"),
                resultSet.getTime("time")
            )
        }
        return null
    }

    override fun updateAppointment(appointment: Appointments) {
        val query = "{CALL updateAppointment(?, ?, ?, ?, ?)}"
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setInt(1, appointment.userId!!)
        preparedStatement.setInt(2, appointment.vaccinationId!!)
        preparedStatement.setDate(3, appointment.date!!)
        preparedStatement.setTime(4, appointment.time!!)
        preparedStatement.executeUpdate()
    }

    override fun deleteAppointment(id: Int) {
        val query = "{CALL deleteAppointment(?)}"
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setInt(1, id)
        preparedStatement.executeUpdate()
    }

    override fun getAllAppointments(): List<Appointments> {
        val query = "{CALL getAllAppointments()}"
        val preparedStatement = connection.prepareStatement(query)
        val resultSet = preparedStatement.executeQuery()
        val appointments = mutableListOf<Appointments>()
        while (resultSet.next()) {
            appointments.add(
                Appointments(
                    resultSet.getInt("id"),
                    resultSet.getInt("userId"),
                    resultSet.getInt("vaccinationId"),
                    resultSet.getDate("date"),
                    resultSet.getTime("time")
                )
            )
        }
        return appointments
    }
}