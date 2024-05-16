package com.example.vaccinationapp.DB

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * Singleton object for managing database connections.
 * This object provides a method to get a connection to the database.
 */
object DBconnection {
    // Database connection parameters
    private const val URL =
        "jdbc:mysql://sql11.freesqldatabase.com:3306/sql11693454?useUnicode=true&characterEncoding=utf-8&serverTimezone=CET"
    private const val USER = "sql11693454"
    private const val PASS = "eYTSr5hPYs"

    // Load the database driver when the object is initialized
    init {
        Class.forName("com.mysql.jdbc.Driver")
    }

    /**
     * Establishes a connection to the database and returns it.
     *
     * @return A Connection object for the database.
     * @throws RuntimeException if a database access error occurs.
     */
    fun getConnection(): Connection {
        try {
            return DriverManager.getConnection(URL, USER, PASS)
        } catch (ex: SQLException) {
            throw RuntimeException(
                "Error connecting to the database ", ex
            )
        }
    }

    /**
     * Main function for testing the database connection.
     * This function attempts to establish a connection to the database and then closes it.
     *
     * @param args Command-line arguments. Not used in this function.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            val conn = getConnection()
            conn.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}