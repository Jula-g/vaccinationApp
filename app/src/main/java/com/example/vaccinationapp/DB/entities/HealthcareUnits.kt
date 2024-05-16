package com.example.vaccinationapp.DB.entities

/**
 * Data class representing a healthcare unit in the database.
 * @property name The name of the healthcare unit.
 * @property country The country where the healthcare unit is located.
 * @property city The city where the healthcare unit is located.
 * @property street The street where the healthcare unit is located.
 * @property streetNumber The street number where the healthcare unit is located.
 * @property phone The phone number of the healthcare unit.
 * @property email The email address of the healthcare unit.
 */
data class HealthcareUnits(
    val name : String? = null,
    val country : String? = null,
    val city : String? = null,
    val street : String? = null,
    val streetNumber : String? = null,
    val phone : String? = null,
    val email : String? = null
)