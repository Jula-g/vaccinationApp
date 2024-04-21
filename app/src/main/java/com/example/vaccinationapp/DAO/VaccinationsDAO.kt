package com.example.vaccinationapp.DAO

import com.example.vaccinationapp.entities.Vaccinations

interface VaccinationsDAO {
    fun addVaccination(vaccination: Vaccinations): Boolean
    fun getVaccination(id: Int): Vaccinations?
    fun getVaccinationId(name: String, healthcareUnitId: Int): Int
    fun updateVaccination(id: Int, vaccination: Vaccinations): Boolean
    fun deleteVaccination(id: Int): Boolean
    fun getAllVaccinations(): Set<Vaccinations>?
}