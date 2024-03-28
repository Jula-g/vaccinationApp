package com.example.vaccinationapp.queries

import com.example.vaccinationapp.DAO.HealthcareUnitsDAO
import com.example.vaccinationapp.entities.HealthcareUnits
import java.sql.Connection

class HealthcareUnitsQueries(private val connection: Connection) : HealthcareUnitsDAO {
    override fun addHealthcareUnit(healthcareUnit: HealthcareUnits) {
        val query = "{CALL addHealthcareUnit(?, ?, ?, ?, ?, ?, ?, ?)}"
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setInt(1, healthcareUnit.id!!)
        preparedStatement.setString(2, healthcareUnit.name)
        preparedStatement.setString(3, healthcareUnit.country)
        preparedStatement.setString(4, healthcareUnit.city)
        preparedStatement.setString(5, healthcareUnit.street)
        preparedStatement.setString(6, healthcareUnit.streetNumber)
        preparedStatement.setString(7, healthcareUnit.phone)
        preparedStatement.setString(8, healthcareUnit.email)
        preparedStatement.executeUpdate()
    }

    override fun getHealthcareUnit(id: Int): HealthcareUnits? {
        val query = "{CALL getHealthcareUnit(?)}"
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setInt(1, id)
        val resultSet = preparedStatement.executeQuery()
        if (resultSet.next()) {
            return HealthcareUnits(
                resultSet.getInt("id"),
                resultSet.getString("name"),
                resultSet.getString("country"),
                resultSet.getString("city"),
                resultSet.getString("street"),
                resultSet.getString("streetNumber"),
                resultSet.getString("phone"),
                resultSet.getString("email")
            )
        }
        return null
    }

    override fun updateHealthcareUnit(healthcareUnit: HealthcareUnits) {
        val query = "{CALL updateHealthcareUnit(?, ?, ?, ?, ?, ?, ?, ?)}"
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setString(1, healthcareUnit.name)
        preparedStatement.setString(2, healthcareUnit.country)
        preparedStatement.setString(3, healthcareUnit.city)
        preparedStatement.setString(4, healthcareUnit.street)
        preparedStatement.setString(5, healthcareUnit.streetNumber)
        preparedStatement.setString(6, healthcareUnit.phone)
        preparedStatement.setString(7, healthcareUnit.email)
        preparedStatement.setInt(8, healthcareUnit.id!!)
        preparedStatement.executeUpdate()
    }

    override fun deleteHealthcareUnit(id: Int) {
        val query = "{CALL deleteHealthcareUnit(?)}"
        val preparedStatement = connection.prepareStatement(query)
        preparedStatement.setInt(1, id)
        preparedStatement.executeUpdate()
    }

    override fun getAllHealthcareUnits(): List<HealthcareUnits> {
        val query = "{CALL getAllHealthcareUnits()}"
        val preparedStatement = connection.prepareStatement(query)
        val resultSet = preparedStatement.executeQuery()
        val healthcareUnits = mutableListOf<HealthcareUnits>()
        while (resultSet.next()) {
            healthcareUnits.add(
                HealthcareUnits(
                    resultSet.getInt("id"),
                    resultSet.getString("name"),
                    resultSet.getString("country"),
                    resultSet.getString("city"),
                    resultSet.getString("street"),
                    resultSet.getString("streetNumber"),
                    resultSet.getString("phone"),
                    resultSet.getString("email")
                )
            )
        }
        return healthcareUnits
    }
}