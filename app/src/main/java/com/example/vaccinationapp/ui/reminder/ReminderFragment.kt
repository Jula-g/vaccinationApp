package com.example.vaccinationapp.ui.reminder

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.NumberPicker
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.DB.DBconnection
import com.example.vaccinationapp.DB.entities.Appointments
import com.example.vaccinationapp.DB.queries.AppointmentsQueries
import com.example.vaccinationapp.R
import com.example.vaccinationapp.adapters.AppointmentAdapter
import com.example.vaccinationapp.databinding.FragmentReminderBinding
import com.example.vaccinationapp.ui.reminder.alarm.Alarm
import com.example.vaccinationapp.ui.reminder.alarm.AndroidAlarmScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * ReminderFragment class is a Fragment class that displays the upcoming appointments and enables
 * the user to set reminders for the chosen appointments.
 */
class ReminderFragment : Fragment() {

    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!

    /**
     * onCreateView method is called to create and return the view hierarchy associated with the fragment
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment
     * @param container The parent view that the fragment's UI should be attached to
     * @param savedInstanceState The previously saved state of the fragment
     * @return The root view of the fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = root.findViewById(R.id.recycleAppointment)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadUpcomingAppointments(recyclerView) { appointment ->
            showDateTimePickerDialog(appointment)
        }

        return root
    }

    /**
     * onDestroyView method is called when the view is about to be destroyed
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * loadUpcomingAppointments method is used to load the upcoming appointments from the database
     * @param recyclerView The RecyclerView in which the appointments are displayed
     * @param onItemClick The lambda function that is called when an appointment is clicked
     */
    private fun loadUpcomingAppointments(
        recyclerView: RecyclerView,
        onItemClick: (Appointments) -> Unit
    ) {
        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            val connection = DBconnection.getConnection()
            val ap = AppointmentsQueries(connection)
            val appointmentsList = ap.getAllAppointments()?.toList() ?: emptyList()

            val upcomingAppointments = filterUpcomingAppointments(appointmentsList)

            withContext(Dispatchers.Main) {
                val adapter = AppointmentAdapter(upcomingAppointments, onItemClick)
                recyclerView.adapter = adapter
                connection.close()
            }
        }
    }

    /**
     * filterUpcomingAppointments method is used to filter the upcoming appointments from the list of appointments
     * @param appointments The list of appointments
     * @return The list of upcoming appointments
     */
    private fun filterUpcomingAppointments(appointments: List<Appointments>): List<Appointments> {
        val upcomingAppointments = mutableListOf<Appointments>()

        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("CET")
        val currentDate = calendar.time
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        dateTimeFormat.timeZone = TimeZone.getTimeZone("CET")

        for (appointment in appointments) {
            val appointmentDateTimeString = "${appointment.date} ${appointment.time}"
            val appointmentDateTime = dateTimeFormat.parse(appointmentDateTimeString)

            if (appointmentDateTime!!.after(currentDate)) {
                upcomingAppointments.add(appointment)
            }
        }
        return upcomingAppointments
    }


    /**
     * showDateTimePickerDialog method is used to show the date and time picker dialog to the user
     * @param appointment The appointment for which the reminder is to be set
     */
    private fun showDateTimePickerDialog(appointment: Appointments) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_time_picker, null)

        val hourPicker = dialogView.findViewById<NumberPicker>(R.id.hourPicker)
        val minutePicker = dialogView.findViewById<NumberPicker>(R.id.minutePicker)
        val datePicker = dialogView.findViewById<DatePicker>(R.id.datePicker)

        val calendar = Calendar.getInstance()
        val maxHour = appointment.time.toString().split(':')[0].toInt()
        val maxMinute = appointment.time.toString().split(':')[1].toInt()

        hourPicker.minValue = 0
        hourPicker.maxValue = 23

        minutePicker.minValue = 0
        minutePicker.maxValue = 59

        datePicker.minDate = calendar.timeInMillis

        val appointmentYear = appointment.date.toString().split('-')[0].toInt()
        val appointmentMonth = appointment.date.toString().split('-')[1].toInt() - 1
        val appointmentDay = appointment.date.toString().split('-')[2].toInt()

        datePicker.maxDate = appointment.date?.time ?: calendar.timeInMillis

        datePicker.init(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ) { _, year, monthOfYear, dayOfMonth ->
            val isAppointmentDate =
                year == appointmentYear && monthOfYear == appointmentMonth && dayOfMonth == appointmentDay

            if (isAppointmentDate) {
                hourPicker.maxValue = maxHour
                minutePicker.maxValue = maxMinute
            } else if (year == calendar.get(Calendar.YEAR) && monthOfYear == calendar.get(Calendar.MONTH) && dayOfMonth == calendar.get(
                    Calendar.DAY_OF_MONTH
                )
            ) {
                hourPicker.minValue = calendar.get(Calendar.HOUR_OF_DAY)
                hourPicker.maxValue = 23
                minutePicker.minValue = calendar.get(Calendar.MINUTE)
                minutePicker.maxValue = 59
            } else {
                hourPicker.minValue = 0
                hourPicker.maxValue = 23
                minutePicker.minValue = 0
                minutePicker.maxValue = 59
            }
        }
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Set") { dialog, _ ->
                val selectedYear = datePicker.year
                val selectedMonth = datePicker.month
                val selectedDay = datePicker.dayOfMonth
                val selectedHour = hourPicker.value
                val selectedMinute = minutePicker.value

                val selectedDateTime = Calendar.getInstance()
                selectedDateTime.set(
                    selectedYear,
                    selectedMonth,
                    selectedDay,
                    selectedHour,
                    selectedMinute
                )

                val alarmScheduler = AndroidAlarmScheduler(requireContext())
                val alarm = Alarm(
                    LocalDateTime.of(
                        selectedYear,
                        selectedMonth + 1,
                        selectedDay,
                        selectedHour,
                        selectedMinute
                    ), appointment.date.toString()
                )
                alarmScheduler.schedule(alarm)

                dialog.dismiss()
                Toast.makeText(requireContext(), "Reminder set", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }
}