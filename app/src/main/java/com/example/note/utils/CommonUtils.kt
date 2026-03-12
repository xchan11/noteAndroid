package com.example.note.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.note.BuildConfig
import com.example.note.model.RequestType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** ---------- 日志 & Toast ---------- */

fun String?.logD(tag: String = "CommonUtils"): String {
    if (BuildConfig.DEBUG) {
        Log.d(tag, this ?: "null")
    }
    return this ?: ""
}

fun String?.toast(context: Context): String {
    Toast.makeText(context, this ?: "null", Toast.LENGTH_SHORT).show()
    return this ?: ""
}

fun String?.toastLong(context: Context): String {
    Toast.makeText(context, this ?: "null", Toast.LENGTH_LONG).show()
    return this ?: ""
}

/** ---------- View 显示/隐藏 ---------- */

fun <T : View> T?.show(): T? {
    this?.visibility = View.VISIBLE
    return this
}

fun <T : View> T?.hide(): T? {
    this?.visibility = View.GONE
    return this
}

fun <T : View> T?.invisible(): T? {
    this?.visibility = View.INVISIBLE
    return this
}

fun <T : View> T?.toShow(show: Boolean): T? {
    this?.visibility = if (show) View.VISIBLE else View.GONE
    return this
}

/** ---------- dp/px 工具 ---------- */

fun Int.dp(context: Context): Int {
    val density = context.resources.displayMetrics.density
    return (this * density + 0.5f).toInt()
}

/** ---------- 时间格式工具（时间戳转字符串） ---------- */

fun Long.toDateString(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    val date = Date(this)
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(date)
}

/** ---------- ImageView 简单加载 ---------- */

fun ImageView.loadUrl(url: String?, @DrawableRes placeholder: Int? = null) {
    val ctx = context ?: return
    if (ctx is FragmentActivity && (ctx.isFinishing || ctx.isDestroyed)) return
    if (url.isNullOrEmpty()) {
        placeholder?.let { setImageResource(it) }
        return
    }
    val request = Glide.with(ctx).load(url)
    placeholder?.let { request.placeholder(it).error(it) }
    request.into(this)
}

fun ImageView.loadRounded(url: String?, radiusDp: Int, @DrawableRes placeholder: Int? = null) {
    val ctx = context ?: return
    if (ctx is FragmentActivity && (ctx.isFinishing || ctx.isDestroyed)) return
    if (url.isNullOrEmpty()) {
        placeholder?.let { setImageResource(it) }
        return
    }
    val radiusPx = radiusDp.dp(ctx)
    val options = RequestOptions().transform(CenterCrop(), RoundedCorners(radiusPx))
    val request = Glide.with(ctx).load(url).apply(options)
    placeholder?.let { request.placeholder(it).error(it) }
    request.into(this)
}

/** ---------- 网络返回通用判断 ---------- */

/**
 * 配合形如：data class RequestType<T>(val code:Int, val message:String, val data:T?)
 * code == 0 表示成功，其它为失败（你可以按后端约定修改）。
 */
fun <T> RequestType<T>?.isOk(showToast: Boolean = true, context: Context? = null): Boolean {
    if (this == null) {
        if (showToast && context != null) "系统异常".toast(context)
        return false
    }
    return if (code == 0) {
        true
    } else {
        if (showToast && context != null) (message ?: "请求失败").toast(context)
        false
    }
}