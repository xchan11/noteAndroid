package com.example.note.ui.bill

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.note.MyApplication
import com.example.note.base.BaseViewModel
import com.example.note.model.BillRecord
import com.example.note.model.RequestType
import com.example.note.utils.cookie_tool.JSONBodyBuilder
import com.example.note.utils.cookie_tool.JSONBodyBuilder.addParams
import com.example.note.utils.cookie_tool.JSONBodyBuilder.submit
import com.example.note.utils.toastCover

/**
 * 新增/编辑收支记录 ViewModel。
 */
class BillAddViewModel : BaseViewModel() {

    val saveResult = MutableLiveData<RequestType<BillRecord>?>()

    fun addRecord(
        owner: LifecycleOwner,
        categoryId: Long,
        type: Int,
        amount: Double,
        remark: String?,
        createTime: String
    ) {
        val body = JSONBodyBuilder.build()
            .addParams("categoryId", categoryId)
            .addParams("type", type)
            .addParams("amount", amount)
            .addParams("remark", remark ?: "")
            .addParams("createTime", createTime)
            .submit()
        MyApplication.apiService.addBillRecord(body).observe(owner) { saveResult.postValue(it) }
    }

    fun updateRecord(
        owner: LifecycleOwner,
        recordId: Long,
        categoryId: Long,
        type: Int,
        amount: Double,
        remark: String?,
        createTime: String
    ) {
        val body = JSONBodyBuilder.build()
            .addParams("recordId", recordId)
            .addParams("categoryId", categoryId)
            .addParams("type", type)
            .addParams("amount", amount)
            .addParams("remark", remark ?: "")
            .addParams("createTime", createTime)
            .submit()
        MyApplication.apiService.updateBillRecord(body).observe(owner) { saveResult.postValue(it) }
    }
}
