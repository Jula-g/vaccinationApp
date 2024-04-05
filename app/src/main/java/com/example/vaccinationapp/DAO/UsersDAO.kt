package com.example.vaccinationapp.DAO

import com.example.vaccinationapp.entities.Users

interface UsersDAO {
    fun addUser(user: Users) : Boolean
    fun getUser(id: Int): Users?
    fun updateUser(id: Int, user: Users): Boolean
    fun deleteUser(id: Int) : Boolean
    fun getAllUsers(): Set<Users>?
}