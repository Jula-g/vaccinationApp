package com.example.vaccinationapp.DB.entities

data class Records (
    val userId : Int? = null,
    val vaccineId: Int? = null,
    val dateAdministered : java.sql.Date? = null,
    val dose : Int? = null,
    val nextDoseDueDate : java.sql.Date? = null
)