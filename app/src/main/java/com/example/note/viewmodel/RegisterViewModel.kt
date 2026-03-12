package com.example.note.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.note.MyApplication.apiService
import com.example.note.base.BaseViewModel
import com.example.note.model.LoginUser
import com.example.note.model.RequestType
import com.example.note.utils.cookie_tool.JSONBodyBuilder
import com.example.note.utils.cookie_tool.JSONBodyBuilder.addParams
import com.example.note.utils.cookie_tool.JSONBodyBuilder.submit
import okhttp3.RequestBody

class RegisterViewModel : BaseViewModel() {

    val registerResult = MutableLiveData<RequestType<LoginUser>?>()

    private fun register(body: RequestBody): LiveData<RequestType<LoginUser>> {
        return apiService.register(body)
    }

    fun register(
        lifecycle: LifecycleOwner,
        username: String,
        phone: String,
        password: String,
        confirmPassword: String
    ) {
        val body = JSONBodyBuilder.build()
            .addParams("username", username)
            .addParams("phone", phone)
            .addParams("password", password)
            .addParams("confirmPassword", confirmPassword)
            .submit()

        register(body).observe(lifecycle) { result ->
            registerResult.postValue(result)
        }
    }
}

