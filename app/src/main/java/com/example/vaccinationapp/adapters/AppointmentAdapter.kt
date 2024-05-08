package com.example.vaccinationapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.DB.DBconnection
import com.example.vaccinationapp.DB.entities.Appointments
import com.example.vaccinationapp.DB.entities.Vaccinations
import com.example.vaccinationapp.DB.queries.VaccinationsQueries
import com.example.vaccinationapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale

class AppointmentAdapter(
    private val appointments: List<Appointments>,
    private val onItemClick: (Appointments) -> Unit
) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.bind(appointment)
    }

    override fun getItemCount(): Int {
        return appointments.size
    }

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        @SuppressLint("SetTextI18n")
        fun bind(appointment: Appointments) {
            val vaccination = getVaccination(appointment.vaccinationId!!)

            itemView.findViewById<TextView>(R.id.textViewAppointmentName).text =
                vaccination?.vaccineName.toString()
                    .replaceFirstChar { it.uppercase(Locale.getDefault()) }
            itemView.findViewById<TextView>(R.id.textViewAppointmentDate).text =
                "\t\tDate: " + appointment.date.toString()
            itemView.findViewById<TextView>(R.id.textViewAppointmentTime).text =
                "\t\tTime: " + appointment.time.toString()
        }

        override fun onClick(view: View) {
            onItemClick(appointments[adapterPosition])
        }
    }

    private fun getVaccination(vaccinationId: Int): Vaccinations? {
        var vaccination: Vaccinations? = null
        runBlocking {
            launch(Dispatchers.IO) {
                val connection = DBconnection.getConnection()
                val va = VaccinationsQueries(connection)
                vaccination = va.getVaccination(vaccinationId)
                connection.close()
            }
        }
        return vaccination
    }
}
