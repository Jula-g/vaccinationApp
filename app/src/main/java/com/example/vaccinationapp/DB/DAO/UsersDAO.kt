package com.example.vaccinationapp.DB.DAO

import com.example.vaccinationapp.DB.entities.Users

/**
 * Interface for managing the users in the database.
 */

interface UsersDAO {

    /**
     * Adds a new user to the database.
     *
     * @param user The user to add.
     * @return true if the user was added successfully, false otherwise.
     */
    fun addUser(user: Users): Boolean

    /**
     * Retrieves a user from the database.
     *
     * @param id The ID of the user to retrieve.
     * @return The user with the given ID, or null if no such user exists.
     */
    fun getUser(id: Int): Users?

    /**
     * Retrieves a user from the database.
     *
     * @param email The email of the user to retrieve.
     * @return The user with the given email, or null if no such user exists.
     */
    fun getUserByEmail(email: String): Users?

    /**
     * Retrieves the ID of a user with the given email.
     *
     * @param email The email of the user.
     * @return The ID of the user with the given email, or null if no such user exists.
     */
    fun getUserId(email: String): Int?

    /**
     * Updates an existing user in the database.
     *
     * @param id The ID of the user to update.
     * @param user The updated user.
     * @return true if the user was updated successfully, false otherwise.
     */
    fun updateUser(id: Int, user: Users): Boolean

    /**
     * Deletes a user from the database.
     *
     * @param id The ID of the user to delete.
     * @return true if the user was deleted successfully, false otherwise.
     */
    fun deleteUser(id: Int): Boolean

    /**
     * Deletes a user from the database.
     *
     * @param email The email of the user to delete.
     * @return true if the user was deleted successfully, false otherwise.
     */
    fun deleteUserByEmail(email: String): Boolean

    /**
     * Retrieves all users from the database.
     *
     * @return A set of all users in the database, or null if no users exist.
     */
    fun getAllUsers(): Set<Users>?
}