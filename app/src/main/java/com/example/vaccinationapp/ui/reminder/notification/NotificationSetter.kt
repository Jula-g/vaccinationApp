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

/**
 * NotificationSetter class is a class that sets notifications for the appointments
 * @param context The context in which the notifications are set
 */
class NotificationSetter(private val context: Context) {

    private val notificationHelper = NotificationHelper(context)

    /**
     * setNotifications method is used to set notifications for the appointments
     */
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

    /**
     * scheduleNotification method is used to schedule a notification for the appointment
     * @param appointment The appointment for which the notification is to be scheduled
     */
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

    /**
     * calculateTriggerTime method is used to calculate the time at which the notification should be triggered
     * @param appointment The appointment for which the trigger time is to be calculated
     * @return The time at which the notification should be triggered
     */
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

    /**
     * getAllAppointments method is used to get all the appointments from the database
     * @return The set of appointments
     */
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
