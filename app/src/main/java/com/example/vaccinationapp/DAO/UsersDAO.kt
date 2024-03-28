package com.example.vaccinationapp.DAO

import com.example.vaccinationapp.entities.Users

interface UsersDAO {
    fun addUser(user: Users) : Boolean
    fun getUser(email: String): Users?
    fun updateUser(email: String, user: Users): Boolean
    fun deleteUser(email: String) : Boolean
    fun getAllUsers(): Set<Users>?
}