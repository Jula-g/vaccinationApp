import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.vaccinationapp.databinding.FragmentCalendarBinding
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.Calendar

/**
 * Fragment for the calendar screen.
 */
class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    /**
     * Creates the view for the calendar screen.
     * @param inflater The layout inflater.
     * @param container The view group container.
     * @param savedInstanceState The saved instance state.
     * @return The view for the calendar screen.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val root: View = binding.root

        showMaterialDatePicker()

        return root
    }

    /**
     * Destroys the view for the calendar screen.
     */

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Shows the material date picker.
     */
    private fun showMaterialDatePicker() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val now = Calendar.getInstance()
        builder.setCalendarConstraints(
            com.google.android.material.datepicker.CalendarConstraints.Builder()
                .setStart(now.timeInMillis)
                .build()
        )
        val picker = builder.build()
        picker.show(parentFragmentManager, picker.toString())
    }

}
