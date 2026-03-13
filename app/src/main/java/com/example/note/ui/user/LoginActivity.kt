package com.example.note.ui.user

import android.content.Context
import android.content.Intent
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.example.note.MainActivity
import com.example.note.MyApplication
import com.example.note.R
import com.example.note.base.BaseActivity
import com.example.note.databinding.ActivityLoginBinding
import com.example.note.viewmodel.LoginViewModel

/**
 * 登录页，模仿参考项目写法：initView 里 cookieJar.clear、点击读输入框调 viewModel.login(lifecycle, phone, pwd)。
 */
class LoginActivity : BaseActivity<LoginViewModel, ActivityLoginBinding>() {

    private var isPwdVisible = false

    override fun getLayoutId(): Int = R.layout.activity_login

    override fun initViewModel(): LoginViewModel =
        ViewModelProvider(this)[LoginViewModel::class.java]

    override fun initView() {
        MyApplication.cookieJar.clear() // 登录前清除缓存
        dataBinding.btnLogin.setOnClickListener {
            val phone = dataBinding.etPhone.text.toString().trim()
            val password = dataBinding.etPassword.text.toString().trim()
            viewModel.login(this, phone, password)
        }

        dataBinding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        dataBinding.ivPwdToggle.setOnClickListener {
            togglePasswordVisibility(dataBinding.etPassword, dataBinding.ivPwdToggle, isPwdVisible)
            isPwdVisible = !isPwdVisible
        }
    }

    override fun initData() {
        viewModel.loginSuccess.observe(this) { user ->
            if (user != null) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
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

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            if (context !is android.app.Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
}
