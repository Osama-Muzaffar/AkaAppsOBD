package com.akapps.obd2carscannerapp.Reciever

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.akapps.obd2carscannerapp.R

class NotificationHelper(private val context: Context) {
    private val channelId = "notification_channel_id"
    private val channelName = "Daily Notification Channel"

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun getNotificationBuilder(content: String): Notification.Builder {
        return Notification.Builder(context, channelId)
            .setContentTitle("Diagnose Alert")
            .setContentText(content)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setAutoCancel(true)
    }
}
