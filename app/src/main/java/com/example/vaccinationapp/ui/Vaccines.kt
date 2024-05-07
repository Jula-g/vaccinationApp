package com.example.vaccinationapp.ui

import com.example.vaccinationapp.DB.entities.HealthcareUnits
import com.example.vaccinationapp.DB.entities.Vaccinations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


class Vaccines {
    private val queries = Queries()
    fun offerVaccines(offeredVaccines:Set<Vaccinations>): List<String>{
        val result = mutableListOf<String>()
        var healthcareUnit: HealthcareUnits? = HealthcareUnits()

        if (offeredVaccines.isNotEmpty()){
            for (vaccine in offeredVaccines){
                val name = vaccine.vaccineName.toString()
                val id = vaccine.healthcareUnitId
                runBlocking { launch(Dispatchers.IO){
                    healthcareUnit = id?.let { queries.getHealthcareUnit(it) }
                } }
                val unitName = healthcareUnit?.name
                val address = "${healthcareUnit?.city} ${healthcareUnit?.street} ${healthcareUnit?.streetNumber}"
                val nameAddress = "$name;$unitName;$address;$id"
                result.add(nameAddress)
            }
        }
        return result
    }

    fun filterVaccinesByHealthcareUnit(vaccines: Set<Vaccinations>, unitId: Int): Set<Vaccinations> {
        return vaccines.filter { it.healthcareUnitId == unitId }.toSet()
    }

}