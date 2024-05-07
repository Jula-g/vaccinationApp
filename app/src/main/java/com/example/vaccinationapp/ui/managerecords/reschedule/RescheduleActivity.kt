package com.example.vaccinationapp.ui.managerecords.reschedule

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.DB.DBconnection
import com.example.vaccinationapp.R
import com.example.vaccinationapp.adapters.HoursAdapter
import com.example.vaccinationapp.adapters.VaccinesAdapter
import com.example.vaccinationapp.DB.entities.Appointments
import com.example.vaccinationapp.DB.entities.Vaccinations
import com.example.vaccinationapp.DB.queries.AppointmentsQueries
import com.example.vaccinationapp.ui.Dates
import com.example.vaccinationapp.ui.Hours
import com.example.vaccinationapp.ui.Queries
import com.example.vaccinationapp.ui.Vaccines
import com.example.vaccinationapp.ui.managerecords.ManageRecordsFragment
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

class RescheduleActivity : AppCompatActivity(), HoursAdapter.OnItemClickListener,
    VaccinesAdapter.OnItemClickListener{

    private var appointment: Appointments? = null
    private var selectedDateFormatted = ""
    private var selectedDate = ""
    private var dateTime = ""
    //FINAL VALUES
    private var FvaccineID: Int = 0
    private val hoursManager = Hours()
    private val vaccinesManager = Vaccines()
    private val datesManager = Dates()
    private val queries = Queries()

    @SuppressLint("MissingInflatedId", "SimpleDateFormat")
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

        val appId = intent.getIntExtra("appointmentId", -1)

        runBlocking { launch(Dispatchers.IO){
            appointment = queries.getAppointment(appId)
        } }

        val vaccineId = appointment?.vaccinationId

        var allVaccines: Set<Vaccinations>? = setOf(Vaccinations())
        var vaccine: Vaccinations? = null
        runBlocking {
            launch(Dispatchers.IO){
                allVaccines = queries.getAllVaccines()
                vaccine = vaccineId?.let { queries.getVaccination(it) }
            }
        }

        // show only vaccines from the same healthcare unit, you can reschedule only within the same unit
        val unitId = vaccine?.healthcareUnitId!!
        val filteredVaccines =
            allVaccines?.let { vaccinesManager.filterVaccinesByHealthcareUnit(it, unitId) }
        val vaccines = filteredVaccines?.let { vaccinesManager.offerVaccines(it) }

        val vaccinesRecycler = findViewById<RecyclerView>(R.id.vaccinesRecycler)
        vaccinesRecycler.layoutManager = LinearLayoutManager(this)

        if(!vaccines.isNullOrEmpty()) {
            val adapterVaccine = VaccinesAdapter(vaccines)
            vaccinesRecycler.adapter = adapterVaccine

            adapterVaccine.setOnItemClickListener(this)

            search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                @SuppressLint("NotifyDataSetChanged")
                override fun onQueryTextChange(newText: String?): Boolean {
                    // Filter the list of vaccines based on the search query
                    val filteredList = vaccines.filter {
                        it.contains(newText ?: "", ignoreCase = true)
                    }
                    // Update the adapter with the filtered list
                    adapterVaccine.updateData(filteredList)
                    adapterVaccine.notifyDataSetChanged()

                    // Return false to indicate that the event has been handled
                    return false
                }
            })
        }

        val offeredHours = hoursManager.offerHours("08:00:00", "16:00:00", 30)
        Log.d("OFFEREDHOURS", "$offeredHours")

        // PICKING THE DATE WILL RESULT IN THE APP SHOWING AVAILABLE HOURS
        date.setOnClickListener {
            lifecycleScope.launch {
                val result =
                    datesManager.showDatePickerDialog(this@RescheduleActivity, date, offeredHours, hoursManager)

                val (sDate, sDateFormatted) = result.await()
                selectedDate = sDate
                selectedDateFormatted = sDateFormatted
                Log.d("DATES", "selectedFinito: $selectedDate, formatted: $selectedDateFormatted")
            }
        }

        confirm.setOnClickListener {
            val splitDateTime = dateTime.split(";")
            val dateString = splitDateTime[0]
            val time = splitDateTime[1]
            val dateFormat = SimpleDateFormat("yyyy-M-dd")
            val timeFormat = SimpleDateFormat("HH:mm")
            val Fdate = java.sql.Date(dateFormat.parse(dateString)!!.time)  // PROBLEM with date, it returns a different one for some reason
            val Ftime = java.sql.Time(timeFormat.parse(time)!!.time)

            var FuserID: Int = 0
            val email = FirebaseAuth.getInstance().currentUser!!.email
            runBlocking { launch(Dispatchers.IO) {
                //if user has an account and is logged in, it must be in the database so userID will never be null
                FuserID = queries.getUserId(email!!)!!.toInt()
            } }

            //create appointment object
            val appointment = Appointments(Fdate, Ftime, FuserID, FvaccineID)
            Log.d("DATABASE", "appointment: $appointment")

            //add appoitment to the DB
            runBlocking { launch(Dispatchers.IO) {
                val result = queries.updateAppointment(appId, appointment)
                Log.d("DATABASE", "Update appointment successful: $result")
            } }

            val intent = Intent(this, ManageRecordsFragment::class.java)
            startActivity(intent)
        }

        cancel.setOnClickListener {
            val intent = Intent(this, ManageRecordsFragment::class.java)
            startActivity(intent)
        }
    }

    override fun onHourClick(item: String, date: Button) {
        val finalDate = "$selectedDateFormatted $item"
        dateTime = "$selectedDate;$item"
        date.text = finalDate
    }

    override suspend fun onVaccineClick(vaccineName: String, healthcareUnitId: Int){
        FvaccineID = queries.getVaccinationId(vaccineName, healthcareUnitId)!!
    }

}