package com.example.vaccinationapp.ui.history

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
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
import androidx.lifecycle.lifecycleScope
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
import java.util.Date

class AddRecord : AppCompatActivity() {
    private val queries = Queries()
    private val datesManager = Dates()
    private var dateFin: java.sql.Date? = null
    private var currentDose: Int? = null
    private var nextDoseDate: java.sql.Date? = null

    private val name = findViewById<AutoCompleteTextView>(R.id.vaccineNameAdd)
    private val dose = findViewById<EditText>(R.id.doseNumberAdd)
    private val date = findViewById<Button>(R.id.dateAdd)
    private val cancel = findViewById<Button>(R.id.cancelAdd)
    private val confirm = findViewById<Button>(R.id.confirmAdd)
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_record)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        confirm.isEnabled = false

        var vaccinations : Set<Vaccinations>? = null
        runBlocking { launch(Dispatchers.IO){
            vaccinations = queries.getAllVaccines()
        } }

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
        name.setAdapter(adapter)

        date.setOnClickListener{
            val dateString = showDatePickerDialog()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd")
            dateFin = java.sql.Date(dateFormat.parse(dateString)!!.time)
        }

        val vaccineName = name.text.toString().trim()
        val inputDose = dose.text.toString().trim()

        if (dateFin!=null)
            date.isEnabled = true

        confirm.setOnClickListener{
            try {
                currentDose = inputDose.toInt()
            } catch (e: NumberFormatException) {
                currentDose = 0
                Toast.makeText(this, "Invalid input. Please enter a number.", Toast.LENGTH_SHORT).show()
            }

            val email = FirebaseAuth.getInstance().currentUser!!.email
            var userId = 0
            var vaccines : Set<Vaccinations>? = null
            runBlocking { launch(Dispatchers.IO) {
                userId = queries.getUserId(email!!)!!.toInt()
                vaccines = queries.getAllVaccines()
            } }

            val unit = getUnitOfVaccine(vaccineName,vaccines)
            var vaccineId : Int? = null
            var vaccine :Vaccinations? = null
            runBlocking { launch(Dispatchers.IO) {
                vaccineId = unit?.let { it1 -> queries.getVaccinationId(vaccineName, it1) }
                vaccine = vaccineId?.let { it1 -> queries.getVaccination(it1) }
            } }

            val interval = vaccine?.timeBetweenDoses
            val intSplit = interval?.split(";")
            val index = currentDose?.minus(1)
            if (index != null && intSplit!=null)  {
                checkDose(index, intSplit, dateFin!!)
            }

            val record = Records(userId,vaccineId,dateFin,currentDose, nextDoseDate)
            runBlocking { launch(Dispatchers.IO) {
                queries.addRecord(record)
            } }

        }
    }

    private fun checkDose(index: Int, intSplit: List<String>, Fdate: java.sql.Date): java.sql.Date? {
        var nextDoseDate : java.sql.Date? = null
        if(index >= 0 && index < intSplit.size ) {
            nextDoseDate = addDaysToDate(Fdate, intSplit[index].toInt())
        }else if (index > intSplit.size){
            currentDose = 0
            checkDose(index, intSplit, Fdate)
        }
        return nextDoseDate
    }

    fun addDaysToDate(date: java.sql.Date, days: Int): java.sql.Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, days)
        return java.sql.Date(calendar.timeInMillis)
    }


    fun getUnitOfVaccine(vaccineName: String, vaccineList: Set<Vaccinations>?): Int? {
        if(!vaccineList.isNullOrEmpty()) {
            for (vaccine in vaccineList) {
                if (vaccine.vaccineName.equals(vaccineName, ignoreCase = true)) {
                    return vaccine.healthcareUnitId
                }
            }
        }
        return null
    }

    private fun showDatePickerDialog(): String {
        var selectedDate= ""
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int ->
                selectedDate = "$selectedYear-${selectedMonth+1}-$selectedDay"
                val monthName = DateFormatSymbols().months[selectedMonth]
                val selectedDateFormatted = "$selectedDay $monthName $selectedYear"

                if (datesManager.isDateValid(selectedYear, selectedMonth, selectedDay)) {
                    date.text = selectedDateFormatted
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
        return selectedDate
    }
}