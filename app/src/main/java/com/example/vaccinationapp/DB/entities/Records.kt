package com.example.vaccinationapp.DB.entities

/**
 * Data class representing a record in the database.
 * @property userId The ID of the user who has the record.
 * @property vaccineId The ID of the vaccine that the record is for.
 * @property dateAdministered The date the vaccine was administered.
 * @property dose The dose number of the vaccine.
 * @property nextDoseDueDate The date the next dose is due.
 */
data class Records (
    val userId : Int? = null,
    val vaccineId: Int? = null,
    val dateAdministered : java.sql.Date? = null,
    val dose : Int? = null,
    val nextDoseDueDate : java.sql.Date? = null
)