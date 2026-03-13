package com.example.note.ui.main

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.note.MyApplication
import com.example.note.base.BaseViewModel
import com.example.note.model.RequestType
import com.example.note.model.UpdatePwdReq

class ChangePwdViewModel : BaseViewModel() {

    val updatePwdResult = MutableLiveData<RequestType<Void>?>()

    fun updatePassword(owner: LifecycleOwner, oldPwd: String, newPwd: String, confirmPwd: String) {
        val body = UpdatePwdReq(oldPwd, newPwd, confirmPwd)
        MyApplication.apiService.updatePwd(body).observe(owner) {
            updatePwdResult.postValue(it)
        }
    }
}

