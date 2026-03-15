package com.example.note.ui.bill

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.note.MyApplication
import com.example.note.auth.AuthPrefs
import com.example.note.base.BaseViewModel
import com.example.note.model.BillCategory
import com.example.note.utils.cookie_tool.JSONBodyBuilder
import com.example.note.utils.cookie_tool.JSONBodyBuilder.addParams
import com.example.note.utils.cookie_tool.JSONBodyBuilder.submit
import com.example.note.utils.isOk
import com.example.note.utils.toastCover

/**
 * 记账分类管理 ViewModel。
 */
class BillCategoryViewModel : BaseViewModel() {

    val categoryList = MutableLiveData<List<BillCategory>?>()

    fun loadCategories(showToastOnFail: Boolean = true) {
        val owner = lifecycleOwner ?: return
        val ctx = context ?: return
        MyApplication.apiService.getBillCategoryList().observe(owner) { result ->
            if (result.isOk()) {
                categoryList.postValue(result.data ?: emptyList())
            } else {
                if (result?.code == 401) {
                    AuthPrefs.setLoggedIn(ctx, false)
                    MyApplication.cookieJar.clear()
                }
                if (showToastOnFail) (result?.message ?: "加载失败").toastCover()
            }
        }
    }

    fun addCategory(name: String, onSuccess: () -> Unit) {
        val owner = lifecycleOwner ?: return
        val body = JSONBodyBuilder.build().addParams("categoryName", name).submit()
        MyApplication.apiService.addBillCategory(body).observe(owner) { result ->
            if (result.isOk()) {
                (result.message ?: "分类添加成功").toastCover()
                onSuccess()
            } else {
                (result?.message ?: "添加失败").toastCover()
            }
        }
    }

    fun updateCategory(categoryId: Long, name: String, onSuccess: () -> Unit) {
        val owner = lifecycleOwner ?: return
        val body = JSONBodyBuilder.build()
            .addParams("categoryId", categoryId)
            .addParams("categoryName", name)
            .submit()
        MyApplication.apiService.updateBillCategory(body).observe(owner) { result ->
            if (result.isOk()) {
                (result.message ?: "分类编辑成功").toastCover()
                onSuccess()
            } else {
                (result?.message ?: "编辑失败").toastCover()
            }
        }
    }

    fun deleteCategory(categoryId: Long, onSuccess: () -> Unit) {
        val owner = lifecycleOwner ?: return
        val body = JSONBodyBuilder.build().addParams("categoryId", categoryId).submit()
        MyApplication.apiService.deleteBillCategory(body).observe(owner) { result ->
            if (result.isOk()) {
                (result.message ?: "分类删除成功").toastCover()
                onSuccess()
            } else {
                (result?.message ?: "删除失败").toastCover()
            }
        }
    }
}
