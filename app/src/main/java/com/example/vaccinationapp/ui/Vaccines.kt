package com.example.vaccinationapp.ui

import android.util.Log
import com.example.vaccinationapp.DBconnection
import com.example.vaccinationapp.entities.HealthcareUnits
import com.example.vaccinationapp.entities.Vaccinations
import com.example.vaccinationapp.queries.HealthcareUnitsQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class Vaccines {
    fun offerVaccines(offeredVaccines:Set<Vaccinations>): List<String>{
        val result = mutableListOf<String>()
        var healthcareUnit: HealthcareUnits? = HealthcareUnits()

        if (offeredVaccines.isNotEmpty()){
            for (vaccine in offeredVaccines){
                val name = vaccine.vaccineName.toString()
                val id = vaccine.healthcareUnitId
                runBlocking { launch(Dispatchers.IO){
                    healthcareUnit = id?.let { getHealthcareUnit(it) }
                } }
                val unitName = healthcareUnit?.name
                val city = healthcareUnit?.city
                val street = healthcareUnit?.street
                val nr = healthcareUnit?.streetNumber
                val address = "$unitName $city $street $nr"
                val nameAddress = "$name;$address;$id"
                result.add(nameAddress)
            }
        }
        return result
    }

    private suspend fun getHealthcareUnit(id: Int): HealthcareUnits?{
        return withContext(Dispatchers.IO){
            val conn = DBconnection.getConnection()
            val HUqueries = HealthcareUnitsQueries(conn)
            val result = HUqueries.getHealthcareUnit(id)
            Log.d("DATABASE", "healthcare units: $result")
            conn.close()
            result
        }
    }
}