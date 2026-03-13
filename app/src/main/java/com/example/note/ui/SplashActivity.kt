package com.example.note.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.note.MainActivity
import com.example.note.auth.AuthPrefs
import com.example.note.ui.user.LoginActivity

/**
 * 启动页：只根据本地登录标记决定跳 Login 还是 Main。
 * Session / Cookie 是否过期，由各业务接口在返回 401 时单独处理。
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        decideNext()
    }

    private fun decideNext() {
        val next = if (AuthPrefs.isLoggedIn(this)) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }
        startActivity(next)
        finish()
    }
}

