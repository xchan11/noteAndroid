package com.example.note.ui.goods

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LifecycleOwner
import com.example.note.MyApplication
import com.example.note.base.BaseViewModel
import com.example.note.model.GoodsInfo
import com.example.note.utils.cookie_tool.JSONBodyBuilder
import com.example.note.utils.cookie_tool.JSONBodyBuilder.addParams
import com.example.note.utils.cookie_tool.JSONBodyBuilder.submit
import com.example.note.utils.toastCover

/**
 * 新增/编辑物品共用的 ViewModel。
 */
class GoodsAddEditViewModel : BaseViewModel() {

    val saveResult = MutableLiveData<com.example.note.model.RequestType<GoodsInfo>?>()

    fun addGoods(
        owner: LifecycleOwner,
        categoryId: Long,
        goodsName: String,
        traceInfo: String,
        shelfLife: Long?,
        openDate: Long?
    ) {
        val body = JSONBodyBuilder.build()
            .addParams("categoryId", categoryId)
            .addParams("goodsName", goodsName)
            .addParams("traceInfo", traceInfo)
            .addParams("shelfLife", shelfLife)
            .addParams("openDate", openDate)
            .submit()
        MyApplication.apiService.addGoods(body).observe(owner) { result ->
            saveResult.postValue(result)
        }
    }

    fun updateGoods(
        owner: LifecycleOwner,
        goodsId: Long,
        categoryId: Long,
        goodsName: String,
        traceInfo: String,
        shelfLife: Long?,
        openDate: Long?
    ) {
        val body = JSONBodyBuilder.build()
            .addParams("goodsId", goodsId)
            .addParams("categoryId", categoryId)
            .addParams("goodsName", goodsName)
            .addParams("traceInfo", traceInfo)
            .addParams("shelfLife", shelfLife)
            .addParams("openDate", openDate)
            .submit()
        MyApplication.apiService.updateGoods(body).observe(owner) { result ->
            saveResult.postValue(result)
        }
    }
}

