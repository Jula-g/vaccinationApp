package com.example.vaccinationapp.DB.entities

/**
 * Data class representing a user in the database.
 * @property firstName The first name of the user.
 * @property lastName The last name of the user.
 * @property email The email address of the user.
 */
data class Users(
    val firstName : String? = null,
    val lastName : String? = null,
    val email : String? = null
)