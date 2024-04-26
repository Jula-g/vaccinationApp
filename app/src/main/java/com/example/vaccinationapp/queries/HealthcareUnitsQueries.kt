package com.example.vaccinationapp.queries

import com.example.vaccinationapp.DAO.HealthcareUnitsDAO
import com.example.vaccinationapp.entities.HealthcareUnits
import java.sql.Connection
import java.sql.ResultSet

class HealthcareUnitsQueries(private val connection: Connection) : HealthcareUnitsDAO {
    override fun addHealthcareUnit(healthcareUnit: HealthcareUnits): Boolean {
        val query = "{CALL addHealthcareUnit(?, ?, ?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setString(1, healthcareUnit.name)
        statement.setString(2, healthcareUnit.country)
        statement.setString(3, healthcareUnit.city)
        statement.setString(4, healthcareUnit.street)
        statement.setString(5, healthcareUnit.streetNumber)
        statement.setString(6, healthcareUnit.phone)
        statement.setString(7, healthcareUnit.email)
        val result = !statement.execute()
        statement.close()
        return result
    }

    override fun getHealthcareUnit(id: Int): HealthcareUnits? {
        val query = "{CALL getHealthcareUnit(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) {
            mapResultSetToUnit(resultSet)
        }else{
            null
        }
    }

    override fun getHalthcareUnitId(name: String): Int? {
        val query = "{CALL getHealthcareUnitId(?)}"
        val statement = connection.prepareCall(query)
        statement.setString(1, name)
        val result = statement.executeQuery()
        return if(result.next()){
            result.getInt("id")
        }else{
            null
        }
    }

    override fun updateHealthcareUnit(id: Int, healthcareUnit: HealthcareUnits): Boolean {
        val query = "{CALL updateHealthcareUnit(?, ?, ?, ?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setString(1, healthcareUnit.name)
        statement.setString(2, healthcareUnit.country)
        statement.setString(3, healthcareUnit.city)
        statement.setString(4, healthcareUnit.street)
        statement.setString(5, healthcareUnit.streetNumber)
        statement.setString(6, healthcareUnit.phone)
        statement.setString(7, healthcareUnit.email)
        statement.setInt(8, id)
        return statement.executeUpdate() > 0
    }

    override fun deleteHealthcareUnit(id: Int): Boolean {
        val query = "{CALL deleteHealthcareUnit(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        return statement.executeUpdate() > 0
    }

    override fun getAllHealthcareUnits(): Set<HealthcareUnits>? {
        val query = "{CALL getAllHealthcareUnits()}"
        val statement = connection.prepareCall(query)
        val resultSet = statement.executeQuery()
        val healthcareUnits = mutableSetOf<HealthcareUnits>()
        while (resultSet.next()) {
            healthcareUnits.add(mapResultSetToUnit(resultSet))
        }
        val unitsFinal = healthcareUnits.filterNotNull().toSet()
        return if (healthcareUnits.isEmpty()) null else unitsFinal
    }

    private fun mapResultSetToUnit(resultSet: ResultSet):
            HealthcareUnits {
        return HealthcareUnits(
            name = resultSet.getString("name"),
            country = resultSet.getString("country"),
            city = resultSet.getString("city"),
            street = resultSet.getString("street"),
            streetNumber = resultSet.getString("street_number"),
            phone =  resultSet.getString("phone"),
            email = resultSet.getString("email")
        )
    }
}