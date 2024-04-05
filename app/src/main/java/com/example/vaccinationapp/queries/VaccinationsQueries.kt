package com.example.vaccinationapp.queries

import com.example.vaccinationapp.DAO.VaccinationsDAO
import com.example.vaccinationapp.entities.Vaccinations
import java.sql.Connection
import java.sql.ResultSet

class VaccinationsQueries(private val connection: Connection) : VaccinationsDAO {
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

    override fun getVaccination(id: Int): Vaccinations? {
        val query = "{CALL getVaccination(?)}"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) {
            mapResultSetToVaccination(resultSet)
        } else{
            null
        }
    }

    override fun updateVaccination(id: Int, vaccination: Vaccinations): Boolean {
        val query = "{CALL updateVaccination(?, ?, ?, ?)}"
        val statement = connection.prepareStatement(query)
        statement.setString(1, vaccination.vaccineName)
        statement.setInt(2, vaccination.noOfDoses!!)
        statement.setInt(3, vaccination.healthcareUnitId!!)
        statement.setInt(4, id)
        return statement.executeUpdate() > 0
    }

    override fun deleteVaccination(id: Int): Boolean {
        val query = "{CALL deleteVaccination(?)}"
        val statement = connection.prepareStatement(query)
        statement.setInt(1, id)
        return statement.executeUpdate() > 0
    }

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

    private fun mapResultSetToVaccination(resultSet: ResultSet):
            Vaccinations {
        return Vaccinations(
            vaccineName = resultSet.getString("name"),
            noOfDoses = resultSet.getInt("number_of_doses"),
            healthcareUnitId = resultSet.getInt("healthcare_unit_id")
        )
    }
}