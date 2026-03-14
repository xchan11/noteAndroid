package com.example.note.ui.goods

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LifecycleOwner
import com.example.note.MyApplication
import com.example.note.base.BaseViewModel
import com.example.note.model.GoodsInfo
import com.example.note.utils.toastCover

/**
 * 物品列表 ViewModel：支持按分类和按提醒两种模式。
 */
class GoodsListViewModel : BaseViewModel() {

    val goodsList = MutableLiveData<List<GoodsInfo>>(emptyList())

    fun loadByCategory(categoryId: Long) {
        val owner = lifecycleOwner ?: return
        MyApplication.apiService.listGoodsByCategory(categoryId).observe(owner) { result ->
            if (result != null && result.code == 200) {
                goodsList.postValue(result.data ?: emptyList())
            } else {
                (result?.message ?: "加载物品失败").toastCover()
            }
        }
    }

    fun loadByRemind(type: Int) {
        val owner = lifecycleOwner ?: return
        MyApplication.apiService.listGoodsByRemind(type).observe(owner) { result ->
            if (result != null && result.code == 200) {
                goodsList.postValue(result.data ?: emptyList())
            } else {
                (result?.message ?: "加载物品失败").toastCover()
            }
        }
    }

    fun deleteGoods(owner: LifecycleOwner, goodsId: Long, onSuccess: () -> Unit, onFail: (String) -> Unit) {
        MyApplication.apiService.deleteGoods(goodsId).observe(owner) { result ->
            if (result != null && result.code == 200) {
                onSuccess()
            } else {
                val msg = result?.message ?: "删除失败，请重试"
                onFail(msg)
            }
        }
    }
}

