package com.example.note.ui.main

import android.content.Intent
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.example.note.MainActivity
import com.example.note.MyApplication
import com.example.note.R
import com.example.note.auth.AuthPrefs
import com.example.note.base.BaseFragment
import com.example.note.databinding.FragmentChangePwdBinding
import com.example.note.ui.user.LoginActivity
import com.example.note.utils.toastCover

class ChangePwdFragment : BaseFragment<ChangePwdViewModel, FragmentChangePwdBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_change_pwd

    override fun initViewModel(): ChangePwdViewModel =
        ViewModelProvider(this)[ChangePwdViewModel::class.java]

    private var oldVisible = false
    private var newVisible = false
    private var confirmVisible = false

    override fun initView() {
        dataBinding.btnUpdatePwd.setOnClickListener {
            val oldPwd = dataBinding.etOldPwd.text.toString().trim()
            val newPwd = dataBinding.etNewPwd.text.toString().trim()
            val confirm = dataBinding.etConfirmPwd.text.toString().trim()

            if (oldPwd.isEmpty() || newPwd.isEmpty() || confirm.isEmpty()) {
                "密码不能为空".toastCover()
                return@setOnClickListener
            }
            if (newPwd != confirm) {
                "两次新密码不一致".toastCover()
                return@setOnClickListener
            }

            viewModel.updatePassword(viewLifecycleOwner, oldPwd, newPwd, confirm)
        }

        dataBinding.ivOldPwdToggle.setOnClickListener {
            togglePasswordVisibility(dataBinding.etOldPwd, dataBinding.ivOldPwdToggle, oldVisible)
            oldVisible = !oldVisible
        }
        dataBinding.ivNewPwdToggle.setOnClickListener {
            togglePasswordVisibility(dataBinding.etNewPwd, dataBinding.ivNewPwdToggle, newVisible)
            newVisible = !newVisible
        }
        dataBinding.ivConfirmPwdToggle.setOnClickListener {
            togglePasswordVisibility(
                dataBinding.etConfirmPwd,
                dataBinding.ivConfirmPwdToggle,
                confirmVisible
            )
            confirmVisible = !confirmVisible
        }
    }

    override fun initData() {
        viewModel.updatePwdResult.observe(viewLifecycleOwner) { result ->
            if (result == null) return@observe
            result.message.toastCover()
            if (result.code == 200) {
                // 修改密码成功后也退出登录
                AuthPrefs.setLoggedIn(requireContext(), false)
                MyApplication.cookieJar.clear()
                // 调用后端 logout 接口（可选，为保持一致性）
                MyApplication.apiService.logout().observe(viewLifecycleOwner) {
                    // 不强依赖结果
                }
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setBottomBarVisible(false)
    }

    private fun togglePasswordVisibility(
        editText: EditText,
        imageView: ImageView,
        isVisible: Boolean
    ) {
        if (isVisible) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            imageView.setImageResource(R.drawable.ic_eye)
        } else {
            editText.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            imageView.setImageResource(R.drawable.ic_eye_off)
        }
        editText.setSelection(editText.text?.length ?: 0)
    }
}

