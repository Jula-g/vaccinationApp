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


/**
 * Adapter for managing the display of records in a RecyclerView.
 * This adapter binds the data of records to the corresponding views.
 *
 * @property dataSet List of records to be displayed.
 * @property update Button that represents the update action.
 * @property delete Button that represents the delete action.
 */
class RecordsAdapter (private val dataSet: MutableList<Records>?, private val update: Button, private val  delete: Button):
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
    fun setOnItemClickListener(listener: HistoryFragment) {
        this.listener = listener
    }

    /**
     * ViewHolder for managing the individual items in the RecyclerView.
     * Handles binding data to views and setting up click listeners.
     *
     * @param view The view for an individual item in the RecyclerView.
     */
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

    /**
     * This function returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    override fun getItemCount(): Int {
        return dataSet!!.size
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
                        recordId = queries.getRecordId(userId, vaccId, currentDose, dateAdm)
                    }
                }

                listener?.onRecordClick(recordId, update, delete)

                selected = if (selected == position) null else position
                notifyDataSetChanged()
            }
        }
    }
    fun updateDataSet(newDataSet: List<Records>?) {
        dataSet?.clear()
        newDataSet?.let { dataSet?.addAll(it) }
    }

}