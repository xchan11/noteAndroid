package com.example.note.auth

import android.content.Context
import android.content.SharedPreferences

/**
 * 简单的登录状态持久化工具。
 * 只关心“是否已登录”这个布尔值，由业务在登录 / 退出时显式设置。
 */
object AuthPrefs {

    private const val PREF_NAME = "auth_prefs"
    private const val KEY_LOGGED_IN = "logged_in"

    private fun prefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setLoggedIn(context: Context, loggedIn: Boolean) {
        prefs(context).edit().putBoolean(KEY_LOGGED_IN, loggedIn).apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_LOGGED_IN, false)
    }
}

