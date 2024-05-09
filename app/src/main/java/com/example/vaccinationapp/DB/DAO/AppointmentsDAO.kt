package com.example.vaccinationapp.DB.DAO

import com.example.vaccinationapp.DB.entities.Appointments

/**
 * Interface for managing the appointments in the database.
 */
interface AppointmentsDAO {

    /**
     * Adds a new appointment to the database.
     *
     * @param appointment The appointment to add.
     * @return true if the appointment was added successfully, false otherwise.
     */
    fun addAppointment(appointment: Appointments): Boolean

    /**
     * Retrieves an appointment from the database.
     *
     * @param id The ID of the appointment to retrieve.
     * @return The appointment with the given ID, or null if no such appointment exists.
     */
    fun getAppointment(id: Int): Appointments?

    /**
     * Retrieves all appointments for a given user.
     *
     * @param id The ID of the user.
     * @return A set of all appointments for the given user, or null if no such appointments exist.
     */
    fun getAllAppointmentsForUserId(id: Int): Set<Appointments>?

    /**
     * Retrieves the ID of an appointment with the given date and time.
     *
     * @param date The date of the appointment.
     * @param time The time of the appointment.
     * @return The ID of the appointment with the given date and time, or null if no such appointment exists.
     */
    fun getAppointmentId(date: String, time: String): Int?

    /**
     * Updates an existing appointment in the database.
     *
     * @param id The ID of the appointment to update.
     * @param appointment The updated appointment.
     * @return true if the appointment was updated successfully, false otherwise.
     */
    fun updateAppointment(id: Int, appointment: Appointments): Boolean

    /**
     * Deletes an appointment from the database.
     *
     * @param id The ID of the appointment to delete.
     * @return true if the appointment was deleted successfully, false otherwise.
     */
    fun deleteAppointment(id: Int): Boolean

    /**
     * Retrieves all appointments from the database.
     *
     * @return A set of all appointments in the database, or null if no appointments exist.
     */
    fun getAllAppointments(): Set<Appointments>?

    /**
     * Retrieves all appointments for a given date.
     *
     * @param date The date of the appointments.
     * @return A list of all appointments for the given date, or null if no appointments exist.
     */
    fun getAllAppointmentsForDate(date: String): List<String>?
}