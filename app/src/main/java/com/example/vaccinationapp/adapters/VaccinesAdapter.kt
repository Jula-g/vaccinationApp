package com.example.vaccinationapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.R
import com.example.vaccinationapp.entities.Vaccinations
import com.example.vaccinationapp.ui.schedule.ScheduleActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class VaccinesAdapter(private val dataSet: List<String>, private val date: Button):
    RecyclerView.Adapter<VaccinesAdapter.ViewHolder>() {

    interface OnItemClickListener {
        suspend fun onVaccineClick(vaccineName: String, healthcareUnitId: Int)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: ScheduleActivity) {
        this.listener = listener
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val buttonVaccine : TextView = view.findViewById(R.id.vaccineButton)
        val buttonAddress : TextView = view.findViewById(R.id.addressText)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.vaccine_item, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        val itemSplit = item.split(";")

        val name = itemSplit[0]
        val address = itemSplit[1]
        val unitId = itemSplit[2].toInt()

        holder.buttonVaccine.text = name
        holder.buttonAddress.text = address

        holder.itemView.setBackgroundColor(Color.parseColor("#53B658"))

        holder.itemView.setOnClickListener {
            runBlocking { launch(Dispatchers.IO) {
            listener?.onVaccineClick(name, unitId)
            } }
        }
    }
}