package com.example.vaccinationapp.DB.entities

data class Appointments(
    var date : java.sql.Date? = null,
    val time : java.sql.Time? = null,
    val userId : Int? = null,
    val vaccinationId : Int? = null,
    val recordId: Int? = null
)