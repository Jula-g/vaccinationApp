package com.example.vaccinationapp.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.widget.Button
import android.widget.DatePicker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.R
import com.example.vaccinationapp.adapters.HoursAdapter
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import java.text.DateFormatSymbols
import java.util.Calendar

/**
 * Dates class is a utility class that provides methods to show a date picker dialog and check if a date is valid.
 */
class Dates {

    /**
     * showDatePickerDialog method is called to show a date picker dialog and return the selected date.
     * @param activity The activity that calls the method
     * @param dateButton The button that displays the selected date
     * @param offeredHours The list of offered hours
     * @param hoursManager The Hours object that manages the available hours
     * @return A Deferred object that contains the selected date and the formatted date
     */
    fun showDatePickerDialog(
        activity: Activity,
        dateButton: Button,
        offeredHours: List<String>,
        hoursManager: Hours
    )
            : Deferred<Pair<String, String>> {
        val deferredResult = CompletableDeferred<Pair<String, String>>()

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            activity,
            { _: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                val monthName = DateFormatSymbols().months[selectedMonth]
                val selectedDateFormatted = "$selectedDay $monthName $selectedYear"

                if (isDateValid(selectedYear, selectedMonth, selectedDay)) {
                    dateButton.text = selectedDateFormatted

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

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()

        return deferredResult
    }

    /**
     * isDateValid method is called to check if the selected date is valid.
     * @param selectedYear The selected year
     * @param selectedMonth The selected month
     * @param selectedDay The selected day
     * @return A Boolean value that indicates if the selected date is valid
     */
    fun isDateValid(selectedYear: Int, selectedMonth: Int, selectedDay: Int): Boolean {
        if (selectedYear < 0 || selectedMonth < 1 || selectedMonth > 12 || selectedDay < 1) {
            return false
        }

        val daysInMonth = when (selectedMonth) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (selectedYear % 4 == 0 && (selectedYear % 100 != 0 || selectedYear % 400 == 0)) 29 else 28
            else -> return false
        }
        return selectedDay <= daysInMonth
    }
}