package com.example.vaccinationapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.R
import com.example.vaccinationapp.ui.schedule.ScheduleActivity

class HoursAdapter(private val dataSet: List<String>, private val date: Button):
    RecyclerView.Adapter<HoursAdapter.ViewHolder>(){

    interface OnItemClickListener {
        fun onItemClick(item: String, date: Button)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: ScheduleActivity) {
        this.listener = listener
    }

    class ViewHolder(view: View):  RecyclerView.ViewHolder(view) {
        val buttonHour : TextView = view.findViewById(R.id.hourButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.hour_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataSet[position]
        holder.buttonHour.text = item

//        holder.itemView.setBackgroundColor(Color.parseColor("#A9F1EF")) // #A9F1EF looks good

        holder.itemView.setOnClickListener {
            listener?.onItemClick(item, date)
        }
    }
}