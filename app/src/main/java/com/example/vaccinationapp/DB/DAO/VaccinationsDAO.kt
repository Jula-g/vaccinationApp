package com.example.vaccinationapp.DB.DAO

import com.example.vaccinationapp.DB.entities.Vaccinations

interface VaccinationsDAO {
    fun addVaccination(vaccination: Vaccinations): Boolean
    fun getVaccination(id: Int): Vaccinations?
    fun getVaccinationId(name: String, healthcareUnitId: Int): Int?
    fun updateVaccination(id: Int, vaccination: Vaccinations): Boolean
    fun deleteVaccination(id: Int): Boolean
    fun getAllVaccinations(): Set<Vaccinations>?
}