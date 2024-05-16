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

/**
 * Adapter for managing the display of vaccines in a RecyclerView.
 * This adapter binds the data of vaccines to the corresponding views.
 *
 * @property dataSet List of vaccines to be displayed.
 */
class VaccinesAdapter(private var dataSet: List<String>) :
    RecyclerView.Adapter<VaccinesAdapter.ViewHolder>() {

    /**
     * Interface for handling click events on a vaccine item.
     */
    interface OnItemClickListener {
        suspend fun onVaccineClick(vaccineName: String, healthcareUnitId: Int, isSelected: Boolean)
    }

    private var listener: OnItemClickListener? = null

    /**
     * Sets the listener that will be notified when a vaccine item is clicked.
     *
     * @param listener The listener to set.
     */
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    /**
     * ViewHolder for managing the individual items in the RecyclerView.
     * Handles binding data to views and setting up click listeners.
     *
     * @param view The view for an individual item in the RecyclerView.
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val buttonVaccine: TextView = view.findViewById(R.id.vaccineButton)
        val buttonAddress: TextView = view.findViewById(R.id.addressText)
        val buttonUnit: TextView = view.findViewById(R.id.unitText)
    }

    /**
     * This function is called when RecyclerView needs a new ViewHolder of the given type to represent an item.
     * This new ViewHolder should be constructed with a new View that can represent the items of the given type.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.vaccine_item, parent, false)
        return ViewHolder(view)
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
     * This function is called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the itemView to reflect the item at the given position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
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
            val isSelected = (selected == position)

            runBlocking { launch(Dispatchers.IO) {
            listener?.onVaccineClick(name, unitId, isSelected)
            } }

            selected = if (selected == position) null else position
            notifyDataSetChanged()
        }
    }

    /**
     * Updates the data set of the adapter and notifies the RecyclerView that the data set has changed.
     *
     * @param newData The new data set.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: List<String>) {
        dataSet = newData
        notifyDataSetChanged()
    }
}