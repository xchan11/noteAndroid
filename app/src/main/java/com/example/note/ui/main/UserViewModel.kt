package com.example.note.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.note.MyApplication
import com.example.note.auth.AuthPrefs
import com.example.note.base.BaseViewModel
import com.example.note.model.LoginUser
import com.example.note.model.RequestType
import com.example.note.utils.isOk
import com.example.note.utils.toastCover

class UserViewModel : BaseViewModel() {

    val userInfo = MutableLiveData<LoginUser?>()

    private fun requestUserInfo(): LiveData<RequestType<LoginUser>> {
        return MyApplication.apiService.getUserInfo()
    }

    fun loadUserInfo() {
        val owner = lifecycleOwner ?: return
        val ctx = context as? Context ?: return
        requestUserInfo().observe(owner) { result ->
            if (result.isOk()) {
                userInfo.postValue(result.data)
            } else {
                if (result?.code == 401) {
                    AuthPrefs.setLoggedIn(ctx, false)
                    MyApplication.cookieJar.clear()
                }
                result?.message.toastCover()
            }
        }
    }
}

