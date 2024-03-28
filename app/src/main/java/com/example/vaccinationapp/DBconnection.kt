package com.example.vaccinationapp

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DBconnection {
    // Database connection details
    private const val URL =
        "jdbc:mysql://sql11.freesqldatabase.com:3306/XXXXXXXXXXXX?useUnicode=true&characterEncoding=utf-8&serverTimezone=CET" //XXXXXXXXX - database name
    private const val USER = "XXXXXXXXXX"
    private const val PASS = "XXXXXXXXXX"

    init {
        Class.forName("com.mysql.cj.jdbc.Driver")
    }

    // Function to get a connection to the database
    fun getConnection(): Connection {
        try {
            return DriverManager.getConnection(URL, USER, PASS)
        } catch (ex: SQLException) {
            throw RuntimeException(
                "Error connecting to the database ", ex)
        }
    }

    // Main function to test the database connection
    @JvmStatic
    fun main(args: Array<String>) {
        try {
            // Getting a connection
            val conn = getConnection()
            // Closing the connection
            conn.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

}