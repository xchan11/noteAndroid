package com.example.note.ui.main

import android.app.AlertDialog
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.note.MainActivity
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
            val name = dataBinding.tvUserNameValue.text.toString()
            val phone = dataBinding.tvPhoneValue.text.toString()
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fl_fragment_container,
                    EditInfoFragment.newInstance(name, phone)
                )
                .addToBackStack(null)
                .commit()
        }
        dataBinding.itemChangePassword.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fl_fragment_container, ChangePwdFragment())
                .addToBackStack(null)
                .commit()
        }
        dataBinding.itemLogout.setOnClickListener {
            handleLogout()
        }
        dataBinding.itemCancelAccount.setOnClickListener {
            handleCancelAccount()
        }

        dataBinding.btLoginOut.setOnClickListener {
            handleLogout()
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

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomBarVisible(true)
    }

    private fun handleLogout() {
        AlertDialog.Builder(requireContext())
            .setMessage("是否退出账号？")
            .setPositiveButton("确定") { _, _ ->
                viewModel.requestLogout(viewLifecycleOwner)
                viewModel.logoutResult.observe(viewLifecycleOwner) { result ->
                    if (result == null) return@observe
                    result.message.toastCover()
                    if (result.code == 200) {
                        AuthPrefs.setLoggedIn(requireContext(), false)
                        MyApplication.cookieJar.clear()
                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                        requireActivity().finish()
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun handleCancelAccount() {
        AlertDialog.Builder(requireContext())
            .setMessage("是否注销账号？")
            .setPositiveButton("确定") { _, _ ->
                viewModel.requestCancel(viewLifecycleOwner)
                viewModel.cancelResult.observe(viewLifecycleOwner) { result ->
                    if (result == null) return@observe
                    result.message.toastCover()
                    if (result.code == 200) {
                        AuthPrefs.setLoggedIn(requireContext(), false)
                        MyApplication.cookieJar.clear()
                        startActivity(Intent(requireContext(), LoginActivity::class.java))
                        requireActivity().finish()
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
}

