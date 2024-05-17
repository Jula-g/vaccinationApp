package com.example.vaccinationapp.ui.managerecords.schedule

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
import com.example.vaccinationapp.DB.entities.Appointments
import com.example.vaccinationapp.DB.entities.Vaccinations
import com.example.vaccinationapp.R
import com.example.vaccinationapp.adapters.HoursAdapter
import com.example.vaccinationapp.adapters.VaccinesAdapter
import com.example.vaccinationapp.DB.entities.Records
import com.example.vaccinationapp.ui.Dates
import com.example.vaccinationapp.ui.Hours
import com.example.vaccinationapp.ui.MainActivity
import com.example.vaccinationapp.ui.Queries
import com.example.vaccinationapp.ui.Vaccines
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Activity for scheduling an appointment.
 */
class ScheduleActivity : AppCompatActivity(), HoursAdapter.OnItemClickListener,
    VaccinesAdapter.OnItemClickListener {

    private var selectedDateFormatted = ""
    private var selectedDate = ""
    private var dateTime = ""
    private var minDate : Date?= null
    //FINAL VALUES
    private var FvaccineID: Int = 0
    private var latestDose: Int = 0
    private var nextDoseDate: Date? = null
    private val hoursManager = Hours()
    private val vaccinesManager = Vaccines()
    private val datesManager = Dates()
    private val queries = Queries()


    /**
     * Creates the view for the schedule screen.
     * @param savedInstanceState The saved instance state.
     */
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


        //retrieves all vaccines from the database
        var offeredVaccines: Set<Vaccinations>? = setOf(Vaccinations())
        runBlocking {
            launch(Dispatchers.IO) {
                offeredVaccines = queries.getAllVaccines()
            }
        }

        val vaccines = offeredVaccines?.let { vaccinesManager.offerVaccines(it) }

        val vaccinesRecycler = findViewById<RecyclerView>(R.id.vaccinesRecycler)
        vaccinesRecycler.layoutManager = LinearLayoutManager(this)

        if (!vaccines.isNullOrEmpty()) {
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
                    datesManager.showDatePickerDialog(this@ScheduleActivity, date, offeredHours, hoursManager, minDate)

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
            val Fdate = Date(dateFormat.parse(dateString)!!.time)
            val Ftime = java.sql.Time(timeFormat.parse(time)!!.time)

            var FuserID: Int = 0
            val email = FirebaseAuth.getInstance().currentUser!!.email
            runBlocking {
                launch(Dispatchers.IO) {
                    //if user has an account and is logged in, it must be in the database so userID will never be null
                    FuserID = queries.getUserId(email!!)!!.toInt()
                }
            }

            //create appointment object
            val appointment = Appointments(Fdate, Ftime, FuserID, FvaccineID, null)
            Log.d("DATABASE ", "appointment: $appointment")

            var vacc: Vaccinations? = null
            var nextDose: Date? = null
            //add appointment to the DB
            runBlocking { launch(Dispatchers.IO) {
                val rec = queries.getRecordByUserVaccDate(FuserID,FvaccineID, Fdate)
                if(rec != null){
                    nextDose = rec.nextDoseDueDate!!
                }
                val result = queries.addAppointment(appointment, nextDose)
                Log.d("DATABASE", "Add appointment successful: $result")
                vacc = queries.getVaccination(FvaccineID)
            } }
            val interval = vacc?.timeBetweenDoses!!
            val intSplit = interval.split(";")

            runBlocking { launch(Dispatchers.IO) {
                latestDose = queries.getAppointmentsForUserAndVaccine(FuserID, FvaccineID)?.size ?: 0
            } }

            val currentDose = latestDose + 1
            Log.d("DOSESstupid", "latest: $latestDose")
            Log.d("DOSESstupid", "current: $currentDose")
            checkDose(latestDose, intSplit, Fdate)
            Log.d("TESTING", "nextDoseDate: $nextDoseDate")
            val record = Records(FuserID, FvaccineID, Fdate, latestDose, nextDoseDate)

            // add record
            var recordId: Int? = null
            runBlocking { launch(Dispatchers.IO) {
                val result2 = queries.addRecord(record)
                Log.d("RECORDS", "Add record succesful: $result2")
                recordId = queries.getRecordId(FuserID, FvaccineID, latestDose, Fdate)
                }}

            val updatedAppointment = Appointments(Fdate, Ftime, FuserID, FvaccineID,recordId)
            runBlocking { launch(Dispatchers.IO) {
                val appointmentId = queries.getAppointmentId(Fdate.toString(), Ftime.toString())!!
                val result3 = queries.updateAppointment(appointmentId, nextDose, updatedAppointment)
                Log.d("APPOINTMENTUPDATE", "Update appointment succesfull: $result3")
            } }

            goToManageRecords()
        }

        cancel.setOnClickListener {
            goToManageRecords()
        }

    }

    private fun checkDose(latestDose: Int, intSplit: List<String>, Fdate: Date){
        val index = this.latestDose - 1
        if(latestDose >= 0 && latestDose <= intSplit.size ) {
            nextDoseDate = addDaysToDate(Fdate, intSplit[index].toInt())
        }else if (latestDose > intSplit.size){
            this.latestDose = 1
            val newIndex = 0
            checkDose(newIndex, intSplit, Fdate)
        }
    }
    fun addDaysToDate(date: Date, days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return Date(calendar.timeInMillis)
    }

    private fun goToManageRecords() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("value", 1)
        startActivity(intent)
        finish()
    }


    override fun onHourClick(item: String, date: Button) {
        val finalDate = "$selectedDateFormatted $item"
        dateTime = "$selectedDate;$item"
        date.text = finalDate
    }

    override suspend fun onVaccineClick(vaccineName: String, healthcareUnitId: Int, isSelected: Boolean){
        if(!isSelected) {
            FvaccineID =  queries.getVaccinationId(vaccineName, healthcareUnitId)!!
            Log.d("VACCINEID", "vaccine id: $FvaccineID")
        }else{
            FvaccineID = 0
            Log.d("VACCINEID", "vaccine id: $FvaccineID")
        }

        if(!isSelected) {
            // get all records for user
            val email = FirebaseAuth.getInstance().currentUser!!.email
            var userId = 0
            var records: List<Records>? = null
            runBlocking {
                launch(Dispatchers.IO) {
                    userId = queries.getUserId(email!!)!!.toInt()
                    records = queries.getAllRecordsForUserId(userId)?.toList()
                }
            }

            val vacc1 = queries.getVaccination(FvaccineID)

            //filter them by vaccineId
            val filteredRecords = records?.filter { record ->
                val vacc2 = record.vaccineId?.let { queries.getVaccination(it) }
                vacc1?.vaccineName == vacc2?.vaccineName
            }

            //find the record with highest 'dose' value
            val maxRec = filteredRecords?.maxByOrNull { it.dose!! }

            //get next dose due date from the remaining record
            minDate = maxRec?.nextDoseDueDate
        }else
            minDate = null
    }

}
