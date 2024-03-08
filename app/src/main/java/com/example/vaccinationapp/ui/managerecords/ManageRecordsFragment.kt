package com.example.vaccinationapp.ui.managerecords

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.vaccinationapp.databinding.FragmentManageRecordsBinding

class ManageRecordsFragment : Fragment() {

    private var _binding: FragmentManageRecordsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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

//        val textView: TextView = binding.textAdd
//        addViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}