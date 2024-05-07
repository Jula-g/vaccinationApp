package com.example.vaccinationapp.ui.reminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.NumberPicker
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vaccinationapp.DB.DBconnection
import com.example.vaccinationapp.DB.entities.Appointments
import com.example.vaccinationapp.DB.queries.AppointmentsQueries
import com.example.vaccinationapp.R
import com.example.vaccinationapp.adapters.AppointmentAdapter
import com.example.vaccinationapp.databinding.FragmentReminderBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.Calendar

class ReminderFragment : Fragment() {

    private var _binding: FragmentReminderBinding? = null
    private val binding get() = _binding!!

    private var requestCode = 123

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReminderBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = root.findViewById(R.id.recycleAppointment)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadAppointment(recyclerView) { appointment ->
            showDateTimePickerDialog(appointment)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadAppointment(recyclerView: RecyclerView, onItemClick: (Appointments) -> Unit) {
        val scope = CoroutineScope(Dispatchers.IO)

        scope.launch {
            val connection = DBconnection.getConnection()
            val ap = AppointmentsQueries(connection)
            val appointmentsList = ap.getAllAppointments()?.toList() ?: emptyList()

            withContext(Dispatchers.Main) {
                val adapter = AppointmentAdapter(appointmentsList, onItemClick)
                recyclerView.adapter = adapter
                connection.close()
            }
        }
    }

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
        ) { view, year, monthOfYear, dayOfMonth ->
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

                setReminder(requireContext(), selectedDateTime.timeInMillis, appointment)

                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }


    @SuppressLint("ScheduleExactAlarm")
    private fun setReminder(context: Context, dateTimeMillis: Long, appointment: Appointments) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("dateTimeMillis", dateTimeMillis)
            putExtra("appointment_date", appointment.date)
            putExtra("appointment_time", appointment.time)
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.SCHEDULE_EXACT_ALARM
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.SCHEDULE_EXACT_ALARM),
                requestCode
            )
        } else {
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                timeInMillis = dateTimeMillis
            }

            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }
}