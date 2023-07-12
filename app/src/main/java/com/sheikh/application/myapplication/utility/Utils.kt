package com.sheikh.application.myapplication.utility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sheikh.application.myapplication.R
import com.sheikh.application.myapplication.model.Stream

val String.getStream: Stream
    get() {
        val iTag = Regex("itag=\\S+")
        val mimeType = Regex("mime_type=\\S+")
        val resolution = Regex("res=\\S+")
        val fps = Regex("fps=\\S+")
        val type = Regex("type=\\S+")

        return Stream(
            iTag = iTag.find(this)?.value?.let { it.split("itag=")[1] } ?: "",
            mimeType = mimeType.find(this)?.value?.let { it.split("mime_type=")[1] } ?: "",
            resolution = resolution.find(this)?.value?.let { it.split("res=")[1] } ?: "",
            fps = fps.find(this)?.value?.let { it.split("fps=")[1] } ?: "",
            type = type.find(this)?.value?.let { it.split("type=")[1] } ?: ""
        )
    }

val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
    "Verbose WorkManager Notifications"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
    "Shows notifications whenever work starts"
val NOTIFICATION_TITLE: CharSequence = "Youtube Downloader"
const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
const val NOTIFICATION_ID = 1


fun makeStatusNotification(message: String, context: Context) {

    // Make a channel if necessary
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        val name = VERBOSE_NOTIFICATION_CHANNEL_NAME
        val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description

        // Add the channel
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    // Create the notification
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(NOTIFICATION_TITLE)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(LongArray(0))

    // Show the notification
    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
}

