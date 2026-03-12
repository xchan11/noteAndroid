package com.example.note.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.view.setPadding
import com.example.note.R
import com.example.note.utils.dp

class BottomNavigationView : LinearLayout {

    var onTabSelectedListener: (Int)->Unit = {}
    private var selectedTabIndex = -1
    private val tabs: MutableList<Tab> = ArrayList()

    private class Tab(
        var view: View,
        var startDrawableId: Int,
        var endDrawableId: Int
    )

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        orientation = HORIZONTAL
    }

    fun addTab(@DrawableRes startDrawableId: Int, @DrawableRes endDrawableId: Int, text: String?) {
        val tabView = LayoutInflater.from(context).inflate(R.layout.tab_item, this, false)
        val fadingImageView = tabView.findViewById<ImageView>(R.id.tab_icon)
        fadingImageView.setImageDrawable(resources.getDrawable(startDrawableId))
        val textView = tabView.findViewById<TextView>(R.id.tab_text)
        textView.text = text
        val index = childCount
        tabView.setOnClickListener { v: View? ->
            setSelectedTab(index)
            onTabSelectedListener.invoke(index)
        }
        addView(tabView)
        tabs.add(Tab(tabView, startDrawableId, endDrawableId))
    }

    fun setSelectedTab(index: Int) {
        if (selectedTabIndex != index) {
            // 取消之前选中的Tab
            if (selectedTabIndex >= 0) {
                val previousTab = tabs[selectedTabIndex]
                (previousTab.view.findViewById<View>(R.id.tab_icon) as ImageView).run {
                    setImageDrawable(resources.getDrawable(previousTab.startDrawableId))
                    setPadding(3) //setPadding(3.dp())
                }
                (previousTab.view.findViewById<View>(R.id.tab_text) as TextView).run{
                    setTextColor(resources.getColor(R.color.main_green6))
                    textSize = 12f
                }
            }

            // 设置当前选中的Tab
            val currentlySelectedTab = tabs[index]
            (currentlySelectedTab.view.findViewById<View>(R.id.tab_icon) as ImageView).run{
                setImageDrawable(resources.getDrawable(currentlySelectedTab.endDrawableId))
                setPadding(0)
            }
            (currentlySelectedTab.view.findViewById<View>(R.id.tab_text) as TextView).run{
                setTextColor(resources.getColor(R.color.main_green7))
                textSize = 14f
            }
            selectedTabIndex = index
        }
    }
}