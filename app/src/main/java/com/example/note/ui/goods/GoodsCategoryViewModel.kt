package com.example.note.ui.goods

import androidx.lifecycle.MutableLiveData
import com.example.note.MyApplication
import com.example.note.auth.AuthPrefs
import com.example.note.base.BaseViewModel
import com.example.note.model.GoodsCategory
import com.example.note.utils.cookie_tool.JSONBodyBuilder
import com.example.note.utils.cookie_tool.JSONBodyBuilder.addParams
import com.example.note.utils.cookie_tool.JSONBodyBuilder.submit
import com.example.note.utils.isOk
import com.example.note.utils.toastCover

/**
 * 分类相关 ViewModel：负责分类列表加载、新增、修改、删除。
 */
class GoodsCategoryViewModel : BaseViewModel() {

    // 初始为 null，避免一开始就触发空列表回调弹 Toast
    val categoryList = MutableLiveData<List<GoodsCategory>?>()

    fun loadCategories(showToastOnFail: Boolean = true) {
        val owner = lifecycleOwner ?: return
        val ctx = context ?: return
        MyApplication.apiService.getCategoryList().observe(owner) { result ->
            if (result.isOk()) {
                categoryList.postValue(result.data ?: emptyList())
            } else {
                if (result?.code == 401) {
                    AuthPrefs.setLoggedIn(ctx, false)
                    MyApplication.cookieJar.clear()
                }
                if (showToastOnFail) {
                    (result?.message ?: "加载分类失败").toastCover()
                }
            }
        }
    }

    fun addCategory(name: String, onSuccess: () -> Unit) {
        val owner = lifecycleOwner ?: return
        val body = JSONBodyBuilder.build()
            .addParams("categoryName", name)
            .submit()
        MyApplication.apiService.addCategory(body).observe(owner) { result ->
            if (result.isOk()) {
                "分类添加成功".toastCover()
                onSuccess()
            } else {
                (result?.message ?: "添加失败，请重试").toastCover()
            }
        }
    }

    /**
     * 修改分类名称。
     */
    fun updateCategory(categoryId: Long, name: String, onSuccess: () -> Unit) {
        val owner = lifecycleOwner ?: return
        val body = JSONBodyBuilder.build()
            .addParams("categoryId", categoryId)
            .addParams("categoryName", name)
            .submit()
        MyApplication.apiService.updateCategory(body).observe(owner) { result ->
            if (result.isOk()) {
                (result.message ?: "分类修改成功").toastCover()
                onSuccess()
            } else {
                (result?.message ?: "修改分类失败，请重试").toastCover()
            }
        }
    }

    /**
     * 删除分类。
     */
    fun deleteCategory(categoryId: Long, onSuccess: () -> Unit) {
        val owner = lifecycleOwner ?: return
        MyApplication.apiService.deleteCategory(categoryId).observe(owner) { result ->
            if (result.isOk()) {
                // 文案适配：后端已做级联删除，因此提示“分类及旗下物品已删除”
                (result.message ?: "分类及旗下物品已删除").toastCover()
                onSuccess()
            } else {
                (result?.message ?: "删除分类失败，请重试").toastCover()
            }
        }
    }
}

