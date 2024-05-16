package com.example.vaccinationapp.ui.history

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.DB.entities.Records
import com.example.vaccinationapp.R
import com.example.vaccinationapp.adapters.RecordsAdapter
import com.example.vaccinationapp.databinding.FragmentHistoryBinding
import com.example.vaccinationapp.ui.Queries
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Fragment for the history screen.
 */
class HistoryFragment : Fragment(), RecordsAdapter.OnItemClickListener {
    private val queries = Queries()
    private lateinit var recordsRecycler: RecyclerView
    private lateinit var adapter: RecordsAdapter
    private var userId: Int = 0
    private var records: MutableList<Records>? = null

    private val ADD_RECORD_REQUEST = 1
    private val EDIT_RECORD_REQUEST = 2

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    /**
     * Creates the view for the history screen.
     * @param inflater The layout inflater.
     * @param container The view group container.
     * @param savedInstanceState The saved instance state.
     * @return The view for the history screen.
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val add = root.findViewById<Button>(R.id.addBTN)
        val edit = root.findViewById<Button>(R.id.editBTN)
        val delete = root.findViewById<Button>(R.id.deleteBTN)
        val change = root.findViewById<Button>(R.id.changeBTN)

        val email = FirebaseAuth.getInstance().currentUser!!.email

        runBlocking {
            launch(Dispatchers.IO) {
                //if user has an account and is logged in, it must be in the database so userID will never be null
                userId = queries.getUserId(email!!)!!.toInt()
                records = queries.getAllRecordsForUserId(userId)?.toMutableList()
            }
        }
        recordsRecycler = root.findViewById(R.id.recordsHistoryRecycler)
        recordsRecycler.layoutManager = LinearLayoutManager(context)
        adapter = RecordsAdapter(mutableListOf(), edit, delete)
        recordsRecycler.adapter = adapter
        adapter.setOnItemClickListener(this)

        // show only past records!!!!!!!! needs a function
        var pastRecords = selectPast(records).toMutableList()

        if (pastRecords.isNotEmpty()) {
            recordsRecycler = root.findViewById(R.id.recordsHistoryRecycler)
            recordsRecycler.layoutManager = LinearLayoutManager(context)

            adapter = RecordsAdapter(pastRecords, edit, delete)
            recordsRecycler.adapter = adapter
            adapter.setOnItemClickListener(this)
        }

        add.setOnClickListener {
            val intent = Intent(requireContext(), AddRecord::class.java)
            startActivityForResult(intent, ADD_RECORD_REQUEST)
        }

        change.setOnClickListener {
            val sortedRecords = sortRecordsChronologically(pastRecords).toMutableList()

            pastRecords = sortedRecords
            adapter.updateDataSet(pastRecords)
            adapter.notifyDataSetChanged()
        }

        return root
    }

    /**
     * Selects past appointments from the list of records.
     * @param records The list of records.
     * @return The list of past appointments.
     */
    private fun selectPast(records: List<Records>?): List<Records> {
        val pastAppointments = mutableSetOf<Records>()

        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("CET")
        val currentDate = calendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("CET")

        if (records != null) {
            for (record in records) {
                val recordDateString = "${record.dateAdministered}"
                val recordDate = dateFormat.parse(recordDateString)

                if (recordDate != null) {
                    // Check if the appointment date and time is before the current date and time
                    if (recordDate.before(currentDate)) {
                        pastAppointments.add(record)
                    }
                }
            }
        }
        return pastAppointments.toList()
    }

    /**
     * Destroys the view for the history screen.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Handles the click on a record.
     * @param id The record ID.
     * @param update The update button.
     * @param cancel The cancel button.
     */
    @SuppressLint("NotifyDataSetChanged")
    override fun onRecordClick(id: Int?, update: Button, cancel: Button) {

        update.setOnClickListener {
            val updateRecord = Intent(requireContext(), EditRecord::class.java)
            updateRecord.putExtra("recordId", id)
            startActivityForResult(updateRecord, EDIT_RECORD_REQUEST)
        }

        cancel.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Are you sure you want to delete the record?")
                .setPositiveButton("Yes") { dialog, _ ->
                    deleteRecord(id!!)
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

            val alert = builder.create()
            alert.show()
        }
    }

    /**
     * Sorts the records chronologically.
     * @param records The list of records.
     * @return The list of records sorted chronologically.
     */
    private fun sortRecordsChronologically(records: List<Records>?): List<Records> {
        val sortedRecords = records?.sortedBy { it.dateAdministered }
        return sortedRecords ?: emptyList()
    }

    /**
     * Handles the result of an activity.
     * @param requestCode The request code.
     * @param resultCode The result code.
     * @param data The intent data.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == ADD_RECORD_REQUEST || requestCode == EDIT_RECORD_REQUEST) && resultCode == Activity.RESULT_OK) {
            reloadRecords()
        }
    }

    /**
     * Reloads the records.
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun reloadRecords() {
        runBlocking {
            launch(Dispatchers.IO) {
                userId =
                    queries.getUserId(FirebaseAuth.getInstance().currentUser!!.email!!)!!.toInt()
                records = queries.getAllRecordsForUserId(userId)?.toMutableList()
            }
        }
        val pastRecords = selectPast(records).toMutableList()
        adapter.updateDataSet(pastRecords)
        adapter.notifyDataSetChanged()
    }

    /**
     * Deletes a record.
     * @param recordId The record ID.
     */
    private fun deleteRecord(recordId: Int) {
        runBlocking {
            launch(Dispatchers.IO) {
                queries.deleteRecord(recordId)
            }
        }
        reloadRecords()
    }
}