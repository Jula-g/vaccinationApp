package com.example.vaccinationapp

import com.example.vaccinationapp.ui.reminder.NotificationPublisher
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.vaccinationapp.ui.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Channel id
 */
const val channelId = "notification_channel"

/**
 * Channel name
 */
const val channelName = "com.example.vaccinationapp"


/**
 * Firebase service is a service that listens for notifications
 *
 * @constructor Create empty Firebase service
 */
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseService : FirebaseMessagingService() {

    /**
     * On message received is called when a message is received
     *
     * @param remoteMessage
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.notification != null) {
            generateNotification(
                applicationContext,
                remoteMessage.notification!!.title!!,
                remoteMessage.notification!!.body!!
            )
        }
    }

    companion object {

        /**
         * Schedule notification schedules a notification
         *
         * @param context
         * @param timeInMillis
         * @param title
         * @param message
         */
        fun scheduleNotification(
            context: Context,
            timeInMillis: Long,
            title: String,
            message: String
        ) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.SET_ALARM
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val intent = Intent(context, NotificationPublisher::class.java)
                intent.putExtra("title", title)
                intent.putExtra("message", message)

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                val alarmManager =
                    context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    timeInMillis,
                    pendingIntent
                )
            } else {
                AlertDialog.Builder(context)
                    .setTitle("Permission Required")
                    .setMessage("The app needs permission to set alarms for reminders. Please grant the permission in the app settings.")
                    .setPositiveButton("Go to Settings") { dialog, _ ->
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", context.packageName, null)
                        intent.data = uri
                        context.startActivity(intent)
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    /**
     * Get remote view creates a remote view for the notification
     *
     * @param context
     * @param title
     * @param message
     * @return
     */

    @SuppressLint("RemoteViewLayout")
    fun getRemoteView(context: Context, title: String, message: String): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.notification)
        remoteViews.setTextViewText(R.id.title_notification, title)
        remoteViews.setTextViewText(R.id.description, message)
        return remoteViews
    }


    /**
     * Generate notification generates a notification
     *
     * @param context
     * @param title
     * @param message
     */
    fun generateNotification(context: Context, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        var builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.syringe_logo_with_name)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(context, title, message))

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        notificationManager.notify(0, builder.build())

    }
}
