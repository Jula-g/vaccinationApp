
package com.example.vaccinationapp.DB
import com.example.vaccinationapp.DB.DBconnection
import com.example.vaccinationapp.DB.entities.Appointments
import com.example.vaccinationapp.DB.entities.HealthcareUnits
import com.example.vaccinationapp.DB.entities.Records
import com.example.vaccinationapp.DB.entities.Users
import com.example.vaccinationapp.DB.entities.Vaccinations
import com.example.vaccinationapp.DB.queries.AppointmentsQueries
import com.example.vaccinationapp.DB.queries.HealthcareUnitsQueries
import com.example.vaccinationapp.DB.queries.RecordsQueries
import com.example.vaccinationapp.DB.queries.UsersQueries
import com.example.vaccinationapp.DB.queries.VaccinationsQueries
import java.sql.Date
import java.sql.Time

fun main() {
    try {
        val connection = DBconnection.getConnection()
        val appointmentQueries = AppointmentsQueries(connection)
        val healthcareUnitsQueries = HealthcareUnitsQueries(connection)
        val userQueries = UsersQueries(connection)
        val vaccinationsQueries = VaccinationsQueries(connection)
        val recordQueries = RecordsQueries(connection)

        println("add record")
        val record = Records(1,1,Date.valueOf("2024-04-11"), 3, Date.valueOf("2024-08-20"))
        println("insertion: ${recordQueries.addRecord(record)}")
        val id = recordQueries.getRecordId(1,1,3, Date.valueOf("2024-04-11"))!!
        println(id)
        println("update record")
        val updatedRecord = Records(1,1,Date.valueOf("2024-04-11"), 2, Date.valueOf("2024-08-20"))
        println("update: ${recordQueries.updateRecord(id, updatedRecord)}")

//        println("= TESTING USER QUERIES =")
//        println("Testing addUser():")
//        val newUser = Users("Jan", "Kowalski", "test@wp.pl")
//        println("Insertion successful:${userQueries.addUser(newUser)}")
//
//        println("Testing addUser():")
//        val newUser2 = Users("Michael", "Phelps", "mich.phil@wp.pl")
//        println("Insertion successful:${userQueries.addUser(newUser2)}")
//
//        println("Testing getAllUsers():")
//        println(userQueries.getAllUsers())
//
//        println("Testing updateUser():")
//        val updatedUser = Users("Michael", "Philips", "mich.phil@wp.pl")
//        println("Update successful:${userQueries.updateUser(2, updatedUser)}")
//
//        println("Testing getUser():")
//        println(userQueries.getUser(2))
//
////        println("Testing deleteUser():")
////        println("Deletion successful:${userQueries.deleteUser(2)}")
//
//        println("Testing getAllUsers():")
//        println(userQueries.getAllUsers())
//
//
//        println("= TESTING HEALTHCARE UNIT QUERIES =")
//        println("Testing addHealthcareUnit():")
//        val newUnit = HealthcareUnits("unit1", "poland", "warsaw", "street", "2c", "938372900", "emailH1@wp.pl")
//        println("Insertion successful:${healthcareUnitsQueries.addHealthcareUnit(newUnit)}")
//
//        println("Testing addHealthcareUnit():")
//        val newUnit2 = HealthcareUnits("unit2", "poland", "wroclaw", "street", "12", "289345664", "emailH2@wp.pl")
//        println("Insertion successful:${healthcareUnitsQueries.addHealthcareUnit(newUnit2)}")
//
//        println("Testing getAllHealthcareUnits():")
//        println(healthcareUnitsQueries.getAllHealthcareUnits())
//
//        println("Testing updateHealthcareUnit():")
//        val updatedUnit = HealthcareUnits("unit2", "poland", "wroclaw", "different street", "5", "289345664", "emailH2@wp.pl")
//        println("Update successful:${healthcareUnitsQueries.updateHealthcareUnit(2, updatedUnit)}")
//
//        println("Testing getUnit():")
//        println(healthcareUnitsQueries.getHealthcareUnit(2))
//
//        println("Testing deleteHealthcareUnit():")
//        println("Deletion successful:${healthcareUnitsQueries.deleteHealthcareUnit(2)}")
//
////        println("Testing getAllHealthcareUnits():")
////        println(healthcareUnitsQueries.getAllHealthcareUnits())
//
//
//        println("= TESTING VACCINE QUERIES =")
//        println("Testing addVaccination():")
//        val newVaccine = Vaccinations("tetanus", 2, "12",1)
//        println("Insertion successful:${vaccinationsQueries.addVaccination(newVaccine)}")
//        val newVaccine3 = Vaccinations("tetanus", 2, "32",4)
//        println("Insertion successful:${vaccinationsQueries.addVaccination(newVaccine3)}")
//
//        println("Testing addVaccination():")
//        val newVaccine2 = Vaccinations("covid-19 vaccine", 3, "42" ,1)
//        println("Insertion successful:${vaccinationsQueries.addVaccination(newVaccine2)}")
//
//        println("Testing getVaccination():")
//        println(vaccinationsQueries.getVaccination(2))
//
////        println("Testing updateVaccination():")
////        val updatedVaccine = Vaccinations("tetanus", 1, 1)
////        println("Update successful:${vaccinationsQueries.updateVaccination(2, updatedVaccine)}")
//
//        println("Testing getAllVaccinations():")
//        println(vaccinationsQueries.getAllVaccinations())
//
////        println("Testing deleteVaccination():")
////        println("Deletion successful:${vaccinationsQueries.deleteVaccination(2)}")
//
//
//
//        println("= TESTING APPOINTMENTS QUERIES =")   //gotta figure out how to do date and tie :/
//        println("Testing addAppointment():")
//        val date = Date.valueOf("2024-05-28")
//        val time = Time.valueOf("15:00:00")
//        val newAppointment = Appointments(date, time, 1, 1)
//        println("Insertion successful:${appointmentQueries.addAppointment(newAppointment,null)}")
//
//        println("Testing addAppointment():")
//        val date2 = Date.valueOf("2024-05-13")
//        val time2 = Time.valueOf("12:30:00")
//        val newAppointment2 = Appointments(date, time2, 1, 1) //doesn't see date and time
//        println("Insertion successful:${appointmentQueries.addAppointment(newAppointment2, null)}")
//
//        println("Testing getAllAppointments():")
//        println(appointmentQueries.getAllAppointments())
//
//        println("Testing updateAppointment():")
//        val time3 = Time.valueOf("09:30:00")
//        val updatedApp = Appointments(date, time3, 1, 1)
//        println("Update successful:${appointmentQueries.updateAppointment(2, updatedApp)}")
//
//        println("Testing getAppointment():")
//        println(appointmentQueries.getAppointment(2))
//
////        println("Testing deleteUser():")
////        println("Deletion successful:${appointmentQueries.deleteAppointment(2)}")
//
//        println("Testing getAllAppointmentsForDate():")
//        val dateString = date.toString()
//        println("date: $dateString")
//        println(appointmentQueries.getAllAppointmentsForDate(dateString))
//
//        println("get all app for user id")
//        println(appointmentQueries.getAllAppointmentsForUserId(60))
//
//        print("get app id")
//        println(appointmentQueries.getAppointmentId("2024-04-26", "15:30"))

                // Closing connection
                connection.close() // Closing the database connection
    } catch (e: Exception) {
        e.printStackTrace() // Printing error information in case an exception occurs
    }
}