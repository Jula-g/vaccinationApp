package com.example.vaccinationapp.ui

import android.annotation.SuppressLint
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.TimeZone

class Hours {
    private val queries = Queries()
    @SuppressLint("SimpleDateFormat")
    fun getAvailableHours(selectedDate: String, offeredHours: List<String>): List<String>{
        var takenHours: List<String>? = null
        var offeredHoursFormatted: List<String>? = null
        try {
            runBlocking {
                launch(Dispatchers.IO) {
                    takenHours = queries.getAllAppointmentsForDate(selectedDate)
                }
            }
            Log.d("TAKENHOURS", "$takenHours")

        } catch (e: Exception) {
            e.printStackTrace()
        }

        takenHours = formatHours(takenHours)
        offeredHoursFormatted = formatHours(offeredHours)

//        // get current date and time
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("CET")
        val currentDate = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH)+1}-${calendar.get(
            Calendar.DAY_OF_MONTH)}"
        val currentTime= "${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get((Calendar.MINUTE))}"
        val sdf = SimpleDateFormat("H:mm")

        if(selectedDate == currentDate) {
            offeredHoursFormatted = filterHours(offeredHoursFormatted, currentTime)
        }

        return if (takenHours == null) {
            offeredHoursFormatted
        }else{
            val availableHours = offeredHoursFormatted.minus(takenHours ?: emptyList())
            availableHours
        }
    }

    fun filterHours(offeredHours: List<String>, currentTime: String): List<String> {
        // Parse the current time
        val currentTimeSplit = currentTime.split(":")
        val currentHour = currentTimeSplit[0].toInt()
        val currentMinute = currentTimeSplit[1].toInt()

        // Function to parse a time string and return the hour and minute as a pair
        fun parseTime(time: String): Pair<Int, Int> {
            val parts = time.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()
            return Pair(hour, minute)
        }

        // Filter the times based on the comparison with the current time
        return offeredHours.filter { time ->
            val (hour, minute) = parseTime(time)
            if (hour < currentHour) {
                false
            } else if (hour == currentHour) {
                minute >= currentMinute
            } else {
                true
            }
        }
    }

    fun formatHours(hours: List<String>?): List<String> {
        return hours?.map { time ->
            val parts = time.split(":")
            val formattedHour = parts[0].trimStart('0')
            "$formattedHour:${parts[1]}"
        } ?: emptyList()
    }

    //code that generates offered hours (admin can choose the working hours they offer to patients
    //as well as choosing the time interval between the visits)
    fun offerHours(startTime: String, endTime: String, timeSkip: Int): List<String>{
        val offeredHours = mutableListOf<String>()
        var currentTime = startTime

        while (currentTime != endTime){
            offeredHours.add(currentTime)
            currentTime = addHalfHour(currentTime, timeSkip)
        }
        offeredHours.add(endTime)
        return offeredHours
    }
    private fun addHalfHour(currentTime: String, timeSkip: Int): String{
        val (hour, minute) = currentTime.split(":").map{it.toInt()}
        // calculate next hour in minutes (+30 for next available hour)
        val totalMinutes = hour * 60 + minute + timeSkip
        val nextHour = totalMinutes/60
        val nextMinute = totalMinutes%60
        return String.format("%02d:%02d:00", nextHour, nextMinute)
    }

}