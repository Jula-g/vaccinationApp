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

/**
 * Adapter for managing the display of appointments in a RecyclerView.
 * This adapter binds the data of appointments to the corresponding views.
 *
 * @property appointments List of appointments to be displayed.
 * @property onItemClick Callback function to be invoked when an item is clicked.
 */

class AppointmentAdapter(
    private val appointments: List<Appointments>,
    private val onItemClick: (Appointments) -> Unit
) :
    RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder>() {

    /**
     * This function is called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     * This new ViewHolder should be constructed with a new View that can represent the items of the given type.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_appointment, parent, false)
        return AppointmentViewHolder(view)
    }

    /**
     * This function is called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the itemView to reflect the item at the given position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        val appointment = appointments[position]
        holder.bind(appointment)
    }

    /**
     * This function returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return appointments.size
    }

    /**
     * ViewHolder for managing the individual items in the RecyclerView.
     * Handles binding data to views and setting up click listeners.
     *
     * @param itemView The view for an individual item in the RecyclerView.
     */
    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        /**
         * Binds appointment data to the views within the ViewHolder.
         * Sets up the text for vaccine name, date, and time.
         *
         * @param appointment The appointment object containing the data to be displayed.
         */
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

        /**
         * This function is called when the view is clicked.
         *
         * @param view The view that was clicked.
         */
        override fun onClick(view: View) {
            onItemClick(appointments[adapterPosition])
        }
    }

    /**
     * Fetches vaccination data from the database based on the provided vaccination ID.
     *
     * @param vaccinationId The ID of the vaccination for which data is to be retrieved.
     * @return The Vaccinations object corresponding to the provided ID, or null if not found.
     */
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
