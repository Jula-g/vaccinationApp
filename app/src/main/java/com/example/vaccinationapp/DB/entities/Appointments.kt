package com.example.vaccinationapp.DB.entities

data class Appointments(
    val date : java.sql.Date? = null,
    val time : java.sql.Time? = null,
    val userId : Int? = null,
    val vaccinationId : Int? = null,
)