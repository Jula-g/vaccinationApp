package com.example.vaccinationapp.DB.entities

/**
 * Data class representing a vaccination in the database.
 * @property vaccineName The name of the vaccine.
 * @property noOfDoses The number of doses required for the vaccine.
 * @property healthcareUnitId The ID of the healthcare unit where the vaccine is available.
 */
data class Vaccinations(
    val vaccineName : String? = null,
    val noOfDoses : Int? = null,
    val healthcareUnitId : Int? = null
)