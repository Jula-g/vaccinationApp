package com.example.vaccinationapp.ui

import com.example.vaccinationapp.DB.entities.HealthcareUnits
import com.example.vaccinationapp.DB.entities.Vaccinations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Vaccines class is a utility class that provides methods to offer vaccines and filter vaccines by healthcare unit.
 */
class Vaccines {
    private val queries = Queries()

    /**
     * offerVaccines method is called to offer vaccines to the user.
     * @param offeredVaccines The set of offered vaccines
     * @return The list of offered vaccines
     */
    fun offerVaccines(offeredVaccines: Set<Vaccinations>): List<String> {
        val result = mutableListOf<String>()
        var healthcareUnit: HealthcareUnits? = HealthcareUnits()

        if (offeredVaccines.isNotEmpty()) {
            for (vaccine in offeredVaccines) {
                val name = vaccine.vaccineName.toString()
                val id = vaccine.healthcareUnitId
                runBlocking {
                    launch(Dispatchers.IO) {
                        healthcareUnit = id?.let { queries.getHealthcareUnit(it) }
                    }
                }
                val unitName = healthcareUnit?.name
                val address =
                    "${healthcareUnit?.city} ${healthcareUnit?.street} ${healthcareUnit?.streetNumber}"
                val nameAddress = "$name;$unitName;$address;$id"
                result.add(nameAddress)
            }
        }
        return result
    }

    /**
     * filterVaccinesByHealthcareUnit method is called to filter the offered vaccines by healthcare unit.
     * @param vaccines The set of offered vaccines
     * @param unitId The id of the healthcare unit
     * @return The set of filtered vaccines
     */
    fun filterVaccinesByHealthcareUnit(
        vaccines: Set<Vaccinations>,
        unitId: Int
    ): Set<Vaccinations> {
        return vaccines.filter { it.healthcareUnitId == unitId }.toSet()
    }

}