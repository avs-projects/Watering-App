package com.example.kotlinproject.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.example.kotlinproject.AlarmActivity
import com.example.kotlinproject.R
import com.example.kotlinproject.data.Plant
import com.example.kotlinproject.data.PlantRoomDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**

NotificationService sends notification after consulting the database

 */

@SuppressLint("UnspecifiedImmutableFlag")
@ExperimentalCoroutinesApi

class NotificationService : Service() {
    private val channelId = "Notification Alarm Plants"
    private lateinit var notification: Notification
    private lateinit var notificationManager: NotificationManager

    private var checkBd = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()

        /*
        Using a thread to prevent the program from crashing
        when consulting the database
        Error: Cannot access database on the main thread since it may potentially lock the UI for a long period of time
        */
        Thread {
            val allPlants: List<Plant> =
                PlantRoomDatabase.getDatabase(this).plantDao().getAnyPlants()

            checkBd = allPlants.isNotEmpty()
        }.start()

        if (checkBd) {
            val title = "Your Plants need water"
            val message =
                "The life of your plants depends on it. One click will take you to the list of plants to water"

            val notificationIntent = Intent(this, AlarmActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent, 0
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notification = Notification.Builder(this, channelId)
                    .setContentTitle(title) // Title of notification
                    .setContentText(message) // Message to print
                    .setContentIntent(pendingIntent) // When click in notification
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build()
            }

            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1, notification)
        }

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}