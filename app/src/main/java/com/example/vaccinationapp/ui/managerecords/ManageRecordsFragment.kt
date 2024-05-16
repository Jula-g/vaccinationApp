package com.example.vaccinationapp.ui.managerecords

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
import com.example.vaccinationapp.R
import com.example.vaccinationapp.adapters.UpcomingAppointmentsAdapter
import com.example.vaccinationapp.databinding.FragmentManageRecordsBinding
import com.example.vaccinationapp.DB.entities.Appointments
import com.example.vaccinationapp.ui.Queries
import com.example.vaccinationapp.ui.managerecords.reschedule.RescheduleActivity
import com.example.vaccinationapp.ui.managerecords.schedule.ScheduleActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class ManageRecordsFragment : Fragment(), UpcomingAppointmentsAdapter.OnItemClickListener {
    private val queries = Queries()
    private lateinit var recordsRecycler: RecyclerView
    private lateinit var adapter: UpcomingAppointmentsAdapter
    private var _binding: FragmentManageRecordsBinding? = null
    private val binding get() = _binding!!
    private var userId: Int = 0
    private var appointments: List<Appointments>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val addViewModel =
//            ViewModelProvider(this).get(ManageRecordsViewModel::class.java)

        _binding = FragmentManageRecordsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val editRecord: Button = root.findViewById(R.id.editbtn)
        val addRecord: Button = root.findViewById(R.id.addbtn)
        val deleteRecord: Button = root.findViewById(R.id.deletebtn)

        val email = FirebaseAuth.getInstance().currentUser!!.email

        runBlocking { launch(Dispatchers.IO) {
            //if user has an account and is logged in, it must be in the database so userID will never be null
            userId = queries.getUserId(email!!)!!.toInt()
            appointments = queries.getAllAppointmentsForUserId(userId)?.toList()
        } }

        val upcomingAppointments = selectUpcoming(appointments)

        //select only upcoming

        recordsRecycler = root.findViewById(R.id.recordsRecyclerView)
        recordsRecycler.layoutManager = LinearLayoutManager(context)

        adapter = UpcomingAppointmentsAdapter(upcomingAppointments, editRecord, deleteRecord)
        recordsRecycler.adapter = adapter

        adapter.setOnItemClickListener(this)


        addRecord.setOnClickListener {
            val schedule = Intent(requireContext(), ScheduleActivity::class.java)
            startActivity(schedule)
        }

        return root
    }

    //select only upcoming appointments
    private fun selectUpcoming(appointments: List<Appointments>?): List<Appointments> {
        val upcomingAppointments = mutableSetOf<Appointments>()

        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("CET")
        val currentDate = calendar.time
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        dateTimeFormat.timeZone = TimeZone.getTimeZone("CET")

        if (appointments != null) {
            for (appointment in appointments) {
                val appointmentDateTimeString = "${appointment.date} ${appointment.time}"
                val appointmentDateTime = dateTimeFormat.parse(appointmentDateTimeString)

                if (appointmentDateTime != null) {
                    if (appointmentDateTime.after(currentDate)) {
                        upcomingAppointments.add(appointment)
                    }
                }
            }
        }
        return upcomingAppointments.toList()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onRecordClick(id: Int?, update: Button, cancel: Button) {

        update.setOnClickListener {
            val reschedule = Intent(requireContext(), RescheduleActivity::class.java)
            reschedule.putExtra("appointmentId", id)
            startActivity(reschedule)
        }

        cancel.setOnClickListener {
            // pop up window with "are you sure" question
            val builder = AlertDialog.Builder(requireContext())
            builder.setMessage("Are you sure you want to cancel the appointment?")
                .setPositiveButton("Yes") { dialog, _ ->
                    runBlocking { launch(Dispatchers.IO) {
                        val appointment = queries.getAppointment(id!!)
                        val recordId = appointment!!.recordId
                        if (recordId != null) {
                            queries.deleteRecord(recordId)
                        }
                        queries.deleteAppointment(id)
                    } }
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