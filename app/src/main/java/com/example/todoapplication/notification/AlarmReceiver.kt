package com.example.todoapplication.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("title") ?: "할 일 알림"
        val todoId = intent?.getIntExtra("todoId", -1) ?: -1

        NotificationHelper.showNotification(context, title, todoId)
    }
}