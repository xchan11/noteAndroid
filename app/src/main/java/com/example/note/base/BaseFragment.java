package com.example.note.base;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;

import com.bumptech.glide.Glide;
import com.example.note.manager.GlobalConfig;
import com.example.note.utils.ToastUtil;


/**
 * Fragment基类
 */
public abstract class BaseFragment<VM extends BaseViewModel, DB extends ViewDataBinding>
        extends BaseNoViewModelFragment<DB> {

    protected VM viewModel;

    @Override
    protected DB initDataBinding(LayoutInflater inflater, int layoutId, ViewGroup container) {
        DB db = super.initDataBinding(inflater, layoutId, container);
        viewModel = initViewModel();
        initObserve();
        return db;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel.context = getContext();
    }

    /**
     * 监听当前ViewModel中showdialog和error的值
     */
    private void initObserve() {
        if (viewModel == null)
            return;
        viewModel.error.observe(this, o -> {
            showError(o);
        });
        viewModel.lifecycleOwner = this;
        viewModel.context = getContext();
    }
    public void toast(String tips){
        if(GlobalConfig.isDebug()){
            Log.d("ToastMsg",tips);
        }
        ToastUtil.ToastMsg(context, tips!=null?tips:"");
    }
    public void toastDebug(String tips){
        if(GlobalConfig.isDebug()){
            ToastUtil.ToastMsg(context, tips!=null?tips:"");
            Log.d("toastDebug",tips);
        }
    }
    public void toastLong(String tips){
        ToastUtil.ToastLong(context,tips!=null?tips:"");
    }
    public void logD(String tag, String msg){
        if(GlobalConfig.isDebug()){
            Log.d(tag,msg);
        }
    }
    public void logE(String tag, String msg){
        if(GlobalConfig.isDebug()){
            Log.e(tag,msg);
        }
    }
    /**
     * ViewModel发生错误
     */
    private void showError(Object o) {
    }

    public void toImg(int index) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,index);
    }
    protected void loadHead(String url, int errorImg, ImageView iv){
        Glide.with(this).load(url).circleCrop().error(errorImg).into(iv);
    }
    public void turnToActivity(Class<?> cls){
        if(this.isAdded())
            startActivity(new Intent(requireActivity(),cls));
    }
    /**
     * 初始化ViewModel
     */
    protected abstract VM initViewModel();

//    public LiveData<RequestType<MemberItem>> getUserMember(String userId) {
//        return apiService.getUserMember(userId);
//    }
}
