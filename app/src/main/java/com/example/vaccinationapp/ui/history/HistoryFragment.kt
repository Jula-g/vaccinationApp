package com.example.vaccinationapp.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vaccinationapp.databinding.FragmentHistoryBinding

/**
 * Fragment for the history screen.
 */
class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    /**
     * Creates the view for the history screen.
     * @param inflater The layout inflater.
     * @param container The view group container.
     * @param savedInstanceState The saved instance state.
     * @return The view for the history screen.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    /**
     * Destroys the view for the history screen.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}