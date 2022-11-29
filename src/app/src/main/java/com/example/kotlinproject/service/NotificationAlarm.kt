package com.example.kotlinproject.service

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

/**

Notification Alarm allows you to set up the daily alarm

 */

@ExperimentalCoroutinesApi
@SuppressLint("UnspecifiedImmutableFlag")

class NotificationAlarm {

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setNotification(activity: Activity) {

        // Definition of the alarm manager who calls the alarm receiver
        val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(
            activity.applicationContext,
            AlarmReceiver::class.java
        )

        // Definition of the alarm call time in this case 1 a.m.
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 1)
            set(Calendar.MINUTE, 5)
            set(Calendar.SECOND, 5)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            activity,
            0,
            alarmIntent,
            0
        )

        /**
         * Using setExactAndAllowWhileIdle to counter the hold effect
         * device battery drain and start the service even if the app is closed

        alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
        )
         * */

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

    }
}