package com.example.vaccinationapp.ui.history

import  android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vaccinationapp.DB.entities.Records
import com.example.vaccinationapp.DB.entities.Vaccinations
import com.example.vaccinationapp.R
import com.example.vaccinationapp.ui.Dates
import com.example.vaccinationapp.ui.Queries
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar

/**
 * Activity for editing a record.
 */
class EditRecord : AppCompatActivity() {
    private val queries = Queries()
    private val datesManager = Dates()
    private var dateFin: java.sql.Date? = null
    private var currentDose: Int? = null

    private var name: AutoCompleteTextView? = null
    private var dose: EditText? = null
    private var date: Button? = null
    private var cancel: Button? = null
    private var confirm: Button? = null
    private var selectedDate: String? = null

    /**
     * Creates the view for the edit record screen.
     * @param savedInstanceState The saved instance state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_record)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        name = findViewById(R.id.vaccineNameAdd)
        dose = findViewById(R.id.doseNumberAdd)
        date = findViewById(R.id.dateAdd)
        cancel = findViewById(R.id.cancelAdd)
        confirm = findViewById(R.id.confirmAdd)

        val id = intent.getIntExtra("recordId", 0)

        var vaccinations: Set<Vaccinations>? = null
        runBlocking {
            launch(Dispatchers.IO) {
                vaccinations = queries.getAllVaccines()
            }
        }

        val set = mutableSetOf<String>()
        vaccinations?.let {
            for (vaccination in it) {
                vaccination.vaccineName?.let { name ->
                    set.add(name)
                }
            }
        }
        val dictionary: List<String> = set.toList()

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            dictionary
        )
        name?.setAdapter(adapter)

        date?.setOnClickListener {
            showDatePickerDialog()
        }

        if (dateFin != null)
            date?.isEnabled = true

        confirm?.isEnabled = true
        confirm?.setOnClickListener {
            if (selectedDate.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val vaccineName = name?.text.toString().trim()
            val inputDose = dose?.text.toString().trim()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            dateFin = java.sql.Date(dateFormat.parse(selectedDate!!)!!.time)

            try {
                currentDose = inputDose.toInt()
            } catch (e: NumberFormatException) {
                currentDose = 0
                Toast.makeText(this, "Invalid input. Please enter a number.", Toast.LENGTH_SHORT)
                    .show()
            }

            val email = FirebaseAuth.getInstance().currentUser!!.email
            var userId = 0
            var vaccines: Set<Vaccinations>? = null
            runBlocking {
                launch(Dispatchers.IO) {
                    userId = queries.getUserId(email!!)!!.toInt()
                    vaccines = queries.getAllVaccines()
                }
            }

            val unit = getUnitOfVaccine(vaccineName, vaccines)
            var vaccineId: Int? = null
            var vaccine: Vaccinations? = null
            runBlocking {
                launch(Dispatchers.IO) {
                    vaccineId = unit?.let { it1 -> queries.getVaccinationId(vaccineName, it1) }
                    vaccine = vaccineId?.let { it1 -> queries.getVaccination(it1) }
                }
            }

            var nextDose: java.sql.Date? = null
            val interval = vaccine?.timeBetweenDoses
            val intSplit = interval?.split(";")
            val index = currentDose?.minus(1)
            if (index != null && intSplit != null) {
                nextDose = checkDose(index, intSplit, dateFin!!)
            }

            val updatedRecord = Records(userId, vaccineId, dateFin, currentDose, nextDose)

            runBlocking {
                launch(Dispatchers.IO) {
                    queries.updateRecord(id, updatedRecord)
                }
            }
            setResult(RESULT_OK)
            finish()
        }

        cancel?.setOnClickListener {
            finish()
        }
    }

    /**
     * Checks the dose of the vaccine.
     * @param index The index of the dose.
     * @param intSplit The list of intervals between doses.
     * @param Fdate The date of the dose.
     * @return The date of the next dose.
     */
    private fun checkDose(
        index: Int,
        intSplit: List<String>,
        Fdate: java.sql.Date
    ): java.sql.Date? {
        var nextDoseDate: java.sql.Date? = null
        if (index >= 0 && index < intSplit.size) {
            nextDoseDate = addDaysToDate(Fdate, intSplit[index].toInt())
        } else if (index >= intSplit.size) {
            currentDose = 1
            val newIndex = 0
            checkDose(newIndex, intSplit, Fdate)
        }
        return nextDoseDate
    }

    /**
     * Adds days to a date.
     * @param date The date.
     * @param days The number of days to add.
     * @return The new date.
     */
    fun addDaysToDate(date: java.sql.Date, days: Int): java.sql.Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return java.sql.Date(calendar.timeInMillis)
    }

    /**
     * Gets the healthcare unit of a vaccine.
     * @param vaccineName The name of the vaccine.
     * @param vaccineList The list of vaccines.
     * @return The healthcare unit of the vaccine.
     */
    fun getUnitOfVaccine(vaccineName: String, vaccineList: Set<Vaccinations>?): Int? {
        if (!vaccineList.isNullOrEmpty()) {
            for (vaccine in vaccineList) {
                if (vaccine.vaccineName.equals(vaccineName, ignoreCase = true)) {
                    return vaccine.healthcareUnitId
                }
            }
        }
        return null
    }

    /**
     * Shows the date picker dialog.
     */
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                val monthName = DateFormatSymbols().months[selectedMonth]
                val selectedDateFormatted = "$selectedDay $monthName $selectedYear"

                if (datesManager.isDateValid(selectedYear, selectedMonth, selectedDay)) {
                    date?.text = selectedDateFormatted
                } else {
                    Toast.makeText(
                        this,
                        "Please select a date that is not in the future",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            currentYear,
            currentMonth,
            currentDay
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }
}