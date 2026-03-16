package com.example.note.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.note.MyApplication
import com.example.note.auth.AuthPrefs
import com.example.note.base.BaseViewModel
import com.example.note.model.LoginUser
import com.example.note.model.RequestType
import com.example.note.model.UpdateInfoReq
import com.example.note.utils.isOk
import com.example.note.utils.toastCover

class UserViewModel : BaseViewModel() {

    val userInfo = MutableLiveData<LoginUser?>()
    val updateInfoResult = MutableLiveData<RequestType<Void>?>()
    val logoutResult = MutableLiveData<RequestType<Void>?>()
    val cancelResult = MutableLiveData<RequestType<Void>?>()
    val goodsTotal = MutableLiveData<Long>()
    val billTotal = MutableLiveData<Long>()
    val noteTodoTotal = MutableLiveData<Long>()

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

    fun loadStats() {
        val owner = lifecycleOwner ?: return
        MyApplication.apiService.getGoodsTotal().observe(owner) { result ->
            if (result?.code == 200 && result.data != null) {
                goodsTotal.postValue(result.data.count ?: 0L)
            }
        }
        MyApplication.apiService.getBillTotal().observe(owner) { result ->
            if (result?.code == 200 && result.data != null) {
                billTotal.postValue(result.data.count ?: 0L)
            }
        }
        MyApplication.apiService.getNoteTodo().observe(owner) { result ->
            if (result?.code == 200 && result.data != null) {
                noteTodoTotal.postValue(result.data.count ?: 0L)
            }
        }
    }

    /** 修改基本信息 */
    fun updateInfo(owner: androidx.lifecycle.LifecycleOwner, username: String, phone: String) {
        val body = UpdateInfoReq(username, phone)
        MyApplication.apiService.updateInfo(body).observe(owner) {
            updateInfoResult.postValue(it)
        }
    }

    /** 退出登录 */
    fun requestLogout(owner: androidx.lifecycle.LifecycleOwner) {
        MyApplication.apiService.logout().observe(owner) {
            logoutResult.postValue(it)
        }
    }

    /** 注销账号 */
    fun requestCancel(owner: androidx.lifecycle.LifecycleOwner) {
        MyApplication.apiService.cancel().observe(owner) {
            cancelResult.postValue(it)
        }
    }
}

