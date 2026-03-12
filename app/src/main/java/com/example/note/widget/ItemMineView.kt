package com.example.note.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import com.example.note.R

class ItemMineView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val tvTitle: TextView

    init {
        orientation = HORIZONTAL
        LayoutInflater.from(context).inflate(R.layout.item_me_view, this, true)
        tvTitle = findViewById(R.id.tvMineItem)
    }

    /** 设置左侧文字 */
    fun setTitle(text: CharSequence?) {
        tvTitle.text = text ?: ""
    }
}