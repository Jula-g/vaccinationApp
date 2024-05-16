package com.example.vaccinationapp.DB.DAO

import com.example.vaccinationapp.DB.entities.Records
import java.util.Date

/**
 * Interface for managing the records in the database.
 */
interface RecordsDAO {

    /**
     * Adds a new record to the database.
     *
     * @param record The record to add.
     * @return true if the record was added successfully, false otherwise.
     */
    fun addRecord(record: Records): Boolean

    /**
     * Updates an existing record in the database.
     *
     * @param id The ID of the record to update.
     * @param record The updated record.
     * @return true if the record was updated successfully, false otherwise.
     */
    fun updateRecord(id: Int, record: Records): Boolean

    /**
     * Retrieves the ID of a record with the given user ID, vaccine ID, dose, and date.
     *
     * @param userId The ID of the user.
     * @param vaccineId The ID of the vaccine.
     * @param dose The dose number.
     * @param date The date the vaccine was administered.
     * @return The ID of the record with the given user ID, vaccine ID, dose, and date, or null if no such record exists.
     */
    fun getRecordId(userId: Int, vaccineId: Int, dose: Int, date: java.sql.Date): Int?

    /**
     * Retrieves the ID of a record with the given user ID, vaccine ID, and date.
     *
     * @param userId The ID of the user.
     * @param vaccineId The ID of the vaccine.
     * @param dateAdministered The date the vaccine was administered.
     * @return The ID of the record with the given user ID, vaccine ID, and date, or null if no such record exists.
     */
    fun getRecordIdByDate(userId: Int, vaccineId: Int, dateAdministered: java.sql.Date): Int?

    /**
     * Retrieves a record from the database.
     *
     * @param id The ID of the record to retrieve.
     * @return The record with the given ID, or null if no such record exists.
     */
    fun getRecord(id: Int): Records?

    /**
     * Retrieves a record from the database.
     *
     * @param userId The ID of the user.
     * @param vaccineId The ID of the vaccine.
     * @param dateAdministered The date the vaccine was administered.
     * @return The record with the given user ID, vaccine ID, and date, or null if no such record exists.
     */
    fun getRecordByUserVaccDate(
        userId: Int,
        vaccineId: Int,
        dateAdministered: java.sql.Date
    ): Records?

    /**
     * Retrieves all records from the database.
     *
     * @return A set of all records in the database, or null if no records exist.
     */
    fun getAllRecords(): Set<Records>?

    /**
     * Retrieves all records for a given user.
     *
     * @param id The ID of the user.
     * @return A set of all records for the given user, or null if no such records exist.
     */
    fun getAllRecordsForUserId(id: Int): Set<Records>?

    /**
     * Deletes a record from the database.
     *
     * @param id The ID of the record to delete.
     * @return true if the record was deleted successfully, false otherwise.
     */
    fun deleteRecord(id: Int): Boolean
}