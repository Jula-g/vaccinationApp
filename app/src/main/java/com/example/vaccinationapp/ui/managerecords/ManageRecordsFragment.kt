package com.example.vaccinationapp.ui.managerecords

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.vaccinationapp.R
import com.example.vaccinationapp.databinding.FragmentManageRecordsBinding
import com.example.vaccinationapp.ui.schedule.ScheduleActivity

class ManageRecordsFragment : Fragment() {

    private var _binding: FragmentManageRecordsBinding? = null
    private val binding get() = _binding!!

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}