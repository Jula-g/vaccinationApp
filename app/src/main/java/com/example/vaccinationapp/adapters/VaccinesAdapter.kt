package com.example.vaccinationapp.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class VaccinesAdapter(private var dataSet: List<String>):
    RecyclerView.Adapter<VaccinesAdapter.ViewHolder>() {

    interface OnItemClickListener {
        suspend fun onVaccineClick(vaccineName: String, healthcareUnitId: Int)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val buttonVaccine : TextView = view.findViewById(R.id.vaccineButton)
        val buttonAddress : TextView = view.findViewById(R.id.addressText)
        val buttonUnit : TextView = view.findViewById(R.id.unitText)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.vaccine_item, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return dataSet.size
    }

    private var selected: Int? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        val itemSplit = item.split(";")

        val name = itemSplit[0]
        val unitName = itemSplit[1]
        val address = itemSplit[2]
        val unitId = itemSplit[3].toInt()

        holder.buttonVaccine.text = name
        holder.buttonAddress.text = address
        holder.buttonUnit.text = unitName

        if (position == selected) {
            holder.itemView.setBackgroundColor(Color.parseColor("#53B658"))
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        holder.itemView.setOnClickListener {
            runBlocking { launch(Dispatchers.IO) {
            listener?.onVaccineClick(name, unitId)
            } }

            selected = if (selected == position) null else position
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<String>) {
        dataSet = newData
        notifyDataSetChanged()
    }

}