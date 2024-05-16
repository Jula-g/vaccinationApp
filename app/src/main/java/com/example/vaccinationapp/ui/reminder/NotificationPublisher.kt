package com.example.vaccinationapp.ui.reminder

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.vaccinationapp.R
import com.example.vaccinationapp.channelId

/**
 * Notification publisher is a broadcast receiver that listens for notifications
 *
 * @constructor Create empty Notification publisher
 */
class NotificationPublisher : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.FOREGROUND_SERVICE) == PackageManager.PERMISSION_GRANTED) {
            showNotification(context, intent)
        } else {
            val notificationIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            notificationIntent.data = Uri.parse("package:${context.packageName}")
            context.startActivity(notificationIntent)
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
    private fun getRemoteView(context: Context, title: String, message: String): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, R.layout.notification)
        remoteViews.setTextViewText(R.id.title_notification, title)
        remoteViews.setTextViewText(R.id.description, message)
        return remoteViews
    }

    /**
     * Show notification displays the notification
     *
     * @param context
     * @param intent
     */
    private fun showNotification(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title")
        val message = intent.getStringExtra("message")

        var notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.syringe_logo_with_name)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationBuilder = notificationBuilder.setContent(getRemoteView(context, title!!, message!!))

        with(NotificationManagerCompat.from(context)) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.FOREGROUND_SERVICE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            notify(0, notificationBuilder.build())
        }
    }
}
