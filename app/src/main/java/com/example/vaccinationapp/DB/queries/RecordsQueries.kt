package com.example.vaccinationapp.DB.queries

import com.example.vaccinationapp.DB.DAO.RecordsDAO
import com.example.vaccinationapp.DB.entities.Records
import java.sql.Date
import java.sql.ResultSet

class RecordsQueries(private val connection: java.sql.Connection): RecordsDAO {

    override fun addRecord(record: Records): Boolean{
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

    override fun getRecordId(userId: Int, vaccineId: Int, dose: Int, date: Date): Int? {
        val query = "{CALL getRecordId(?, ?, ?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, userId)
        statement.setInt(2, vaccineId)
        statement.setInt(3, dose)
        statement.setDate(4, date)
        val result = statement.executeQuery()
        return if (result.next()){
            result.getInt("id")
        }else{
            null
        }
    }

    override fun getRecordByUserVaccDate(
        userId: Int,
        vaccineId: Int,
        dateAdministered: Date
    ): Records? {
        val query = "{CALL getRecordByUserVaccDate(?,?,?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1,userId)
        statement.setInt(2, vaccineId)
        statement.setDate(3, dateAdministered)
        val result = statement.executeQuery()
        return if (result.next()){
            mapResultSetToRecords(result)
        }else{
            null
        }
    }

    override fun getRecord(id: Int): Records? {
        val query = "{CALL getRecord(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        val result = statement.executeQuery()
        return if (result.next()){
            mapResultSetToRecords(result)
        }else{
            null
        }
    }

    override fun getAllRecords(): Set<Records>? {
        val query = "{CALL getAllRecords()}"
        val statement = connection.prepareCall(query)
        val result = statement.executeQuery()
        val recordsSet = mutableSetOf<Records>()
        while (result.next()){
            recordsSet.add(mapResultSetToRecords(result))
        }
        val recordsFinal = recordsSet.toSet()
        return if (recordsSet.isEmpty()) null else recordsFinal
    }

    override fun getAllRecordsForUserId(id: Int): Set<Records>?{
        val query = "{CALL getAllRecordsForUserId(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        val result = statement.executeQuery()
        val recordsSet = mutableSetOf<Records>()
        while (result.next()){
            recordsSet.add(mapResultSetToRecords(result))
        }
        val recordsFinal = recordsSet.toSet()
        return if (recordsSet.isEmpty()) null else recordsFinal
    }

    override fun deleteRecord(id: Int): Boolean {
        val query = "{CALL deleteRecord(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        return statement.executeUpdate() > 0

    }

    override fun getRecordIdByDate(userId: Int, vaccineId: Int, dateAdministered: Date): Int? {
        val query = "{CALL getRecordIdByDate(?, ?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, userId)
        statement.setInt(2, vaccineId)
        statement.setDate(3, dateAdministered)
        val result = statement.executeQuery()
        return if (result.next()){
            result.getInt("id")
        }else{
            null
        }
    }

    private fun mapResultSetToRecords(resultSet: ResultSet): Records{
        return Records(
            userId = resultSet.getInt("user_id"),
            vaccineId = resultSet.getInt("vaccine_id"),
            dateAdministered = resultSet.getDate("date_administered"),
            dose = resultSet.getInt("dose"),
            nextDoseDueDate = resultSet.getDate("next_dose_due_date")
        )
    }

}