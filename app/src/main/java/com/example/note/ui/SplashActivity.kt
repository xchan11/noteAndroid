package com.example.note.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.note.auth.AuthPrefs
import com.example.note.ui.user.LoginActivity
import com.example.note.MainActivity

/**
 * 启动页：根据本地登录标记简单分流到 Login 或 Main。
 * 如需“自动登录”检查，可在 decideNext() 里增加网络校验逻辑。
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

