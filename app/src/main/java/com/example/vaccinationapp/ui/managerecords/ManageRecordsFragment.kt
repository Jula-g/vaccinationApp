package com.example.vaccinationapp.ui.managerecords

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.DB.DBconnection
import com.example.vaccinationapp.R
import com.example.vaccinationapp.adapters.RecordsAdapter
import com.example.vaccinationapp.databinding.FragmentManageRecordsBinding
import com.example.vaccinationapp.DB.entities.Appointments
import com.example.vaccinationapp.DB.queries.AppointmentsQueries
import com.example.vaccinationapp.DB.queries.UsersQueries
import com.example.vaccinationapp.ui.managerecords.schedule.ScheduleActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class ManageRecordsFragment : Fragment(), RecordsAdapter.OnItemClickListener {
    private lateinit var recordsRecycler: RecyclerView
    private lateinit var adapter: RecordsAdapter
    private var _binding: FragmentManageRecordsBinding? = null
    private val binding get() = _binding!!
    private var userId: Int = 0
    private var appointments: List<Appointments>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val addViewModel =
            ViewModelProvider(this).get(ManageRecordsViewModel::class.java)

        _binding = FragmentManageRecordsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val editRecord: Button = root.findViewById(R.id.editbtn)
        val addRecord: Button = root.findViewById(R.id.addbtn)
        val deleteRecord: Button = root.findViewById(R.id.deletebtn)


//        val email = FirebaseAuth.getInstance().currentUser!!.email
//        runBlocking { launch(Dispatchers.IO) {
//            //if user has an account and is logged in, it must be in the database so userID will never be null
//            userId = getUserId(email!!)!!.toInt()
//            appointments = getAllAppointmentsForUserId(userId)?.toList()
//        } }
//
//        recordsRecycler = root.findViewById<RecyclerView>(R.id.recordsRecyclerView)
//        recordsRecycler.layoutManager = LinearLayoutManager(context)
//
//        if(!appointments.isNullOrEmpty()) {
//            adapter = RecordsAdapter(appointments!!)
//            recordsRecycler.adapter = adapter
//        }


        editRecord.setOnClickListener {
            val reschedule = Intent(requireContext(), ScheduleActivity::class.java)
            startActivity(reschedule)
        }

        addRecord.setOnClickListener {
            val schedule = Intent(requireContext(), ScheduleActivity::class.java)
            startActivity(schedule)
        }

        deleteRecord.setOnClickListener {
            // pop up window with "are you sure" question
        }


        return root
    }

    //select only upcoming appointments
//    private fun selectUpcoming(appointments: Set<Appointments>): Set<Appointments>{
//        var upcomingAppointments = mutableSetOf<Appointments>()
//        for(appointment in appointments){
//            if (appointment.date)
//        }
//    }

    private suspend fun getAllAppointmentsForUserId(id: Int): Set<Appointments>? {
        return withContext(Dispatchers.IO) {
            val conn = DBconnection.getConnection()
            val appQueries = AppointmentsQueries(conn)
            val result = appQueries.getAllAppointmentsForUserId(id)
            conn.close()
            result
        }
    }

    private suspend fun getUserId(email: String): Int? {
        return withContext(Dispatchers.IO){
            val conn = DBconnection.getConnection()
            val userQueries = UsersQueries(conn)
            val result = userQueries.getUserId(email)
            Log.d("DATABASE", "user ID: $result")
            conn.close()
            result
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRecordClick(item: String, date: Button) {
        TODO("Not yet implemented")
    }
}