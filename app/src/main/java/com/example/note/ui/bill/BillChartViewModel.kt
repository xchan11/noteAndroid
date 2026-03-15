package com.example.note.ui.bill

import androidx.lifecycle.MutableLiveData
import com.example.note.MyApplication
import com.example.note.auth.AuthPrefs
import com.example.note.base.BaseViewModel
import com.example.note.model.ChartCategoryRatioItem
import com.example.note.model.ChartTrendItem
import com.example.note.utils.isOk
import com.example.note.utils.toastCover

/**
 * 图表页 ViewModel：趋势 + 分类占比。
 */
class BillChartViewModel : BaseViewModel() {

    val trendList = MutableLiveData<List<ChartTrendItem>>(emptyList())
    val ratioList = MutableLiveData<List<ChartCategoryRatioItem>>(emptyList())

    fun loadTrend(yearMonth: String) {
        val owner = lifecycleOwner ?: return
        val ctx = context ?: return
        MyApplication.apiService.getChartTrend("month", yearMonth).observe(owner) { result ->
            if (result.isOk()) {
                trendList.postValue(result.data ?: emptyList())
            } else {
                if (result?.code == 401) {
                    AuthPrefs.setLoggedIn(ctx, false)
                    MyApplication.cookieJar.clear()
                }
                trendList.postValue(emptyList())
            }
        }
    }

    fun loadCategoryRatio(yearMonth: String) {
        val owner = lifecycleOwner ?: return
        val ctx = context ?: return
        MyApplication.apiService.getChartCategoryRatio(yearMonth).observe(owner) { result ->
            if (result.isOk()) {
                ratioList.postValue(result.data ?: emptyList())
            } else {
                if (result?.code == 401) {
                    AuthPrefs.setLoggedIn(ctx, false)
                    MyApplication.cookieJar.clear()
                }
                ratioList.postValue(emptyList())
            }
        }
    }
}
