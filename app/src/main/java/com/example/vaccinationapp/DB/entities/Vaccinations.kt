package com.example.vaccinationapp.DB.entities

data class Vaccinations(
    val vaccineName : String? = null,
    val noOfDoses : Int? = null,
    val timeBetweenDoses : String? = null,
    val healthcareUnitId : Int? = null
)