package com.example.vaccinationapp.DAO

import com.example.vaccinationapp.entities.HealthcareUnits

interface HealthcareUnitsDAO {
    fun addHealthcareUnit(healthcareUnit: HealthcareUnits)
    fun getHealthcareUnit(id: Int): HealthcareUnits?
    fun updateHealthcareUnit(healthcareUnit: HealthcareUnits)
    fun deleteHealthcareUnit(id: Int)
    fun getAllHealthcareUnits(): List<HealthcareUnits>
}