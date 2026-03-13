package com.example.note

import android.view.View
import androidx.fragment.app.Fragment
import com.example.note.base.BaseNoViewModelActivity
import com.example.note.databinding.ActivityMainBinding
import com.example.note.ui.main.BillFragment
import com.example.note.ui.main.GoodsFragment
import com.example.note.ui.main.NoteFragment
import com.example.note.ui.main.UserFragment

class MainActivity : BaseNoViewModelActivity<ActivityMainBinding>() {

    private var mLastFragment: Fragment? = null
    private val fragmentList = mutableListOf<Fragment>()

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView() {
        initBottomNavigationView()
        switchFragment(0)
        dataBinding.bnv.setSelectedTab(0)
    }

    override fun initData() {
        // no-op
    }

    private fun initBottomNavigationView() {
        dataBinding.bnv.addTab(
            R.drawable.ic_tab_note,
            R.drawable.ic_tab_note_sel,
            "记事"
        )
        fragmentList.add(NoteFragment())

        dataBinding.bnv.addTab(
            R.drawable.ic_tab_bill,
            R.drawable.ic_tab_bill_sel,
            "记账"
        )
        fragmentList.add(BillFragment())

        dataBinding.bnv.addTab(
            R.drawable.ic_tab_goods,
            R.drawable.ic_tab_goods_sel,
            "物品"
        )
        fragmentList.add(GoodsFragment())

        dataBinding.bnv.addTab(
            R.drawable.ic_tab_user,
            R.drawable.ic_tab_user_sel,
            "我的"
        )
        fragmentList.add(UserFragment())

        dataBinding.bnv.onTabSelectedListener = {
            switchFragment(it)
        }
    }

    /** 仅四个主 Tab 页面显示底部栏，其它 Fragment 隐藏 */
    fun setBottomBarVisible(visible: Boolean) {
        val v = if (visible) View.VISIBLE else View.GONE
        dataBinding.bnv.visibility = v
        // 分割线一起控制
        dataBinding.view6.visibility = v
    }

    private fun getFragment(fragmentIndex: Int): Fragment = fragmentList[fragmentIndex]

    private fun switchFragment(fragmentIndex: Int) {
        handler.post {
            val transaction = supportFragmentManager.beginTransaction()
            mLastFragment?.let { transaction.hide(it) }
            val fragment = getFragment(fragmentIndex)
            if (!fragment.isAdded) {
                transaction.add(R.id.fl_fragment_container, fragment, "fragment_$fragmentIndex")
            } else {
                transaction.show(fragment)
            }
            mLastFragment = fragment
            transaction.commitAllowingStateLoss()
        }
    }
}