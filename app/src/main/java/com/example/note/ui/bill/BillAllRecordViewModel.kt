package com.example.note.ui.bill

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.note.MyApplication
import com.example.note.base.BaseViewModel
import com.example.note.model.BillRecord
import com.example.note.utils.cookie_tool.JSONBodyBuilder.build
import com.example.note.utils.cookie_tool.JSONBodyBuilder.submit
import com.example.note.utils.toastCover

/**
 * 全部收支记录 ViewModel：按月加载列表 + 删除记录。
 */
class BillAllRecordViewModel : BaseViewModel() {

    val recordList = MutableLiveData<List<BillRecord>>(emptyList())

    fun loadByMonth(yearMonth: String) {
        val owner = lifecycleOwner ?: return
        MyApplication.apiService.getBillRecordListAllByMonth(yearMonth).observe(owner) { result ->
            if (result != null && result.code == 200) {
                recordList.postValue(result.data ?: emptyList())
            } else {
                (result?.message ?: "加载失败，请稍后重试").toastCover()
            }
        }
    }

    fun deleteRecord(
        owner: LifecycleOwner,
        recordId: Long,
        onSuccess: () -> Unit,
        onFail: (String) -> Unit
    ) {
        val body = build()
            .put("recordId", recordId)
            .submit()
        MyApplication.apiService.deleteBillRecord(body).observe(owner) { result ->
            if (result != null && result.code == 200) {
                (result.message ?: "删除成功").toastCover()
                onSuccess()
            } else {
                onFail(result?.message ?: "删除失败")
            }
        }
    }
}

