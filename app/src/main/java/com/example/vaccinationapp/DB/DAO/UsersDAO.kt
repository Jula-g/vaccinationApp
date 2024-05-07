package com.example.vaccinationapp.DB.DAO

import com.example.vaccinationapp.DB.entities.Users

interface UsersDAO {
    fun addUser(user: Users) : Boolean
    fun getUser(id: Int): Users?
    fun getUserByEmail(email: String): Users?
    fun getUserId(email: String): Int?
    fun updateUser(id: Int, user: Users): Boolean
    fun deleteUser(id: Int) : Boolean
    fun deleteUserByEmail(email: String) : Boolean
    fun getAllUsers(): Set<Users>?
}