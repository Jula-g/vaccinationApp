package com.example.vaccinationapp.ui.managerecords.reschedule

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vaccinationapp.DBconnection
import com.example.vaccinationapp.R
import com.example.vaccinationapp.adapters.HoursAdapter
import com.example.vaccinationapp.entities.Appointments
import com.example.vaccinationapp.queries.AppointmentsQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class RescheduleActivity : AppCompatActivity(), HoursAdapter.OnItemClickListener {

    private var appointment: Appointments? = null
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

        val vaccId = intent.getIntExtra("appointmentId", -1)

        val name = findViewById<TextView>(R.id.vaccineName)

        runBlocking { launch(Dispatchers.IO){
            appointment = getAppointment(vaccId)
        } }

//        name.text = appointment

    }

    private suspend fun getAppointment(id:Int): Appointments?{
        return withContext(Dispatchers.IO) {
            val conn = DBconnection.getConnection()
            val query = AppointmentsQueries(conn)
            val result = query.getAppointment(id)
            conn.close()
            result
        }
    }

    override fun onHourClick(item: String, date: Button) {
        TODO("Not yet implemented")
    }


}