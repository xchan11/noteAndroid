package com.example.note.api

import android.util.Log

object LogPrinter {

    private const val TAG = "NetInfoLog"

    /**
     * 给 HttpLoggingInterceptor 用的打印方法
     */
    @JvmStatic
    fun printNetLog(message: String) {
        // 简单版：直接按行打印
        if (message.length <= 4000) {
            Log.d(TAG, message)
        } else {
            // 长日志分段打印，避免被截断
            var start = 0
            val length = message.length
            while (start < length) {
                val end = (start + 4000).coerceAtMost(length)
                Log.d(TAG, message.substring(start, end))
                start = end
            }
        }
    }
}