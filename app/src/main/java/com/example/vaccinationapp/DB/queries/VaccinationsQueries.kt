package com.example.vaccinationapp.DB.queries

import com.example.vaccinationapp.DB.DAO.VaccinationsDAO
import com.example.vaccinationapp.DB.entities.Vaccinations
import java.sql.Connection
import java.sql.ResultSet

/**
 * Class that contains the queries for the Vaccinations table in the database.
 * @property connection The connection to the database.
 *
 */
class VaccinationsQueries(private val connection: Connection) : VaccinationsDAO {

    /**
     * Adds a vaccination to the database.
     * @param vaccination The vaccination to be added.
     * @return True if the vaccination was added successfully, false otherwise.
     */
    override fun addVaccination(vaccination: Vaccinations): Boolean {
        val query = "{CALL addVaccination(?, ?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setString(1, vaccination.vaccineName)
        statement.setInt(2, vaccination.noOfDoses!!)
        statement.setInt(3, vaccination.healthcareUnitId!!)
        val result = !statement.execute()
        statement.close()
        return result
    }

    /**
     * Gets a vaccination from the database.
     * @param id The id of the vaccination to be retrieved.
     * @return The vaccination if it exists, null otherwise.
     */
    override fun getVaccination(id: Int): Vaccinations? {
        val query = "{CALL getVaccination(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) {
            mapResultSetToVaccination(resultSet)
        } else {
            null
        }
    }

    /**
     * Gets a vaccination from the database.
     * @param name The name of the vaccination to be retrieved.
     * @param healthcareUnitId The id of the healthcare unit.
     * @return The id of the vaccination if it exists, null otherwise.
     */
    override fun getVaccinationId(name: String, healthcareUnitId: Int): Int? {
        val query = "{CALL getVaccinationId(?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setString(1, name)
        statement.setInt(2, healthcareUnitId)
        val result = statement.executeQuery()
        return if (result.next()) {
            result.getInt("id")
        } else {
            null
        }
    }

    /**
     * Updates a vaccination in the database.
     * @param id The id of the vaccination to be updated.
     * @param vaccination The new vaccination.
     * @return True if the vaccination was updated successfully, false otherwise.
     */
    override fun updateVaccination(id: Int, vaccination: Vaccinations): Boolean {
        val query = "{CALL updateVaccination(?, ?, ?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setString(1, vaccination.vaccineName)
        statement.setInt(2, vaccination.noOfDoses!!)
        statement.setInt(3, vaccination.healthcareUnitId!!)
        statement.setInt(4, id)
        return statement.executeUpdate() > 0
    }

    /**
     * Deletes a vaccination from the database.
     * @param id The id of the vaccination to be deleted.
     * @return True if the vaccination was deleted successfully, false otherwise.
     */
    override fun deleteVaccination(id: Int): Boolean {
        val query = "{CALL deleteVaccination(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        return statement.executeUpdate() > 0
    }

    /**
     * Gets all vaccinations from the database.
     * @return A set of all vaccinations if there are any, null otherwise.
     */
    override fun getAllVaccinations(): Set<Vaccinations>? {
        val query = "{CALL getAllVaccinations()}"
        val statement = connection.prepareCall(query)
        val resultSet = statement.executeQuery()
        val vaccinations = mutableSetOf<Vaccinations>()
        while (resultSet.next()) {
            vaccinations.add(mapResultSetToVaccination(resultSet))
        }
        val vaccineFinal = vaccinations.filterNotNull().toSet()
        return if (vaccinations.isEmpty()) null else vaccineFinal
    }

    /**
     * Maps a ResultSet to a Vaccination object.
     * @param resultSet The ResultSet to be mapped.
     * @return The Vaccination object.
     */
    private fun mapResultSetToVaccination(resultSet: ResultSet):
            Vaccinations {
        return Vaccinations(
            vaccineName = resultSet.getString("name"),
            noOfDoses = resultSet.getInt("number_of_doses"),
            healthcareUnitId = resultSet.getInt("healthcare_unit_id")
        )
    }
}