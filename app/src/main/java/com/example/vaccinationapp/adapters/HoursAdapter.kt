package com.example.vaccinationapp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.R
import com.example.vaccinationapp.ui.managerecords.schedule.ScheduleActivity

class HoursAdapter(private val dataSet: List<String>, private val date: Button):
    RecyclerView.Adapter<HoursAdapter.ViewHolder>(){

    interface OnItemClickListener {
        fun onHourClick(item: String, date: Button)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class ViewHolder(view: View):  RecyclerView.ViewHolder(view) {
        val buttonHour : TextView = view.findViewById(R.id.vaccineButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.hour_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    private var selected: Int? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        holder.buttonHour.text = item

        if (position == selected) {
            holder.itemView.setBackgroundColor(Color.parseColor("#53B658"))
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT)
        }

        holder.itemView.setOnClickListener {
            listener?.onHourClick(item, date)
            selected = if (selected == position) null else position
            notifyDataSetChanged()
        }
    }
}