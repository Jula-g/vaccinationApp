package com.example.vaccinationapp.ui.reminder.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.vaccinationapp.DB.DBconnection
import com.example.vaccinationapp.DB.entities.Appointments
import com.example.vaccinationapp.DB.queries.AppointmentsQueries
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationSetter(private val context: Context) {

    private val notificationHelper = NotificationHelper(context)

    fun setNotifications() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val appointments = getAllAppointments() ?: return@launch

                notificationHelper.createNotificationChannel()

                appointments.forEach { appointment ->
                    scheduleNotification(appointment)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun scheduleNotification(appointment: Appointments) {
        val triggerTime = calculateTriggerTime(appointment)

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("appointment_date", appointment.date)
            putExtra("appointment_time", appointment.time)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            appointment.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    private fun calculateTriggerTime(appointment: Appointments): Long {
        val appointmentTime = appointment.time.toString()
        val appointmentHour = appointmentTime.split(":")[0].toInt()
        val appointmentMinute = appointmentTime.split(":")[1].toInt()

        val triggerHour = appointmentHour - 1
        val finalTriggerHour = if (triggerHour < 0) triggerHour + 24 else triggerHour

        val triggerHourMillis = finalTriggerHour * 60 * 60 * 1000
        val triggerMinuteMillis = appointmentMinute * 60 * 1000

        return System.currentTimeMillis() + triggerHourMillis + triggerMinuteMillis
    }

    private suspend fun getAllAppointments(): Set<Appointments>? {
        return withContext(Dispatchers.IO) {
            val connection = DBconnection.getConnection()
            val appQueries = AppointmentsQueries(connection)
            val appointments = appQueries.getAllAppointments()
            connection.close()
            appointments
        }
    }
}
