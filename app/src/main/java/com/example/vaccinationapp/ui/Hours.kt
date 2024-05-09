package com.example.vaccinationapp.ui

import android.annotation.SuppressLint
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

/**
 * Hours class is a utility class that provides methods to get available hours and offer hours.
 */
class Hours {
    private val queries = Queries()
    @SuppressLint("SimpleDateFormat")
    fun getAvailableHours(selectedDate: String, offeredHours: List<String>): List<String>{
        var takenHours: List<String>? = null
        var offeredHoursFormatted: List<String>?
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

    /**
     * filterHours method is used to filter the offered hours based on the current time
     *  @param offeredHours The list of offered hours
     *  @param currentTime The current time
     *  @return The list of filtered hours
     */
    fun filterHours(offeredHours: List<String>, currentTime: String): List<String> {
        val currentTimeSplit = currentTime.split(":")
        val currentHour = currentTimeSplit[0].toInt()
        val currentMinute = currentTimeSplit[1].toInt()

        fun parseTime(time: String): Pair<Int, Int> {
            val parts = time.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()
            return Pair(hour, minute)
        }

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

    /**
     * formatHours method is used to format the hours in the list
     * @param hours The list of hours
     * @return The list of formatted hours
     */
    fun formatHours(hours: List<String>?): List<String> {
        return hours?.map { time ->
            val parts = time.split(":")
            val formattedHour = parts[0].trimStart('0')
            "$formattedHour:${parts[1]}"
        } ?: emptyList()
    }

    //code that generates offered hours (admin can choose the working hours they offer to patients
    //as well as choosing the time interval between the visits)
    /**
     * offerHours method is used to offer hours between the start and end time with a time skip
     * @param startTime The start time
     * @param endTime The end time
     * @param timeSkip The time skip
     * @return The list of offered hours
     */
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

    /**
     * addHalfHour method is used to add 30 minutes to the current time
     * @param currentTime The current time
     * @param timeSkip The time skip
     * @return The next time
     */
    private fun addHalfHour(currentTime: String, timeSkip: Int): String{
        val (hour, minute) = currentTime.split(":").map{it.toInt()}
        // calculate next hour in minutes (+30 for next available hour)
        val totalMinutes = hour * 60 + minute + timeSkip
        val nextHour = totalMinutes/60
        val nextMinute = totalMinutes%60
        return String.format("%02d:%02d:00", nextHour, nextMinute)
    }

}