package com.example.note.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {
    private val sdfYmdHm = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    private val sdfYmd = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val sdfYearMonth = SimpleDateFormat("yyyy-MM", Locale.getDefault())

    /** 格式化为 yyyy-MM-dd HH:mm（原有日程用法） */
    fun formatYmdHm(ts: Long): String = sdfYmdHm.format(Date(ts))

    /** 格式化为 yyyy-MM-dd HH:mm（记账接口 createTime、listByTime 用） */
    fun formatYmdHms(ts: Long): String = sdfYmdHm.format(Date(ts))

    /** 当前年月 yyyy-MM（预算、图表用） */
    fun getCurrentYearMonth(): String = sdfYearMonth.format(Date())

    /** 格式化为 yyyy-MM-dd（物品保质期/开封日期用） */
    fun formatYmd(ts: Long): String = sdfYmd.format(Date(ts))

    /** 解析 yyyy-MM-dd 字符串为时间戳（失败返回 null） */
    fun parseYmd(text: String): Long? = try {
        sdfYmd.parse(text)?.time
    } catch (e: Exception) {
        null
    }

    /** 判断给定时间是否严格大于当前时间（用于校验保质期） */
    fun isFuture(ts: Long): Boolean = ts > System.currentTimeMillis()
}

