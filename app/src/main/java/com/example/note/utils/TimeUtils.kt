package com.example.note.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    fun formatYmdHm(ts: Long): String = sdf.format(Date(ts))
}

