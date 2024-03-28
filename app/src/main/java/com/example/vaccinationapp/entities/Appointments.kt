package com.example.vaccinationapp.entities

data class Appointments(
    val id : Int? = null,
    val userId : Int? = null,
    val vaccinationId : Int? = null,
    val date : java.sql.Date? = null,
    val time : java.sql.Time? = null,
)