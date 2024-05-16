package com.example.vaccinationapp.DB.queries

import com.example.vaccinationapp.DB.DAO.HealthcareUnitsDAO
import com.example.vaccinationapp.DB.entities.HealthcareUnits
import java.sql.Connection
import java.sql.ResultSet

/**
 * Class that contains the queries for the HealthcareUnits table in the database.
 * @property connection The connection to the database.
 */
class HealthcareUnitsQueries(private val connection: Connection) : HealthcareUnitsDAO {

    /**
     * Adds a healthcare unit to the database.
     * @param healthcareUnit The healthcare unit to be added.
     * @return True if the healthcare unit was added successfully, false otherwise.
     */
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

    /**
     * Gets a healthcare unit from the database.
     * @param id The id of the healthcare unit to be retrieved.
     * @return The healthcare unit if it exists, null otherwise.
     */
    override fun getHealthcareUnit(id: Int): HealthcareUnits? {
        val query = "{CALL getHealthcareUnit(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) {
            mapResultSetToUnit(resultSet)
        } else {
            null
        }
    }

    /**
     * Gets a healthcare unit from the database.
     * @param name The name of the healthcare unit to be retrieved.
     * @return The healthcare unit if it exists, null otherwise.
     */
    override fun getHealthcareUnitId(name: String): Int? {
        val query = "{CALL getHealthcareUnitId(?)}"
        val statement = connection.prepareCall(query)
        statement.setString(1, name)
        val result = statement.executeQuery()
        return if (result.next()) {
            result.getInt("id")
        } else {
            null
        }
    }

    /**
     * Updates a healthcare unit in the database.
     * @param id The id of the healthcare unit to be updated.
     * @param healthcareUnit The new healthcare unit.
     * @return True if the healthcare unit was updated successfully, false otherwise.
     */
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

    /**
     * Deletes a healthcare unit from the database.
     * @param id The id of the healthcare unit to be deleted.
     * @return True if the healthcare unit was deleted successfully, false otherwise.
     */
    override fun deleteHealthcareUnit(id: Int): Boolean {
        val query = "{CALL deleteHealthcareUnit(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        return statement.executeUpdate() > 0
    }

    /**
     * Gets all healthcare units from the database.
     * @return A set with all the healthcare units if they exist, null otherwise.
     */
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

    /**
     * Maps a ResultSet to a HealthcareUnits object.
     * @param resultSet The ResultSet to be mapped.
     * @return The HealthcareUnits object.
     */
    private fun mapResultSetToUnit(resultSet: ResultSet):
            HealthcareUnits {
        return HealthcareUnits(
            name = resultSet.getString("name"),
            country = resultSet.getString("country"),
            city = resultSet.getString("city"),
            street = resultSet.getString("street"),
            streetNumber = resultSet.getString("street_number"),
            phone = resultSet.getString("phone"),
            email = resultSet.getString("email")
        )
    }
}