package com.example.vaccinationapp.ui.history

import android.annotation.SuppressLint
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
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class HistoryFragment : Fragment(), RecordsAdapter.OnItemClickListener {
    private val queries = Queries()
    private lateinit var recordsRecycler: RecyclerView
    private lateinit var adapter: RecordsAdapter
    private var userId: Int = 0
    private var records: List<Records>? = null

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

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

        runBlocking { launch(Dispatchers.IO) {
            //if user has an account and is logged in, it must be in the database so userID will never be null
            userId = queries.getUserId(email!!)!!.toInt()
            records = queries.getAllRecordsForUserId(userId)?.toList()
        } }

        // show only past records!!!!!!!! needs a function
        val pastRecords = selectPast(records)

        if(!records.isNullOrEmpty()) {
            recordsRecycler = root.findViewById(R.id.recordsHistoryRecycler)
            recordsRecycler.layoutManager = LinearLayoutManager(context)

            adapter = RecordsAdapter(pastRecords, edit, delete)
            recordsRecycler.adapter = adapter
            adapter.setOnItemClickListener(this)
        }

        add.setOnClickListener {
            val intent = Intent(requireContext(), AddRecord::class.java)
            startActivity(intent)
        }

        return root
    }

    private fun selectPast(records: List<Records>?): List<Records> {
        val pastAppointments = mutableSetOf<Records>()

        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())
        val currentDate = calendar.time
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone(ZoneId.systemDefault())

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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRecordClick(id: Int?, update: Button, cancel: Button) {

        update.setOnClickListener {
            val updateRecord = Intent(requireContext(), EditRecord::class.java)
            updateRecord.putExtra("recordId", id)
            startActivity(updateRecord)
        }

        cancel.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Are you sure you want to delete the record?")
                .setPositiveButton("Yes") { dialog, _ ->
                    runBlocking {
                        launch(Dispatchers.IO) {
                            id?.let { it1 -> queries.deleteRecord(it1)}
                            id?.let { it1 -> queries.deleteAppointment(it1)}
                        }
                    }
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

            val alert = builder.create()
            alert.show()
        }
    }
}