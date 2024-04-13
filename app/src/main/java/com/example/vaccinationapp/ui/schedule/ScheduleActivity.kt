package com.example.vaccinationapp.ui.schedule

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.DBconnection
import com.example.vaccinationapp.R
import com.example.vaccinationapp.adapters.HoursAdapter
import com.example.vaccinationapp.queries.AppointmentsQueries
import com.example.vaccinationapp.ui.managerecords.ManageRecordsFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.DateFormatSymbols
import java.util.Calendar

class ScheduleActivity : AppCompatActivity(), HoursAdapter.OnItemClickListener {

    private var selectedDateFormatted = ""
    private var selectedDate = ""

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_schedule)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.schedule_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val search = findViewById<SearchView>(R.id.searchVaccine)
        val date = findViewById<Button>(R.id.dateButton)
        val confirm = findViewById<Button>(R.id.confirmButton)
        val cancel = findViewById<Button>(R.id.cancelButton)

        val offeredHours = offerHours("08:00", "16:00", 30)

        // PICKING THE DATE WILL RESULT IN THE APP SHOWING AVAILABLE HOURS
        date.setOnClickListener {
            showDatePickerDialog(date, offeredHours)
        }

        cancel.setOnClickListener {
            val intent = Intent(this, ManageRecordsFragment::class.java)
            startActivity(intent)
        }

    }


    private fun showDatePickerDialog(date: Button, offeredHours: List<String>): String {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                selectedDate = "$selectedYear-${selectedMonth+1}-$selectedDay"
                val monthName = DateFormatSymbols().months[selectedMonth]
                selectedDateFormatted = "$selectedDay $monthName $selectedYear"

                if (isDateValid(selectedYear, selectedMonth, selectedDay)) {
                    date.text = selectedDateFormatted

                    //check available hours for picked date
                    val availableHours = getAvailableHours(selectedDate, offeredHours)

                    val timeRecycler = findViewById<RecyclerView>(R.id.hoursRecycler)
                    timeRecycler.layoutManager = LinearLayoutManager(this)

                    val adapterTime = HoursAdapter(availableHours, date)
                    timeRecycler.adapter = adapterTime

                    adapterTime.setOnItemClickListener(this)
                }
            },
            currentYear,
            currentMonth,
            currentDay
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()

        return selectedDate
    }

    private fun getAvailableHours(selectedDate: String, offeredHours: List<String>): List<String>{
        var takenHours: List<String>? = null
//        val availableHours = mutableListOf<String>()

        try {
//            val connection = DBconnection.getConnection()
//            val appointmentQueries = AppointmentsQueries(connection)
//            takenHours = appointmentQueries.getAllAppointmentsForDate(selectedDate)

            runBlocking {
                launch(Dispatchers.IO) {
                    takenHours = getAllAppointmentsForDate(selectedDate)
                }
            }

            Log.d("TAKENHOURS", "$takenHours")

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return if (takenHours == null) {
            offeredHours
        }else{


            val availableHours = offeredHours.minus(takenHours ?: emptyList())

            Log.d("AVAILABLEHOURS", "$availableHours")
            availableHours
        }
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
        return String.format("%02d:%02d", nextHour, nextMinute)
    }

    //code that disables the buttons for hours that are already taken
    private fun isHourValid(hours: List<String>, takenHours: List<String>){

    }

    fun isValidTime(time: String?): Boolean {
        // this part checks if time
        if (time == null) return false

        val timeRegex = Regex("^([0-1]?[0-9]|2[0-3]):([0-5][0-9])$")
        if (!time.matches(timeRegex)) {
            println("Invalid time format. Please enter in HH:MM format.")
            return false
        }

        val (hour, minutes) = time.split(":").map { it.toInt() }
        if (hour < 8 || hour > 17 || (hour == 17 && minutes > 0)) {
            println("Time must be between 8:00 AM and 5:00 PM.")
            return false
        }

        if (minutes % 30 != 0) {
            println("Time must be in half-hour increments.")
            return false
        }

        return true
    }


//    fun isTimeValid(selectedHour: Int, selectedMinute: Int): Boolean{
//        //needs to download scheduled hours from database and check if selected time != taken times
//    }


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

    override fun onItemClick(item: String, date: Button) {
        val finalDate = "$selectedDateFormatted $item"
        date.text = finalDate
    }


    //QUERIES
    private suspend fun getAllAppointmentsForDate(date:String): List<String>?{
        return withContext(Dispatchers.IO){
            val connection = DBconnection.getConnection()
            Log.d("DATABASE", "connected with date: $date")
            val appointmentQueries = AppointmentsQueries(connection)
            val result = appointmentQueries.getAllAppointmentsForDate(date)
            Log.d("DATABASE", "result: $result")
            connection.close()
            result
        }
    }
}