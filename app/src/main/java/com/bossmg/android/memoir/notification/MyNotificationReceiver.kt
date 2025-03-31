package com.bossmg.android.memoir.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bossmg.android.memoir.ui.main.MainActivity

class MyNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("message") ?: "null..."
        showNotification(context, message)
    }

    private fun showNotification(context: Context, message: String){
        val channelId = "memoir_channel"
        val channelName = "My Memoir"
        val notificationId = if(message == "오늘 하루는 어땠나요? :)") 1002 else 1001

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder: NotificationCompat.Builder

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily Memo Reminder"
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)

            notificationBuilder = NotificationCompat.Builder(context, channelId)
        } else {
            notificationBuilder = NotificationCompat.Builder(context)
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        notificationBuilder.apply {
            setSmallIcon(android.R.drawable.ic_notification_overlay)
            setContentTitle(message)
            setContentText("새로운 항목을 작성하거나 기존 항목을 확인해보세요.")
            setAutoCancel(true)
            setContentIntent(pendingIntent)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}