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
import com.example.vaccinationapp.adapters.VaccinesAdapter
import com.example.vaccinationapp.entities.Appointments
import com.example.vaccinationapp.entities.HealthcareUnits
import com.example.vaccinationapp.entities.Users
import com.example.vaccinationapp.entities.Vaccinations
import com.example.vaccinationapp.queries.AppointmentsQueries
import com.example.vaccinationapp.queries.HealthcareUnitsQueries
import com.example.vaccinationapp.queries.UsersQueries
import com.example.vaccinationapp.queries.VaccinationsQueries
import com.example.vaccinationapp.ui.managerecords.ManageRecordsFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.sql.RowId
import java.sql.Time
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class ScheduleActivity : AppCompatActivity(), HoursAdapter.OnItemClickListener,
    VaccinesAdapter.OnItemClickListener {

    private var selectedDateFormatted = ""
    private var selectedDate = ""
    //FINAL VALUES
    private var Fdate: java.sql.Date? = null
    private var Ftime: java.sql.Time? = null
    private var FuserID: Int = 0
    private var FvaccineID: Int = 0


    @SuppressLint("MissingInflatedId", "SimpleDateFormat", "WeekBasedYear")
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

        val email = FirebaseAuth.getInstance().currentUser!!.email
        runBlocking { launch(Dispatchers.IO) {
            FuserID = getUserId(email!!)
        } }

        var offeredVaccines: Set<Vaccinations>? = setOf(Vaccinations())
        runBlocking {
            launch(Dispatchers.IO){
                offeredVaccines = getAllVaccines()
            }
        }

        val vaccines = offeredVaccines?.let { offerVaccines(it) }

        val vaccinesRecycler = findViewById<RecyclerView>(R.id.vaccinesRecycler)
        vaccinesRecycler.layoutManager = LinearLayoutManager(this)

        if(!vaccines.isNullOrEmpty()) {
            val adapterVaccine = VaccinesAdapter(vaccines, date)
            vaccinesRecycler.adapter = adapterVaccine

            adapterVaccine.setOnItemClickListener(this)
        }


        val offeredHours = offerHours("08:00:00", "16:00:00", 30)
        Log.d("OFFEREDHOURS", "$offeredHours")

//        val dateP = Date

        // PICKING THE DATE WILL RESULT IN THE APP SHOWING AVAILABLE HOURS
        date.setOnClickListener {
            showDatePickerDialog(date, offeredHours)
        }

        confirm.setOnClickListener {
            val dateTime = date.text
            val splitDateTime = dateTime.split(" ")
            val dateString = splitDateTime[0]
            val time = splitDateTime[1]
            val dateFormat = SimpleDateFormat("YYYY-MM-DD")
            val timeFormat = SimpleDateFormat("HH:mm")
            Fdate = dateFormat.parse(dateString) as java.sql.Date?
            Ftime = timeFormat.parse(time) as java.sql.Time?

            //create appointment object
            val appointment = Appointments(Fdate, Ftime, FuserID, FvaccineID)

            //add appoitment to the DB
            runBlocking { launch(Dispatchers.IO) {
                val result = addAppointment(appointment)
                Log.d("DATABASE", "Add appointment successful: $result")
            } }

            val intent = Intent(this, ManageRecordsFragment::class.java)
            startActivity(intent)
        }

        cancel.setOnClickListener {
            val intent = Intent(this, ManageRecordsFragment::class.java)
            startActivity(intent)
        }

    }

    private fun offerVaccines(offeredVaccines:Set<Vaccinations>): List<String>{
        val vaccines = mutableListOf<String>()
        val result = mutableListOf<String>()
        var healthcareUnit: HealthcareUnits? = HealthcareUnits()

        if (offeredVaccines.isNotEmpty()){
            for (vaccine in offeredVaccines){
                // make a list of vaccine names
                vaccines.add(vaccine.vaccineName.toString())
                val id = vaccine.healthcareUnitId
                runBlocking { launch(Dispatchers.IO){
                    healthcareUnit = id?.let { getHealthcareUnit(it) }
                } }
                val unitName = healthcareUnit?.name
                val city = healthcareUnit?.city
                val street = healthcareUnit?.street
                val nr = healthcareUnit?.streetNumber
                val address = "$unitName $city $street $nr"
                val nameAddress = "$vaccine;$address;$id"
                result.add(nameAddress)
            }
        }
        return result
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
        try {
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
            val availableHoursFormatted = availableHours.map { time ->
                val parts = time.split(":")
                "${parts[0]}:${parts[1]}"
            }

            Log.d("AVAILABLEHOURS", "$availableHoursFormatted")
            availableHoursFormatted
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
        return String.format("%02d:%02d:00", nextHour, nextMinute)
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

    override fun onHourClick(item: String, date: Button) {
        val finalDate = "$selectedDateFormatted $item"
        date.text = finalDate
    }

    override suspend fun onVaccineClick(vaccineName: String, healthcareUnitId: Int){
//        runBlocking { launch(Dispatchers.IO) {
            FvaccineID = getVaccinationId(vaccineName, healthcareUnitId)
//        } }
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

    private suspend fun addAppointment(appointment: Appointments):Boolean{
        return withContext(Dispatchers.IO){
            val conn = DBconnection.getConnection()
            Log.d("DATABASE", "appointment connected")
            val appQueries = AppointmentsQueries(conn)
            val result = appQueries.addAppointment(appointment)
            Log.d("DATABASE", "appointment added: $result")
            conn.close()
            result
        }
    }

    private suspend fun getAllVaccines(): Set<Vaccinations>?{
        return withContext(Dispatchers.IO){
            val connection = DBconnection.getConnection()
            Log.d("DATABASE", "vaccines connected")
            val vaccineQueries = VaccinationsQueries(connection)
            val result = vaccineQueries.getAllVaccinations()
            Log.d("DATABASE", "vaccines: $result")
            connection.close()
            result
        }
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

    private suspend fun getUserId(email: String): Int {
        return withContext(Dispatchers.IO){
            val conn = DBconnection.getConnection()
            val userQueries = UsersQueries(conn)
            val result = userQueries.getUserId(email)
            Log.d("DATABASE", "user ID: $result")
            conn.close()
            result
        }
    }

    private suspend fun getVaccinationId(name:String, healthcareUnitId: Int): Int{
        return withContext(Dispatchers.IO){
            val conn = DBconnection.getConnection()
            val vaccQueries = VaccinationsQueries(conn)
            val result = vaccQueries.getVaccinationId(name, healthcareUnitId)
            conn.close()
            result
        }
    }

    private suspend fun getHealthcareUnitId(name: String){
        return withContext(Dispatchers.IO){
            val conn = DBconnection.getConnection()
            val unitQueries = HealthcareUnitsQueries(conn)
            val result = unitQueries.getHalthcareUnitId(name)
            conn.close()
            result
        }
    }

}