package com.example.note.ui.bill

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.note.MyApplication
import com.example.note.auth.AuthPrefs
import com.example.note.base.BaseViewModel
import com.example.note.model.BillRecord
import com.example.note.model.BudgetInfo
import com.example.note.utils.cookie_tool.JSONBodyBuilder
import com.example.note.utils.cookie_tool.JSONBodyBuilder.addParams
import com.example.note.utils.cookie_tool.JSONBodyBuilder.submit
import com.example.note.utils.isOk
import com.example.note.utils.toastCover

/**
 * 记账首页 ViewModel：预算、最近流水、本月汇总、删除记录。
 */
class BillViewModel : BaseViewModel() {

    val budgetInfo = MutableLiveData<BudgetInfo?>()
    val recentList = MutableLiveData<List<BillRecord>>(emptyList())
    val monthIncome = MutableLiveData(0.0)
    val monthExpense = MutableLiveData(0.0)

    fun loadBudget(yearMonth: String) {
        val owner = lifecycleOwner ?: return
        val ctx = context as? Context ?: return
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

    fun loadRecent(limit: Int = 10) {
        val owner = lifecycleOwner ?: return
        val ctx = context as? Context ?: return
        MyApplication.apiService.getBillRecordRecent(limit).observe(owner) { result ->
            if (result.isOk()) {
                recentList.postValue(result.data ?: emptyList())
            } else {
                if (result?.code == 401) {
                    AuthPrefs.setLoggedIn(ctx, false)
                    MyApplication.cookieJar.clear()
                }
                (result?.message ?: "加载失败").toastCover()
            }
        }
    }

    /** 加载本月收支汇总（用于概览） */
    fun loadMonthSummary(yearMonth: String) {
        val owner = lifecycleOwner ?: return
        val (start, end) = monthRange(yearMonth)
        MyApplication.apiService.getBillRecordListByTime(start, end).observe(owner) { result ->
            if (result.isOk()) {
                val list = result.data ?: emptyList()
                var income = 0.0
                var expense = 0.0
                list.forEach {
                    if (it.type == 1) income += it.amount else expense += it.amount
                }
                monthIncome.postValue(income)
                monthExpense.postValue(expense)
            }
        }
    }

    private fun monthRange(yearMonth: String): Pair<String, String> {
        val parts = yearMonth.split("-")
        val y = parts.getOrNull(0) ?: "2026"
        val m = parts.getOrNull(1) ?: "01"
        // 后端要求按分钟精度：yyyy-MM-dd HH:mm
        val start = "$yearMonth-01 00:00"
        val lastDay = when (m.toIntOrNull() ?: 1) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if ((y.toIntOrNull() ?: 2026) % 4 == 0) 29 else 28
            else -> 28
        }
        val end = "$yearMonth-${lastDay.toString().padStart(2, '0')} 23:59"
        return Pair(start, end)
    }

    fun deleteRecord(owner: LifecycleOwner, recordId: Long, onSuccess: () -> Unit, onFail: (String) -> Unit) {
        val body = JSONBodyBuilder.build().addParams("recordId", recordId).submit()
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
