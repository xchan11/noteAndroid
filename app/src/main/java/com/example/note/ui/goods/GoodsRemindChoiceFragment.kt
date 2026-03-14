package com.example.note.ui.goods

import com.example.note.MainActivity
import com.example.note.R
import com.example.note.base.BaseFragment
import com.example.note.databinding.FragmentGoodsRemindChoiceBinding

class GoodsRemindChoiceFragment :
    BaseFragment<GoodsListViewModel, FragmentGoodsRemindChoiceBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_goods_remind_choice

    override fun initViewModel(): GoodsListViewModel =
        androidx.lifecycle.ViewModelProvider(this)[GoodsListViewModel::class.java]

    override fun initView() {
        setupToolbar("临期提醒")

        dataBinding.tvRemind1.setOnClickListener {
            openRemindList(1, "临期1天")
        }
        dataBinding.tvRemind3.setOnClickListener {
            openRemindList(3, "临期3天")
        }
        dataBinding.tvRemind7.setOnClickListener {
            openRemindList(7, "临期7天")
        }
    }

    override fun initData() {
        // no-op
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomBarVisible(false)
    }

    private fun openRemindList(type: Int, title: String) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fl_fragment_container,
                GoodsListFragment.newForRemind(type, title)
            )
            .addToBackStack(null)
            .commit()
    }

    companion object {
        fun newInstance() = GoodsRemindChoiceFragment()
    }
}

