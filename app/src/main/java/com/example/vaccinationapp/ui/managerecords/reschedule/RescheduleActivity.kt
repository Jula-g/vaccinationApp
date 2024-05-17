package com.example.vaccinationapp.ui.managerecords.reschedule

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.R
import com.example.vaccinationapp.adapters.HoursAdapter
import com.example.vaccinationapp.adapters.VaccinesAdapter
import com.example.vaccinationapp.DB.entities.Appointments
import com.example.vaccinationapp.DB.entities.Records
import com.example.vaccinationapp.DB.entities.Vaccinations
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
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

/**
 * Activity for rescheduling an appointment.
 *
 */
class RescheduleActivity : AppCompatActivity(), HoursAdapter.OnItemClickListener,
    VaccinesAdapter.OnItemClickListener {

    private var appointment: Appointments? = null
    private var selectedDateFormatted = ""
    private var selectedDate = ""
    private var dateTime = ""
    private var minDate: Date? = null
    private var prevVaccId: Int = 0

    //FINAL VALUES
    private var FvaccineID: Int = 0
    private var appId: Int = 0
    private var currentDose: Int = 0
    private var nextDoseDate: Date? = null
    private val hoursManager = Hours()
    private val vaccinesManager = Vaccines()
    private val datesManager = Dates()
    private val queries = Queries()

    /**
     * Creates the view for the reschedule screen.
     * @param savedInstanceState The saved instance state.
     */
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

        appId = intent.getIntExtra("appointmentId", -1)

        runBlocking {
            launch(Dispatchers.IO) {
                appointment = queries.getAppointment(appId)
            }
        }

        prevVaccId = appointment?.vaccinationId!!

        var allVaccines: Set<Vaccinations>? = setOf(Vaccinations())
        var vaccine: Vaccinations? = null
        runBlocking {
            launch(Dispatchers.IO) {
                allVaccines = queries.getAllVaccines()
                vaccine = prevVaccId.let { queries.getVaccination(it) }
            }
        }

        // show only vaccines from the same healthcare unit, you can reschedule only within the same unit
        val unitId = vaccine?.healthcareUnitId!!
        val filteredVaccines =
            allVaccines?.let { vaccinesManager.filterVaccinesByHealthcareUnit(it, unitId) }
        val vaccines = filteredVaccines?.let { vaccinesManager.offerVaccines(it) }

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
                    datesManager.showDatePickerDialog(
                        this@RescheduleActivity,
                        date,
                        offeredHours,
                        hoursManager,
                        minDate
                    )

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
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            val timeFormat = SimpleDateFormat("HH:mm")
            val finalDate = Date(dateFormat.parse(dateString)!!.time)
            val finalTime = Time(timeFormat.parse(time)!!.time)

            var finalUserID: Int = 0
            val email = FirebaseAuth.getInstance().currentUser!!.email
            var allUserAppointments: Set<Appointments>? = null
            val recordId = appointment!!.recordId!!
            var record: Records? = null
            runBlocking {
                launch(Dispatchers.IO) {
                    //if user has an account and is logged in, it must be in the database so userID will never be null
                    finalUserID = queries.getUserId(email!!)!!.toInt()
                    record = queries.getRecord(recordId)
                    allUserAppointments = queries.getAllAppointmentsForUserId(finalUserID)
                }
            }

            val appointmentsForSameVaccine =
                allUserAppointments!!.filter { it.vaccinationId == FvaccineID }

            var vacc: Vaccinations? = null
            runBlocking {
                launch(Dispatchers.IO) {
                    vacc = queries.getVaccination(FvaccineID)
                }
            }

            val interval = vacc?.timeBetweenDoses!!
            val intSplit = interval.split(";")

            currentDose = record?.dose!!
            val index = currentDose - 1
            nextDoseDate = checkDose(index, intSplit, finalDate)

            // check if same user has appointments for same vaccine
            if (appointmentsForSameVaccine.size > 1) {
                // check if the rest of the appointments for this vaccine are before the nextDoseDate
                val nextAppointments = appointmentsForSameVaccine.filter {
                    it.date!!.before(nextDoseDate) &&
                            it.date!!.after(finalDate)
                }
                if (nextAppointments.isNotEmpty()) {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage("Are you sure you want to reschedule the appointment?\nIt will result in cancelation of the following appointments.")
                        .setPositiveButton("Yes") { dialog, _ ->
                            // delete following appointments and records for the samer vaccine and user
                            for(appointment in nextAppointments){
                                runBlocking { launch(Dispatchers.IO) {
                                    val recId = appointment.recordId!!
                                    val result = queries.deleteRecord(recId)
                                    Log.d("DATABASE", "Record deletion successful: $result")
                                } }
                            }

                            // update appointment and it's record
                            updateAppointment(finalDate, finalTime, finalUserID, recordId, appId)
                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }

                    val alert = builder.create()
                    alert.show()
                }
            } else
                updateAppointment(finalDate, finalTime, finalUserID, recordId, appId)

        }

        cancel.setOnClickListener {
            goToManageRecords()
        }
    }

    /**
     * Updates the appointment and the record.
     * @param finDate The final date of the appointment.
     * @param finTime The final time of the appointment.
     * @param finUserID The final user id.
     * @param recordId The record id.
     * @param appId The appointment id.
     */
    private fun updateAppointment(
        finDate: Date,
        finTime: Time,
        finUserID: Int,
        recordId: Int,
        appId: Int
    ) {
        //create appointment object
        val appointment = Appointments(finDate, finTime, finUserID, FvaccineID, recordId)
        Log.d("DATABASE", "appointment: $appointment")
        var records: List<Records>? = null
        var vacc: Vaccinations? = null
        runBlocking {
            launch(Dispatchers.IO) {
                val app = queries.getAppointment(appId)
                val recId = app!!.recordId!!
                val rec = queries.getRecord(recId)!!

                val nextDose = rec.nextDoseDueDate!!

                val result = queries.updateAppointment(appId, nextDose, appointment)
                Log.d("UpdateAppointment", "Update successful: $result")

                val recordsList = queries.getAllRecordsForUserId(finUserID)
                records = recordsList?.filter {
                    it.vaccineId == prevVaccId
                }
                vacc = queries.getVaccination(prevVaccId)
            }
        }

        //assign new dose numbers
        val sortedRecords = records?.sortedBy { it.dateAdministered }
        val noOfDoses = vacc!!.noOfDoses!!
        if (sortedRecords != null) {
            for ((index, record) in sortedRecords.withIndex()) {
                val dose = (index % noOfDoses) + 1

                val rec = Records(record.userId, record.vaccineId, record.dateAdministered, dose, record.nextDoseDueDate)

                var recId = 0
                runBlocking { launch(Dispatchers.IO) {
                    recId = queries.getRecordId(record.userId!!, record.vaccineId!!, record.dose!!, record.dateAdministered!!)!!
                    queries.updateRecord(recId, rec)
                }}

            }
        }
        goToManageRecords()
    }

    /**
     * Checks the dose.
     * @param index The index of the dose.
     * @param intSplit The list of intervals.
     * @param Fdate The final date.
     * @return The date of the dose.
     */
    private fun checkDose(index: Int, intSplit: List<String>, Fdate: Date): Date? {
        if (index >= 0 && index < intSplit.size) {
            return addDaysToDate(Fdate, intSplit[index].toInt())
        }else if (index > intSplit.size){
            currentDose = 1
            val newIndex = 0
            checkDose(newIndex, intSplit, Fdate)
        }
        return null
    }

    /**
     * Adds days to a date.
     * @param date The date.
     * @param days The number of days to add.
     * @return The new date.
     */
    fun addDaysToDate(date: Date, days: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return Date(calendar.timeInMillis)
    }

    /**
     * Goes to the manage records screen.
     */
    private fun goToManageRecords() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("value", 1)
        startActivity(intent)
        finish()
    }

    /**
     * Handles the click on an hour.
     * @param item The hour that was clicked.
     * @param date The date button.
     */
    override fun onHourClick(item: String, date: Button) {
        val finalDate = "$selectedDateFormatted $item"
        dateTime = "$selectedDate;$item"
        date.text = finalDate
    }

    /**
     * Handles the click on a vaccine.
     * @param vaccineName The name of the vaccine that was clicked.
     * @param healthcareUnitId The id of the healthcare unit.
     */
    @SuppressLint("SimpleDateFormat")
    override suspend fun onVaccineClick(
        vaccineName: String,
        healthcareUnitId: Int,
        isSelected: Boolean
    ) {
        if (!isSelected) {
            FvaccineID = queries.getVaccinationId(vaccineName, healthcareUnitId)!!
            Log.d("VACCINEID", "vaccine id: $FvaccineID")
        } else {
            FvaccineID = 0
            Log.d("VACCINEID", "vaccine id: $FvaccineID")
        }

        // check if we're reserving for the same vaccine
        var prevApp: Appointments? = null
        runBlocking {
            launch(Dispatchers.IO) {
                prevApp = queries.getAppointment(appId)
            }
        }

        val previousVaccineId = prevApp!!.vaccinationId!!

        if (!isSelected) {
            // get all records for user
            val email = FirebaseAuth.getInstance().currentUser!!.email
            var userId = 0
            var records: List<Records>? = null
            var currentAppointment: Appointments? = null
            var currentRecord: Records? = null
            runBlocking {
                launch(Dispatchers.IO) {
                    userId = queries.getUserId(email!!)!!.toInt()
                    records = queries.getAllRecordsForUserId(userId)?.toList()
                    currentAppointment = queries.getAppointment(appId)
                    val recId = currentAppointment!!.recordId!!
                    currentRecord = queries.getRecord(recId)
                }
            }

            val currentRecordDate = currentRecord!!.dateAdministered!!

            var vacc1: Vaccinations? = null
            var vacc2: Vaccinations? = null

            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getTimeZone("CET")
            val currentDate = calendar.time
            Log.d("MASLO", "currentDate: $currentDate")


            if (FvaccineID != previousVaccineId) {
                // get the apointment for FvaccineId and currentRecordDate
                var userAppointments: Set<Appointments>? = null
                runBlocking {
                    launch(Dispatchers.IO) {
                        userAppointments = queries.getAllAppointmentsForUserId(userId)
                    }
                }

                // filter userAppointments to get only ones with FvaccineId and currentRecordDate
                val filteredApps = userAppointments?.filter { app ->
                    app.vaccinationId == FvaccineID
                            && app.date!! == currentRecordDate
                }

                // it should leave a list of only one appointment
                if (!filteredApps.isNullOrEmpty()) {
                    val app = filteredApps[0]
                    val recId = app.recordId!!
                    var record: Records? = null
                    runBlocking {
                        launch(Dispatchers.IO) {
                            record = queries.getRecord(recId)
                        }
                    }
                    minDate = record?.nextDoseDueDate
                } else {
                    minDate = null
                }
            } else {

                // find which dose we're updating
                var doseNumber = currentRecord!!.dose!!
                if (doseNumber > 1) {
                    doseNumber -= 1

                    //filter records by vaccineId
                    val filteredRecords = records?.filter { record ->
                        runBlocking {
                            launch(Dispatchers.IO) {
                                vacc1 = queries.getVaccination(FvaccineID)
                                vacc2 = queries.getVaccination(record.vaccineId!!)
                            }
                        }

                        Log.d("MASLO", "date administered: ${record.dateAdministered}")

                        vacc1?.vaccineName == vacc2?.vaccineName
                                && record.dose == doseNumber
                                && (record.dateAdministered?.before(currentRecordDate) == true)
                    }

                    if(!filteredRecords.isNullOrEmpty()) {
                        // pick the one closest to the date of the record we're changing right now
                        val sortedRecords = filteredRecords.sortedBy { it.dateAdministered }
                            val index = sortedRecords.size.minus(1)
                            val record = index.let { sortedRecords[it] }
                            minDate = record.nextDoseDueDate
                }else{
                    minDate = null
                }
}
            }

        } else
            minDate = null
    }
}