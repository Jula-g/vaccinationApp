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
 * Adapter for upcoming appointments, used in ManageRecordsFragment
 *
 * @property dataSet
 * @property update
 * @property cancel
 */
class UpcomingAppointmentsAdapter(
    private val dataSet: List<Appointments>,
    private val update: Button,
    private val cancel: Button
) :
    RecyclerView.Adapter<UpcomingAppointmentsAdapter.ViewHolder>() {

    private val queries = Queries()

    /**
     * Interface for item click listener
     */
    interface OnItemClickListener {
        fun onRecordClick(id: Int?, update: Button, cancel: Button)
    }

    private var listener: OnItemClickListener? = null

    /**
     * Set on item click listener
     *
     * @param listener
     */
    fun setOnItemClickListener(listener: ManageRecordsFragment) {
        this.listener = listener
    }

    /**
     * View holder for upcoming appointments
     *
     * @property view
     * @property buttonName
     * @property buttonDate
     * @property buttonTime
     * @property buttonAddress
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buttonName: TextView = view.findViewById(R.id.vaccineName)
        val buttonDate: TextView = view.findViewById(R.id.dateView)
        val buttonTime: TextView = view.findViewById(R.id.timeView)
        val buttonAddress: TextView = view.findViewById(R.id.addressView)
    }

    /**
     * Create view holder for upcoming appointments
     *
     * @param parent
     * @param viewType
     * @return ViewHolder
     */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UpcomingAppointmentsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return UpcomingAppointmentsAdapter.ViewHolder(view)
    }

    /**
     * Get number of items in data set
     *
     * @return Int number of items
     */
    override fun getItemCount(): Int {
        return dataSet.size
    }

    private var selected: Int? = null

    /**
     * Bind data to view holder for upcoming appointments
     *
     * @param holder
     * @param position
     */
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]

        val vaccId = item.vaccinationId!!.toInt()
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