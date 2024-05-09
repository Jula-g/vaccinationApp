package com.example.vaccinationapp.DB.queries

import com.example.vaccinationapp.DB.DAO.UsersDAO
import com.example.vaccinationapp.DB.entities.Users
import java.sql.Connection
import java.sql.ResultSet

/**
 * Class that contains the queries for the Users table in the database.
 * @property connection The connection to the database.
 */
class UsersQueries(private val connection: Connection) : UsersDAO {

    /**
     * Gets a user from the database.
     * @param id The id of the user to be retrieved.
     * @return The user if it exists, null otherwise.
     */
    override fun getUser(id: Int): Users? {
        val query = "{CALL getUser(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) {
            mapResultSetToUser(resultSet)
        } else {
            null
        }
    }

    /**
     * Gets a user from the database.
     * @param email The email of the user to be retrieved.
     * @return The user if it exists, null otherwise.
     */
    override fun getUserByEmail(email: String): Users? {
        val query = "{CALL getUserByEmail(?)}"
        val statement = connection.prepareCall(query)
        statement.setString(1, email)
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) {
            mapResultSetToUser(resultSet)
        } else {
            null
        }
    }

    /**
     * Gets the id of a user from the database.
     * @param email The email of the user.
     * @return The id of the user if it exists, null otherwise.
     */
    override fun getUserId(email: String): Int? {
        val query = "{CALL getUserByEmail(?)}"
        val statement = connection.prepareCall((query))
        statement.setString(1, email)
        val resultSet = statement.executeQuery()
        return if (resultSet.next()) {
            resultSet.getInt("id")
        } else {
            null
        }
    }

    /**
     * Gets all users from the database.
     * @return A set of all users if there are any, null otherwise.
     */
    override fun getAllUsers(): Set<Users>? {
        val query = "{CALL getAllUsers()}"
        val statement = connection.prepareCall(query)
        val resultSet = statement.executeQuery()
        val users = mutableSetOf<Users?>()
        while (resultSet.next()) {
            users.add(mapResultSetToUser(resultSet))
        }
        val usersFinal = users.filterNotNull().toSet()
        return if (users.isEmpty()) null else usersFinal
    }

    /**
     * Updates a user in the database.
     * @param id The id of the user to be updated.
     * @param user The new user.
     * @return True if the user was updated successfully, false otherwise.
     */
    override fun updateUser(id: Int, user: Users): Boolean {
        val query = "{CALL updateUser(?, ?, ?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setString(1, user.firstName)
        statement.setString(2, user.lastName)
        statement.setString(3, user.email)
        statement.setInt(4, id)
        return statement.executeUpdate() > 0
    }

    /**
     * Adds a user to the database.
     * @param user The user to be added.
     * @return True if the user was added successfully, false otherwise.
     */
    override fun addUser(user: Users): Boolean {
        val call = "{CALL addUser(?, ?, ?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, user.firstName)
        statement.setString(2, user.lastName)
        statement.setString(3, user.email)
        val result = !statement.execute()
        statement.close()
        return result
    }

    /**
     * Deletes a user from the database.
     * @param id The id of the user to be deleted.
     * @return True if the user was deleted successfully, false otherwise.
     */
    override fun deleteUser(id: Int): Boolean {
        val query = "{CALL deleteUser(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        return statement.executeUpdate() > 0
    }

    /**
     * Deletes a user from the database.
     * @param email The email of the user to be deleted.
     * @return True if the user was deleted successfully, false otherwise.
     */
    override fun deleteUserByEmail(email: String): Boolean {
        val query = "{CALL deleteUserByEmail(?)}"
        val statement = connection.prepareCall(query)
        statement.setString(1, email)
        return statement.executeUpdate() > 0
    }

    /**
     * Maps a result set to a user.
     * @param resultSet The result set to be mapped.
     * @return The user mapped from the result set.
     */
    private fun mapResultSetToUser(resultSet: ResultSet):
            Users {
        return Users(
            firstName = resultSet.getString("first_name"),
            lastName = resultSet.getString("last_name"),
            email = resultSet.getString("email")
        )
    }
}