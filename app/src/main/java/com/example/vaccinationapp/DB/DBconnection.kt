package com.example.vaccinationapp.DB

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object DBconnection {
    private const val URL =
        "jdbc:mysql://sql11.freesqldatabase.com:3306/sql11693454?useUnicode=true&characterEncoding=utf-8&serverTimezone=CET"
    private const val USER = "sql11693454"
    private const val PASS = "eYTSr5hPYs"

    init {
        Class.forName("com.mysql.jdbc.Driver")
    }

    fun getConnection(): Connection {
        try {
            return DriverManager.getConnection(URL, USER, PASS)
        } catch (ex: SQLException) {
            throw RuntimeException(
                "Error connecting to the database ", ex
            )
        }
    }

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