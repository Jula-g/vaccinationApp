package com.example.vaccinationapp.DAO

import com.example.vaccinationapp.entities.Vaccinations

interface VaccinationsDAO {
    fun addVaccination(vaccination: Vaccinations)
    fun getVaccination(id: Int): Vaccinations?
    fun updateVaccination(vaccination: Vaccinations)
    fun deleteVaccination(id: Int)
    fun getAllVaccinations(): List<Vaccinations>
}