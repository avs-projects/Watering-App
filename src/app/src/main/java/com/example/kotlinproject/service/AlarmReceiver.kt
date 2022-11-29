package com.example.kotlinproject.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**

AlarmReceiver which launches the notificationService

 */

@ExperimentalCoroutinesApi
@SuppressLint("UnsafeProtectedBroadcastReceiver")
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val service = Intent(context, NotificationService::class.java)
        context.startService(service)
    }
}