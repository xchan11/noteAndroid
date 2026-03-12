package com.example.note.base;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * 自定义ViewModel的基类
 * 管理页面跳转显示的对话框和错误信息(当前未实现此功能）
 */
public abstract class BaseViewModel extends ViewModel {
    protected MutableLiveData<Object> error = new MutableLiveData<>();
    public LifecycleOwner lifecycleOwner;
    public Context context;
    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     * <p>
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        error = null;
    }
}
