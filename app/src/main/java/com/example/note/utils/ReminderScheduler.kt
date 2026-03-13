package com.example.note.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.note.NoteReminderReceiver
import com.example.note.model.Note

/**
 * 使用 AlarmManager + BroadcastReceiver 实现本地日程提醒。
 */
object ReminderScheduler {

    private fun pendingIntent(context: Context, noteId: Int): PendingIntent {
        val intent = Intent(context, NoteReminderReceiver::class.java).apply {
            putExtra(NoteReminderReceiver.EXTRA_NOTE_ID, noteId)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        return PendingIntent.getBroadcast(context, noteId, intent, flags)
    }

    fun schedule(context: Context, note: Note) {
        val rt = note.remindTime ?: return
        if (rt <= 0L) return

        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NoteReminderReceiver::class.java).apply {
            putExtra(NoteReminderReceiver.EXTRA_NOTE_ID, note.noteId)
            putExtra(NoteReminderReceiver.EXTRA_TITLE, note.title)
            putExtra(NoteReminderReceiver.EXTRA_CONTENT, note.content)
        }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val pi = PendingIntent.getBroadcast(context, note.noteId, intent, flags)

        try {
            // Android 12+ 需要精确闹钟权限，否则这里会抛 SecurityException
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (am.canScheduleExactAlarms()) {
                    am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, rt, pi)
                } else {
                    // 没有精确闹钟授权时，退而求其次用普通闹钟，至少尽量触发，不崩溃
                    am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, rt, pi)
                }
            } else {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, rt, pi)
            }
        } catch (e: SecurityException) {
            // 避免直接崩溃：如果还是因为权限问题，直接忽略提醒
            e.printStackTrace()
        }
    }

    fun cancel(context: Context, noteId: Int) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        am.cancel(pendingIntent(context, noteId))
    }
}

