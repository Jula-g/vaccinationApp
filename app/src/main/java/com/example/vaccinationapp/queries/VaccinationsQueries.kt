package com.example.vaccinationapp.queries

import com.example.vaccinationapp.DAO.VaccinationsDAO
import com.example.vaccinationapp.entities.Vaccinations
import java.sql.Connection

class VaccinationsQueries(val connection: Connection) : VaccinationsDAO {
    override fun addVaccination(vaccination: Vaccinations) {
        val query = "INSERT INTO vaccinations (name, description, date, time) VALUES (?, ?, ?, ?)"
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, vaccination.name)
        preparedStatement.setString(2, vaccination.description)
        preparedStatement.setDate(3, vaccination.date)
        preparedStatement.setTime(4, vaccination.time)
        preparedStatement.executeUpdate()
    }

    override fun getVaccination(id: Int): Vaccinations? {
        val query = "SELECT * FROM vaccinations WHERE id = ?"
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setInt(1, id)
        val resultSet = preparedStatement.executeQuery()
        if (resultSet.next()) {
            return Vaccinations(
                id = resultSet.getInt("id"),
                name = resultSet.getString("name"),
                description = resultSet.getString("description"),
                date = resultSet.getDate("date"),
                time = resultSet.getTime("time")
            )
        }
        return null
    }

    override fun updateVaccination(vaccination: Vaccinations) {
        val query = "UPDATE vaccinations SET name = ?, description = ?, date = ?, time = ? WHERE id = ?"
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, vaccination.name)
        preparedStatement.setString(2, vaccination.description)
        preparedStatement.setDate(3, vaccination.date)
        preparedStatement.setTime(4, vaccination.time)
        preparedStatement.setInt(5, vaccination.id!!)
        preparedStatement.executeUpdate()
    }

    override fun deleteVaccination(id: Int) {
        val query = "DELETE FROM vaccinations WHERE id = ?"
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setInt(1, id)
        preparedStatement.executeUpdate()
    }

    override fun getAllVaccinations(): List<Vaccinations> {
        val query = "SELECT * FROM vaccinations"
        val preparedStatement = connection.prepareStatement(query)
        val resultSet = preparedStatement.executeQuery()
        val vaccinations = mutableListOf<Vaccinations>()
        while (resultSet.next()) {
            vaccinations.add(
                Vaccinations(
                    id = resultSet.getInt("id"),
                    name = resultSet.getString("name"),
                    description = resultSet.getString("description"),
                    date = resultSet.getDate("date"),
                    time = resultSet.getTime("time")
                )
            )
        }
        return vaccinations
    }
}