package com.example.vaccinationapp.DB.DAO

import com.example.vaccinationapp.DB.entities.Vaccinations

/**
 * Interface for managing the vaccinations in the database.
 */
interface VaccinationsDAO {

    /**
     * Adds a new vaccination to the database.
     *
     * @param vaccination The vaccination to add.
     * @return true if the vaccination was added successfully, false otherwise.
     */
    fun addVaccination(vaccination: Vaccinations): Boolean

    /**
     * Retrieves a vaccination from the database.
     *
     * @param id The ID of the vaccination to retrieve.
     * @return The vaccination with the given ID, or null if no such vaccination exists.
     */
    fun getVaccination(id: Int): Vaccinations?

    /**
     * Retrieves a vaccination from the database.
     *
     * @param name The name of the vaccination to retrieve.
     * @param healthcareUnitId The ID of the healthcare unit where the vaccination is administered.
     * @return The ID of the vaccination with the given name and healthcare unit ID, or null if no such vaccination exists.
     */
    fun getVaccinationId(name: String, healthcareUnitId: Int): Int?

    /**
     * Updates an existing vaccination in the database.
     *
     * @param id The ID of the vaccination to update.
     * @param vaccination The updated vaccination.
     * @return true if the vaccination was updated successfully, false otherwise.
     */
    fun updateVaccination(id: Int, vaccination: Vaccinations): Boolean

    /**
     * Deletes a vaccination from the database.
     *
     * @param id The ID of the vaccination to delete.
     * @return true if the vaccination was deleted successfully, false otherwise.
     */
    fun deleteVaccination(id: Int): Boolean

    /**
     * Retrieves all vaccinations from the database.
     *
     * @return A set of all vaccinations in the database, or null if no vaccinations exist.
     */
    fun getAllVaccinations(): Set<Vaccinations>?
}