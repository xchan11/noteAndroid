package com.example.note.viewmodel

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.note.MyApplication
import com.example.note.MyApplication.apiService
import com.example.note.auth.AuthPrefs
import com.example.note.base.BaseViewModel
import com.example.note.model.LoginUser
import com.example.note.model.RequestType
import com.example.note.utils.cookie_tool.JSONBodyBuilder
import com.example.note.utils.cookie_tool.JSONBodyBuilder.addParams
import com.example.note.utils.cookie_tool.JSONBodyBuilder.submit
import com.example.note.utils.isOk
import com.example.note.utils.toastCover
import okhttp3.RequestBody

/**
 * 登录页 VM，模仿参考项目写法：暴露 login(lifecycle, phone, password)，内部 observe 并 toast。
 */
class LoginViewModel : BaseViewModel() {

    /** 登录成功时 post 用户信息，Activity observe 后跳转 MainActivity */
    val loginSuccess = MutableLiveData<LoginUser?>()

    private fun loginByPhoneAndPwd(body: RequestBody): LiveData<RequestType<LoginUser>> {
        return apiService.login(body)
    }

    fun login(lifecycle: LifecycleOwner, phone: String, password: String) {
        val body = JSONBodyBuilder.build()
            .addParams("phone", phone)
            .addParams("password", password)
            .submit()

        loginByPhoneAndPwd(body).observe(lifecycle) {
            if (it.isOk()) {
                AuthPrefs.setLoggedIn(lifecycle as Context, true)
                loginSuccess.postValue(it.data)
            } else {
                if (it?.code == 401) {
                    AuthPrefs.setLoggedIn(lifecycle as Context, false)
                    MyApplication.cookieJar.clear()
                }
                it?.message.toastCover()
            }
        }
    }
}
