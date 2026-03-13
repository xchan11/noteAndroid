package com.example.note

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * 接收 AlarmManager 唤醒，弹出系统通知提醒日程。
 */
class NoteReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getIntExtra(EXTRA_NOTE_ID, 0)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "日程提醒"
        val content = intent.getStringExtra(EXTRA_CONTENT) ?: ""

        val channelId = "note_reminder_channel"
        val nm = NotificationManagerCompat.from(context)

        // Android 8+ 需要通知渠道
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (mgr.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(
                    channelId,
                    "日程提醒",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "日程到时间的通知提醒"
                    enableLights(true)
                    lightColor = Color.BLUE
                }
                mgr.createNotificationChannel(channel)
            }
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content.ifBlank { "时间到了，看看你的日程" })
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        nm.notify(noteId, notification)
    }

    companion object {
        const val EXTRA_NOTE_ID = "extra_note_id"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_CONTENT = "extra_content"
    }
}

