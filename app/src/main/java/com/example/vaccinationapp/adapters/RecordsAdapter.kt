package com.example.vaccinationapp.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.DB.entities.Records
import com.example.vaccinationapp.DB.entities.Vaccinations
import com.example.vaccinationapp.R
import com.example.vaccinationapp.ui.Queries
import com.example.vaccinationapp.ui.history.HistoryFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RecordsAdapter (private val dataSet: List<Records>?, private val update: Button, private val  cancel: Button):
    RecyclerView.Adapter<RecordsAdapter.ViewHolder>() {
    private val queries = Queries()

    interface OnItemClickListener {
        fun onRecordClick(id: Int?, update: Button, cancel: Button)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: HistoryFragment) {
        this.listener = listener
    }
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val vaccineNameText: TextView = view.findViewById(R.id.vaccineName)
        val dateAdministeredText: TextView = view.findViewById(R.id.dateView)
        val doseText: TextView = view.findViewById(R.id.timeView)
        val nextDoseDateText: TextView = view.findViewById(R.id.addressView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).
        inflate(R.layout.item, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return dataSet!!.size
    }

    private var selected: Int? = null
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(dataSet != null) {
            val item = dataSet[position]
            // time - which dose was it
            // address - next dose due...

            var vaccine: Vaccinations? = null
            val vaccId = item.vaccineId!!
            val userId = item.userId!!
            val dateAdm = item.dateAdministered!!
            val nextDoseDue = item.nextDoseDueDate!!
            val currentDose = item.dose!!
            var dosesLeft = -1

            runBlocking {
                launch(Dispatchers.IO) {
                    vaccine = queries.getVaccination(vaccId)
                }
            }

            holder.dateAdministeredText.text = "Date administered:\n$dateAdm"
            holder.vaccineNameText.text = vaccine!!.vaccineName
            holder.nextDoseDateText.text = "Suggested next dose:\n$nextDoseDue"

            val noOfDoses = vaccine!!.noOfDoses!!

            dosesLeft = noOfDoses - currentDose

            holder.doseText.text = "Dose: $currentDose\nDoses left: $dosesLeft"

            if (position == selected) {
                holder.itemView.setBackgroundColor(Color.parseColor("#53B658"))
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT)
            }

            holder.itemView.setOnClickListener {
                var recordId: Int? = null
                runBlocking {
                    launch(Dispatchers.IO) {
                        recordId = queries.getRecordId(userId, vaccId, currentDose)
                    }
                }

                listener?.onRecordClick(recordId, update, cancel)

                selected = if (selected == position) null else position
                notifyDataSetChanged()
            }
        }
    }
}