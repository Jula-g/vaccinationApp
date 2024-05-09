package com.example.vaccinationapp.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.R
import com.example.vaccinationapp.DB.entities.Appointments
import com.example.vaccinationapp.DB.entities.HealthcareUnits
import com.example.vaccinationapp.DB.entities.Vaccinations
import com.example.vaccinationapp.ui.Queries
import com.example.vaccinationapp.ui.managerecords.ManageRecordsFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter for managing the display of records in a RecyclerView.
 * This adapter binds the data of records to the corresponding views.
 *
 * @property dataSet List of records to be displayed.
 * @property update Button that represents the update action.
 * @property cancel Button that represents the cancel action.
 */
class RecordsAdapter(
    private val dataSet: List<Appointments>,
    private val update: Button,
    private val cancel: Button
) :
    RecyclerView.Adapter<RecordsAdapter.ViewHolder>() {

    private val queries = Queries()

    /**
     * Interface for handling click events on a record item.
     */
    interface OnItemClickListener {
        fun onRecordClick(id: Int?, update: Button, cancel: Button)
    }

    private var listener: OnItemClickListener? = null

    /**
     * Sets the listener that will be notified when a record item is clicked.
     *
     * @param listener The listener to set.
     */
    fun setOnItemClickListener(listener: ManageRecordsFragment) {
        this.listener = listener
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buttonName = view.findViewById<TextView>(R.id.vaccineName)
        val buttonDate = view.findViewById<TextView>(R.id.dateView)
        val buttonTime = view.findViewById<TextView>(R.id.timeView)
        val buttonAddress = view.findViewById<TextView>(R.id.addressView)
    }

    /**
     * ViewHolder for managing the individual items in the RecyclerView.
     * Handles binding data to views and setting up click listeners.
     *
     * @param view The view for an individual item in the RecyclerView.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return RecordsAdapter.ViewHolder(view)
    }

    /**
     * This function returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return dataSet.size
    }

    private var selected: Int? = null

    /**
     * This function is called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     * This new ViewHolder should be constructed with a new View that can represent the items of the given type.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: RecordsAdapter.ViewHolder, position: Int) {
        val item = dataSet[position]

        val vaccId = item.vaccinationId!!.toInt()
        val userId = item.userId!!.toInt()
        val date = item.date.toString()
        val time = item.time

        val outputTimeFormat = SimpleDateFormat("H:mm", Locale.getDefault())
        val timeF = time?.let { outputTimeFormat.format(it) }

        holder.buttonDate.text = date
        holder.buttonTime.text = timeF
        holder.buttonName.text = "Loading..."
        holder.buttonAddress.text = "Loading..."

        var vaccine: Vaccinations? = null
        var unit: HealthcareUnits? = null
        runBlocking {
            launch(Dispatchers.IO) {
                vaccine = queries.getVaccination(vaccId)
                val unitId = vaccine?.healthcareUnitId
                unit = unitId?.let { queries.getHealthcareUnit(it) }
            }
        }

        holder.buttonName.text = vaccine?.vaccineName

        val address = "${unit?.city} ${unit?.street} ${unit?.streetNumber}"

        holder.buttonAddress.text = address

        if (position == selected) {
            holder.itemView.setBackgroundColor(Color.parseColor("#53B658"))
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        holder.itemView.setOnClickListener {
            var appointmentId: Int? = null
            runBlocking {
                launch(Dispatchers.IO) {
                    appointmentId = queries.getAppointmentId(date, time.toString())
                }
            }

            listener?.onRecordClick(appointmentId, update, cancel)

            selected = if (selected == position) null else position
            notifyDataSetChanged()
        }
    }
}