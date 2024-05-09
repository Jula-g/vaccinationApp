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

/**
 * Adapter for managing the display of hours in a RecyclerView.
 * This adapter binds the data of hours to the corresponding views.
 *
 * @property dataSet List of hours to be displayed.
 * @property date Button that represents the selected date.
 */
class HoursAdapter(private val dataSet: List<String>, private val date: Button) :
    RecyclerView.Adapter<HoursAdapter.ViewHolder>() {

    /**
     * Interface for handling click events on an hour item.
     */
    interface OnItemClickListener {
        fun onHourClick(item: String, date: Button)
    }

    private var listener: OnItemClickListener? = null

    /**
     * Sets the listener that will be notified when an hour item is clicked.
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
        val buttonHour: TextView = view.findViewById(R.id.vaccineButton)
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
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hour_item, parent, false)
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