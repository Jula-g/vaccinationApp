package com.example.vaccinationapp.queries

import com.example.vaccinationapp.DAO.VaccinationsDAO
import com.example.vaccinationapp.entities.Vaccinations
import java.sql.Connection

class VaccinationsQueries(private val connection: Connection) : VaccinationsDAO {
    override fun addVaccination(vaccination: Vaccinations) {
        val query = "{CALL addVaccination(?, ?, ?, ?)}"
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setInt(1, vaccination.id!!)
        preparedStatement.setInt(2, vaccination.noOfDoses!!)
        preparedStatement.setString(3, vaccination.vaccineName)
        preparedStatement.setInt(4, vaccination.healthcareUnitId!!)
        preparedStatement.executeUpdate()
    }

    override fun getVaccination(id: Int): Vaccinations? {
        val query = "{CALL getVaccination(?)}"
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setInt(1, id)
        val resultSet = preparedStatement.executeQuery()
        if (resultSet.next()) {
            return Vaccinations(
                id = resultSet.getInt("id"),
                noOfDoses = resultSet.getInt("noOfDoses"),
                vaccineName = resultSet.getString("vaccineName"),
                healthcareUnitId = resultSet.getInt("healthcareUnitId")
            )
        }
        return null
    }

    override fun updateVaccination(vaccination: Vaccinations) {
        val query = "{CALL updateVaccination(?, ?, ?, ?)}"
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setInt(1, vaccination.id!!)
        preparedStatement.setInt(2, vaccination.noOfDoses!!)
        preparedStatement.setString(3, vaccination.vaccineName)
        preparedStatement.setInt(4, vaccination.healthcareUnitId!!)
        preparedStatement.executeUpdate()
    }

    override fun deleteVaccination(id: Int) {
        val query = "{CALL deleteVaccination(?)}"
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setInt(1, id)
        preparedStatement.executeUpdate()
    }

    override fun getAllVaccinations(): List<Vaccinations> {
        val query = "{CALL getAllVaccinations()}"
        val preparedStatement = connection.prepareStatement(query)
        val resultSet = preparedStatement.executeQuery()
        val vaccinations = mutableListOf<Vaccinations>()
        while (resultSet.next()) {
            vaccinations.add(
                Vaccinations(
                    id = resultSet.getInt("id"),
                    noOfDoses = resultSet.getInt("noOfDoses"),
                    vaccineName = resultSet.getString("vaccineName"),
                    healthcareUnitId = resultSet.getInt("healthcareUnitId")
                )
            )
        }
        return vaccinations
    }
}