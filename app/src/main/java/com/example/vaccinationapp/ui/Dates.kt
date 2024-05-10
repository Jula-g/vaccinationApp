package com.example.vaccinationapp.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.R
import com.example.vaccinationapp.adapters.HoursAdapter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import java.sql.Date
import java.text.DateFormatSymbols
import java.util.Calendar

class Dates {
    fun showDatePickerDialog(
        activity: Activity,
        dateButton: Button,
        offeredHours: List<String>,
        hoursManager: Hours,
        minDate: Date?)
    : Deferred<Pair<String, String>> {
        val deferredResult = CompletableDeferred<Pair<String, String>>()

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            activity,
            { _: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate = "$selectedYear-${selectedMonth+1}-$selectedDay"
                val monthName = DateFormatSymbols().months[selectedMonth]
                val selectedDateFormatted = "$selectedDay $monthName $selectedYear"

                if (isDateValid(selectedYear, selectedMonth, selectedDay)) {
                    dateButton.text = selectedDateFormatted
//                    Log.d("SELECTED", "selected1: $selectedDate, formatted1: $selectedDateFormatted")

                    //check available hours for picked date
                    val availableHours = hoursManager.getAvailableHours(selectedDate, offeredHours)

                    val timeRecycler = activity.findViewById<RecyclerView>(R.id.hoursRecycler)
                    timeRecycler.layoutManager = LinearLayoutManager(activity)

                    val adapterTime = HoursAdapter(availableHours, dateButton)
                    timeRecycler.adapter = adapterTime

                    if (activity is HoursAdapter.OnItemClickListener) {
                        adapterTime.setOnItemClickListener(activity)
                    }

                    deferredResult.complete(Pair(selectedDate, selectedDateFormatted))
                }
            },
            currentYear,
            currentMonth,
            currentDay
        )

        if(minDate == null)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        else {
            val minDateInMillis = minDate.time
            datePickerDialog.datePicker.minDate = minDateInMillis
        }
        datePickerDialog.show()

        return deferredResult
    }

    fun isDateValid(selectedYear: Int, selectedMonth: Int, selectedDay: Int): Boolean {
        // Check if the year, month, and day are within valid ranges
        if (selectedYear < 0 || selectedMonth < 1 || selectedMonth > 12 || selectedDay < 1) {
            return false
        }

        // Check if the day is within the valid range for the selected month
        val daysInMonth = when (selectedMonth) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (selectedYear % 4 == 0 && (selectedYear % 100 != 0 || selectedYear % 400 == 0)) 29 else 28
            else -> return false
        }
        return selectedDay <= daysInMonth
    }
}