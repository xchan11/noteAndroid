package com.example.note.ui.main

import android.content.Intent
import androidx.lifecycle.ViewModelProvider
import com.example.note.MyApplication
import com.example.note.R
import com.example.note.auth.AuthPrefs
import com.example.note.base.BaseFragment
import com.example.note.databinding.FragmentUserBinding
import com.example.note.ui.user.LoginActivity
import com.example.note.utils.toastCover

/**
 * 用户“我的”页面：显示用户名、手机号，提供修改信息、修改密码、退出登录、注销账号四个入口。
 */
class UserFragment : BaseFragment<UserViewModel, FragmentUserBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_user

    override fun initViewModel(): UserViewModel =
        ViewModelProvider(this)[UserViewModel::class.java]

    override fun initView() {
        dataBinding.itemEditInfo.setTitle("修改信息")
        dataBinding.itemChangePassword.setTitle("修改密码")
        dataBinding.itemLogout.setTitle("退出登录")
        dataBinding.itemCancelAccount.setTitle("注销账号")

        dataBinding.itemEditInfo.setOnClickListener {
            "TODO: 跳转到修改信息页面".toastCover()
        }
        dataBinding.itemChangePassword.setOnClickListener {
            "TODO: 跳转到修改密码页面".toastCover()
        }
        dataBinding.itemLogout.setOnClickListener {
            handleLogout()
        }
        dataBinding.itemCancelAccount.setOnClickListener {
            "TODO: 调用 /user/cancel 注销账号".toastCover()
        }
    }

    override fun initData() {
        viewModel.userInfo.observe(viewLifecycleOwner) { info ->
            if (info != null) {
                dataBinding.tvUserNameValue.text = info.username ?: ""
                dataBinding.tvPhoneValue.text = info.phone ?: ""
            }
        }
        viewModel.loadUserInfo()
    }

    private fun handleLogout() {
        AuthPrefs.setLoggedIn(requireContext(), false)
        MyApplication.cookieJar.clear()
        "已退出登录".toastCover()
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }
}

