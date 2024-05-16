package com.example.vaccinationapp.DB.DAO

import com.example.vaccinationapp.DB.entities.Records
import java.util.Date

interface RecordsDAO {
    fun addRecord(record: Records): Boolean
    fun updateRecord(id: Int, record: Records):Boolean
    fun getRecordId(userId: Int, vaccineId: Int, dose: Int, date: java.sql.Date): Int?
    fun getRecordIdByDate(userId: Int, vaccineId: Int, dateAdministered: java.sql.Date): Int?
    fun getRecord(id: Int): Records?
    fun getRecordByUserVaccDate(userId: Int, vaccineId: Int, dateAdministered: java.sql.Date): Records?
    fun getAllRecords(): Set<Records>?
    fun getAllRecordsForUserId(id: Int): Set<Records>?
    fun deleteRecord(id: Int): Boolean
}