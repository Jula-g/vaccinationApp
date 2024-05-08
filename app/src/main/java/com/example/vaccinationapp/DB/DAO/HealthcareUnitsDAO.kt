package com.example.vaccinationapp.DB.DAO

import com.example.vaccinationapp.DB.entities.HealthcareUnits

interface HealthcareUnitsDAO {
    fun addHealthcareUnit(healthcareUnit: HealthcareUnits): Boolean
    fun getHealthcareUnit(id: Int): HealthcareUnits?
    fun getHealthcareUnitId(name:String): Int?
    fun updateHealthcareUnit(id: Int, healthcareUnit: HealthcareUnits): Boolean
    fun deleteHealthcareUnit(id: Int): Boolean
    fun getAllHealthcareUnits(): Set<HealthcareUnits>?
}