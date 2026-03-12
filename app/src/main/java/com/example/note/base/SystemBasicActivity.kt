package com.example.note.base

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity

/**
 * 最底层Activity，系统级别的设置放在这
 * */
open class SystemBasicActivity :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

    }
    override fun attachBaseContext(newBase: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // API 17+ 的处理方式
            super.attachBaseContext(createFixedContext(newBase))
        } else {
            // 旧版本API的处理方式
            super.attachBaseContext(newBase)
            applyLegacyFontScaleFix()
        }
    }

    private fun createFixedContext(context: Context): Context? {
        val override = Configuration()
        override.fontScale = 1.0f // 固定字体缩放比例
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // API 24+ 需要特殊处理
            context.createConfigurationContext(override)
        } else {
            // API 17-23 的处理方式
            context.resources.updateConfiguration(override, context.resources.displayMetrics)
            context
        }
    }

    @Suppress("deprecation")
    private fun applyLegacyFontScaleFix() {
        // 适用于API 16及以下的处理方式
        val res = resources
        val config: Configuration = res.configuration
        config.fontScale = 1.0f
        res.updateConfiguration(config, res.displayMetrics)
    }

}