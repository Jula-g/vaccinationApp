package com.example.vaccinationapp.queries

import com.example.vaccinationapp.DAO.UsersDAO
import com.example.vaccinationapp.entities.Users
import java.sql.Connection
import java.sql.ResultSet

class UsersQueries(private val connection: Connection) : UsersDAO {
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

    override fun updateUser(id: Int, user: Users): Boolean {
        val query = "{CALL updateUser(?, ?, ?, ?)}"
        val statement = connection.prepareCall(query)
        statement.setString(1, user.firstName)
        statement.setString(2, user.lastName)
        statement.setString(3, user.email)
        statement.setInt(4, id)
        return statement.executeUpdate() > 0
    }

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

    override fun deleteUser(id: Int): Boolean {
        val query = "{CALL deleteUser(?)}"
        val statement = connection.prepareCall(query)
        statement.setInt(1, id)
        return statement.executeUpdate() > 0
    }

    // Maps a ResultSet row to an Users object
    private fun mapResultSetToUser(resultSet: ResultSet):
            Users {
        return Users(
            firstName = resultSet.getString("first_name"),
            lastName = resultSet.getString("last_name"),
            email = resultSet.getString("email")
        )
    }
}