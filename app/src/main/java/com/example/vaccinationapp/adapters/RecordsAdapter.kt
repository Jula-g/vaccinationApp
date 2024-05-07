package com.example.vaccinationapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.R
import com.example.vaccinationapp.DB.entities.Appointments
import com.example.vaccinationapp.ui.managerecords.ManageRecordsFragment

class RecordsAdapter (private val dataSet: List<Appointments>):
    RecyclerView.Adapter<RecordsAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onRecordClick(item: String, date: Button)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: ManageRecordsFragment) {
        this.listener = listener
    }
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.vaccineName)
        val date = view.findViewById<TextView>(R.id.dateView)
        val time = view.findViewById<TextView>(R.id.timeView)
        val address = view.findViewById<TextView>(R.id.addressView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordsAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.item, parent, false)
        return RecordsAdapter.ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return dataSet.size
    }
    override fun onBindViewHolder(holder: RecordsAdapter.ViewHolder, position: Int) {
        val item = dataSet[position]
    }


}