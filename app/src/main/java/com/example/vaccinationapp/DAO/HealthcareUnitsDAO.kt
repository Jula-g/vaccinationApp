package com.example.vaccinationapp.DAO

import com.example.vaccinationapp.entities.HealthcareUnits

interface HealthcareUnitsDAO {
    fun addHealthcareUnit(healthcareUnit: HealthcareUnits): Boolean
    fun getHealthcareUnit(id: Int): HealthcareUnits?
    fun getHalthcareUnitId(name:String): Int?
    fun updateHealthcareUnit(id: Int, healthcareUnit: HealthcareUnits): Boolean
    fun deleteHealthcareUnit(id: Int): Boolean
    fun getAllHealthcareUnits(): Set<HealthcareUnits>?
}