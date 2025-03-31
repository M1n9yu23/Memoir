package com.bossmg.android.memoir.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

// 오전 12시 requestCode = 1001
// 오후 10시 requestCode = 1002

object NotificationScheduler {

    fun scheduleMorningNotification(context: Context) {
        scheduleNotificationAtTime(context, 12, 0, 1001, "오늘 하루 힘내요!")
    }

    fun scheduleNightNotification(context: Context) {
        scheduleNotificationAtTime(context, 22, 0, 1002, "오늘 하루는 어땠나요? :)")
    }

    private fun scheduleNotificationAtTime(
        context: Context,
        hour: Int,
        minute: Int,
        requestCode: Int,
        message: String
    ) {
        // 특정 시간에 알림 요청하기 위함.
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


        val intent = Intent(context, MyNotificationReceiver::class.java).apply {
            putExtra("message", message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1) // 현재 시간보다 이전이면 다음날로 설정
            }
        }

        // 메인액티비티에서 권한 체크를 했는데 빨간 줄이 떠서 try로 묶음
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}