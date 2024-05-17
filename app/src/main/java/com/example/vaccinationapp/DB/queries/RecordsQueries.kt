package com.example.vaccinationapp.DB.queries

import com.example.vaccinationapp.DB.DAO.RecordsDAO
import com.example.vaccinationapp.DB.entities.Records
import java.sql.Date
import java.sql.ResultSet

/**
 * This class is responsible for handling all the queries related to the Records table in the database.
 * It implements the RecordsDAO interface.
 * @param connection: Connection to the database.
 */
class RecordsQueries(private val connection: java.sql.Connection) : RecordsDAO {

    /**
     * This function is responsible for adding a record to the database.
     * @param record: Record to be added to the database.
     * @return Boolean: True if the record was added successfully, false otherwise.
     */
    override fun addRecord(record: Records): Boolean {
        val query = "{CALL addRecord(?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, record.userId!!)
        statement.setInt(2, record.vaccineId!!)
        statement.setDate(3, record.dateAdministered!!)
        statement.setInt(4, record.dose!!)
        statement.setDate(5, record.nextDoseDueDate)
        val result = !statement.execute()
        statement.close()
        return result
    }

    /**
     * This function is responsible for updating a record in the database.
     * @param id: Id of the record to be updated.
     * @param record: Record to be updated.
     * @return Boolean: True if the record was updated successfully, false otherwise.
     */
    override fun updateRecord(id: Int, record: Records): Boolean {
        val query = "{CALL updateRecord(?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, record.vaccineId!!)
        statement.setDate(2, record.dateAdministered)
        statement.setInt(3, record.dose!!)
        statement.setDate(4, record.nextDoseDueDate)
        statement.setInt(5, record.userId!!)
        return statement.executeUpdate() > 0
    }

    /**
     * This function is responsible for getting the record id from the database.
     * @param userId: Id of the user.
     * @param vaccineId: Id of the vaccine.
     * @param dose: Dose number.
     * @param date: Date the vaccine was administered.
     * @return Int: Id of the record.
     */
    override fun getRecordId(userId: Int, vaccineId: Int, dose: Int, date: Date): Int? {
        val query = "{CALL getRecordId(?, ?, ?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, userId)
        statement.setInt(2, vaccineId)
        statement.setInt(3, dose)
        statement.setDate(4, date)
        val result = statement.executeQuery()
        return if (result.next()) {
            result.getInt("id")
        } else {
            null
        }
    }

    /**
     * This function is responsible for getting the record by the user id, vaccine id and date administered.
     * @param userId: Id of the user.
     * @param vaccineId: Id of the vaccine.
     * @param dateAdministered: Date the vaccine was administered.
     * @return Records: Record object.
     */
    override fun getRecordByUserVaccDate(
        userId: Int,
        vaccineId: Int,
        dateAdministered: Date
    ): Records? {
        val query = "{CALL getRecordByUserVaccDate(?,?,?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, userId)
        statement.setInt(2, vaccineId)
        statement.setDate(3, dateAdministered)
        val result = statement.executeQuery()
        return if (result.next()) {
            mapResultSetToRecords(result)
        } else {
            null
        }
    }

    /**
     * This function is responsible for getting the record by the user id and vaccine id.
     * @param userId: Id of the user.
     * @param vaccineId: Id of the vaccine.
     * @return Records: Record object.
     */
    override fun getRecord(id: Int): Records? {
        val query = "{CALL getRecord(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        val result = statement.executeQuery()
        return if (result.next()) {
            mapResultSetToRecords(result)
        } else {
            null
        }
    }

    /**
     * This function is responsible for getting all the records from the database.
     * @return Set<Records>: Set of all the records.
     */
    override fun getAllRecords(): Set<Records>? {
        val query = "{CALL getAllRecords()}"
        val statement = connection.prepareCall(query)
        val result = statement.executeQuery()
        val recordsSet = mutableSetOf<Records>()
        while (result.next()) {
            recordsSet.add(mapResultSetToRecords(result))
        }
        val recordsFinal = recordsSet.toSet()
        return if (recordsSet.isEmpty()) null else recordsFinal
    }

    /**
     * This function is responsible for getting all the records for a user from the database.
     * @param id: Id of the user.
     * @return Set<Records>: Set of all the records for the user.
     */
    override fun getAllRecordsForUserId(id: Int): Set<Records>? {
        val query = "{CALL getAllRecordsForUserId(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        val result = statement.executeQuery()
        val recordsSet = mutableSetOf<Records>()
        while (result.next()) {
            recordsSet.add(mapResultSetToRecords(result))
        }
        val recordsFinal = recordsSet.toSet()
        return if (recordsSet.isEmpty()) null else recordsFinal
    }

    /**
     * This function is responsible for deleting a record from the database.
     * @param id: Id of the record to be deleted.
     * @return Boolean: True if the record was deleted successfully, false otherwise.
     */
    override fun deleteRecord(id: Int): Boolean {
        val query = "{CALL deleteRecord(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        return statement.executeUpdate() > 0

    }

    /**
     * This function is responsible for getting the record id by the user id, vaccine id and date administered.
     * @param userId: Id of the user.
     * @param vaccineId: Id of the vaccine.
     * @param dateAdministered: Date the vaccine was administered.
     * @return Int: Id of the record.
     */
    override fun getRecordIdByDate(userId: Int, vaccineId: Int, dateAdministered: Date): Int? {
        val query = "{CALL getRecordIdByDate(?, ?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, userId)
        statement.setInt(2, vaccineId)
        statement.setDate(3, dateAdministered)
        val result = statement.executeQuery()
        return if (result.next()) {
            result.getInt("id")
        } else {
            null
        }
    }

    /**
     * This function is responsible for getting the record id by the user id and vaccine id.
     * @param userId: Id of the user.
     * @param vaccineId: Id of the vaccine.
     * @return Int: Id of the record.
     */
    private fun mapResultSetToRecords(resultSet: ResultSet): Records {
        return Records(
            userId = resultSet.getInt("user_id"),
            vaccineId = resultSet.getInt("vaccine_id"),
            dateAdministered = resultSet.getDate("date_administered"),
            dose = resultSet.getInt("dose"),
            nextDoseDueDate = resultSet.getDate("next_dose_due_date")
        )
    }

}