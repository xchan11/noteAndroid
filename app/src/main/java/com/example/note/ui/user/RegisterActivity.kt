package com.example.note.ui.user

import android.content.Intent
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.example.note.R
import com.example.note.base.BaseActivity
import com.example.note.databinding.ActivityRegisterBinding
import com.example.note.utils.toastCover
import com.example.note.viewmodel.RegisterViewModel

class RegisterActivity : BaseActivity<RegisterViewModel, ActivityRegisterBinding>() {

    private var isPwdVisible = false
    private var isConfirmPwdVisible = false

    override fun getLayoutId(): Int = R.layout.activity_register

    override fun initViewModel(): RegisterViewModel =
        ViewModelProvider(this)[RegisterViewModel::class.java]

    override fun initView() {
        dataBinding.ivPwdToggle.setOnClickListener {
            togglePasswordVisibility(dataBinding.etPassword, dataBinding.ivPwdToggle, isPwdVisible)
            isPwdVisible = !isPwdVisible
        }
        dataBinding.ivConfirmPwdToggle.setOnClickListener {
            togglePasswordVisibility(
                dataBinding.etConfirmPassword,
                dataBinding.ivConfirmPwdToggle,
                isConfirmPwdVisible
            )
            isConfirmPwdVisible = !isConfirmPwdVisible
        }

        dataBinding.btnRegister.setOnClickListener {
            val username = dataBinding.etUsername.text.toString().trim()
            val phone = dataBinding.etPhone.text.toString().trim()
            val pwd = dataBinding.etPassword.text.toString().trim()
            val confirm = dataBinding.etConfirmPassword.text.toString().trim()
            if (!preCheck(username, phone, pwd, confirm)) return@setOnClickListener
            viewModel.register(this, username, phone, pwd, confirm)
        }
        dataBinding.btnBack.setOnClickListener { finish() }
    }

    override fun initData() {
        viewModel.registerResult.observe(this) { result ->
            if (result == null) return@observe
            result.message.toastCover()
            if (result.code == 200) {
                // 注册成功，回到登录页
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun preCheck(username: String, phone: String, pwd: String, confirm: String): Boolean {
        if (username.isEmpty() || phone.isEmpty() || pwd.isEmpty() || confirm.isEmpty()) {
            "用户名/手机号/密码不能为空".toastCover()
            return false
        }
        val phoneRegex = Regex("^1[3-9]\\d{9}$")
        if (!phoneRegex.matches(phone)) {
            "手机号格式不正确（需为11位有效手机号）".toastCover()
            return false
        }
        if (pwd != confirm) {
            "两次密码输入不一致".toastCover()
            return false
        }
        return true
    }

    private fun togglePasswordVisibility(
        editText: EditText,
        imageView: ImageView,
        isVisible: Boolean
    ) {
        if (isVisible) {
            editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            imageView.setImageResource(android.R.drawable.ic_menu_view)
        } else {
            editText.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            imageView.setImageResource(android.R.drawable.ic_menu_view)
        }
        editText.setSelection(editText.text?.length ?: 0)
    }
}

