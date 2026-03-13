package com.example.note.ui.main

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.note.MainActivity

class BillFragment : Fragment() {
    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return TextView(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            gravity = Gravity.CENTER
            text = "记账"
            textSize = 20f
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomBarVisible(true)
    }
}

