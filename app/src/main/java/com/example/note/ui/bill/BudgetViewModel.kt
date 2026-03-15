package com.example.note.ui.bill

import androidx.lifecycle.MutableLiveData
import com.example.note.MyApplication
import com.example.note.auth.AuthPrefs
import com.example.note.base.BaseViewModel
import com.example.note.model.BudgetInfo
import com.example.note.utils.cookie_tool.JSONBodyBuilder
import com.example.note.utils.cookie_tool.JSONBodyBuilder.addParams
import com.example.note.utils.cookie_tool.JSONBodyBuilder.submit
import com.example.note.utils.isOk
import com.example.note.utils.toastCover

/**
 * 月度预算设置 ViewModel。
 */
class BudgetViewModel : BaseViewModel() {

    val budgetInfo = MutableLiveData<BudgetInfo?>()

    fun loadBudget(yearMonth: String) {
        val owner = lifecycleOwner ?: return
        val ctx = context ?: return
        MyApplication.apiService.getBudget(yearMonth).observe(owner) { result ->
            if (result.isOk()) {
                budgetInfo.postValue(result.data)
            } else {
                if (result?.code == 401) {
                    AuthPrefs.setLoggedIn(ctx, false)
                    MyApplication.cookieJar.clear()
                }
                budgetInfo.postValue(null)
            }
        }
    }

    fun setBudget(yearMonth: String, amount: Double, onSuccess: () -> Unit) {
        val owner = lifecycleOwner ?: return
        val body = JSONBodyBuilder.build()
            .addParams("yearMonth", yearMonth)
            .addParams("budgetAmount", amount)
            .submit()
        MyApplication.apiService.setBudget(body).observe(owner) { result ->
            if (result.isOk()) {
                (result.message ?: "预算设置成功").toastCover()
                onSuccess()
            } else {
                (result?.message ?: "设置失败").toastCover()
            }
        }
    }
}
