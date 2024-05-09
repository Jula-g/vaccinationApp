package com.example.vaccinationapp.DB.DAO

import com.example.vaccinationapp.DB.entities.HealthcareUnits

/**
 * Interface for managing the healthcare units in the database.
 */
interface HealthcareUnitsDAO {

    /**
     * Adds a new healthcare unit to the database.
     *
     * @param healthcareUnit The healthcare unit to add.
     * @return true if the healthcare unit was added successfully, false otherwise.
     */
    fun addHealthcareUnit(healthcareUnit: HealthcareUnits): Boolean

    /**
     * Retrieves a healthcare unit from the database.
     *
     * @param id The ID of the healthcare unit to retrieve.
     * @return The healthcare unit with the given ID, or null if no such healthcare unit exists.
     */
    fun getHealthcareUnit(id: Int): HealthcareUnits?

    /**
     * Retrieves a healthcare unit from the database.
     *
     * @param name The name of the healthcare unit to retrieve.
     * @return The healthcare unit with the given name, or null if no such healthcare unit exists.
     */
    fun getHealthcareUnitId(name: String): Int?

    /**
     * Updates an existing healthcare unit in the database.
     *
     * @param id The ID of the healthcare unit to update.
     * @param healthcareUnit The updated healthcare unit.
     * @return true if the healthcare unit was updated successfully, false otherwise.
     */
    fun updateHealthcareUnit(id: Int, healthcareUnit: HealthcareUnits): Boolean

    /**
     * Deletes a healthcare unit from the database.
     *
     * @param id The ID of the healthcare unit to delete.
     * @return true if the healthcare unit was deleted successfully, false otherwise.
     */
    fun deleteHealthcareUnit(id: Int): Boolean

    /**
     * Retrieves all healthcare units from the database.
     *
     * @return A set of all healthcare units in the database, or null if no healthcare units exist.
     */
    fun getAllHealthcareUnits(): Set<HealthcareUnits>?
}