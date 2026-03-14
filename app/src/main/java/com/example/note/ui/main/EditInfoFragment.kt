package com.example.note.ui.main

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import com.example.note.MainActivity
import com.example.note.R
import com.example.note.base.BaseFragment
import com.example.note.databinding.FragmentEditInfoBinding
import com.example.note.utils.toastCover

class EditInfoFragment : BaseFragment<UserViewModel, FragmentEditInfoBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_edit_info

    override fun initViewModel(): UserViewModel =
        ViewModelProvider(this)[UserViewModel::class.java]

    override fun initView() {
        // 统一标题栏（基类封装）
        setupToolbar("修改基本信息")

        // 预填当前用户名和手机号
        val username = arguments?.getString(ARG_USERNAME).orEmpty()
        val phone = arguments?.getString(ARG_PHONE).orEmpty()
        dataBinding.etUsername.setText(username)
        dataBinding.etPhone.setText(phone)

        dataBinding.btnUpdateInfo.setOnClickListener {
            val newName = dataBinding.etUsername.text.toString().trim()
            val newPhone = dataBinding.etPhone.text.toString().trim()
            if (newName.isEmpty() || newPhone.isEmpty()) {
                "用户名/手机号不能为空".toastCover()
                return@setOnClickListener
            }
            val phoneRegex = Regex("^1[3-9]\\d{9}$")
            if (!phoneRegex.matches(newPhone)) {
                "手机号格式不正确（需为11位有效手机号）".toastCover()
                return@setOnClickListener
            }
            viewModel.updateInfo(viewLifecycleOwner, newName, newPhone)
        }
    }

    override fun initData() {
        viewModel.updateInfoResult.observe(viewLifecycleOwner) { result ->
            if (result == null) return@observe
            result.message.toastCover()
            if (result.code == 200) {
                parentFragmentManager.popBackStack()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomBarVisible(false)
    }

    companion object {
        private const val ARG_USERNAME = "username"
        private const val ARG_PHONE = "phone"

        fun newInstance(username: String, phone: String) = EditInfoFragment().apply {
            arguments = bundleOf(
                ARG_USERNAME to username,
                ARG_PHONE to phone
            )
        }
    }
}

