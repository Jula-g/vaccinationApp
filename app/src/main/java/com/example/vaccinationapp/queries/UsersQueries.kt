package com.example.vaccinationapp.queries

import com.example.vaccinationapp.DAO.UsersDAO
import com.example.vaccinationapp.entities.Users
import java.sql.Connection
import java.sql.ResultSet

class UsersQueries(private val connection: Connection) : UsersDAO {
    override fun getUser(email: String): Users? {
        val query = "{CALL getUser(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setString(1, email)
        val resultSet = callableStatement.executeQuery()
        return if (resultSet.next()) {
            mapResultSetToUser(resultSet)
        } else {
            null
        }
    }
    override fun getAllUsers(): Set<Users>? {
        val query = "{CALL getAllUsers()}"
        val callableStatement = connection.prepareCall(query)
        val resultSet = callableStatement.executeQuery()
        val users = mutableSetOf<Users?>()
        while (resultSet.next()) {
            users.add(mapResultSetToUser(resultSet))
        }
        val usersFinal = users.filterNotNull().toSet()
        return if (users.isEmpty()) null else usersFinal
    }

    override fun updateUser(email: String, user: Users): Boolean {
        val query = "{CALL updateUser(?, ?, ?, ?, ?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setString(1, user.firstName)
        callableStatement.setString(2, user.lastName)
        callableStatement.setString(3, user.email)
        return callableStatement.executeUpdate() > 0
    }

    override fun addUser(user: Users): Boolean {
        val call = "{CALL addUser(?, ?, ?, ?, ?)}"
        val statement = connection.prepareCall(call)
        statement.setString(1, user.firstName)
        statement.setString(2, user.lastName)
        statement.setString(3, user.email)
        val result = !statement.execute()
        statement.close()
        return result
    }

    override fun deleteUser(email: String): Boolean {
        val query = "{CALL deleteUser(?)}"
        val callableStatement = connection.prepareCall(query)
        callableStatement.setString(1, email)
        return callableStatement.executeUpdate() > 0
    }

    // Maps a ResultSet row to an Users object
    private fun mapResultSetToUser(resultSet: ResultSet):
            Users {
        return Users(
            firstName = resultSet.getString("firstName"),
            lastName = resultSet.getString("lastName"),
            email = resultSet.getString("email")
        )
    }
}