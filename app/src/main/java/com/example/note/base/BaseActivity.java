package com.example.note.base;

import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.databinding.ViewDataBinding;

import com.bumptech.glide.Glide;
import com.example.note.BuildConfig;
import com.example.note.manager.GlobalConfig;
import com.example.note.utils.ToastUtil;

/**
 * Activity基类（需要用到ViewModel和DataBinding）
 */
public abstract class BaseActivity<VM extends BaseViewModel, DB extends ViewDataBinding>
        extends BaseNoViewModelActivity<DB> {

    protected VM viewModel;

    @Override
    protected DB initDataBing(int layoutId) {
        DB db = super.initDataBing(layoutId);  // 拿到db，db的具体类型（ViewDataBinding或其子类）由继承类自行定义
        viewModel = initViewModel();
        viewModel.lifecycleOwner = this;
        viewModel.context = this;
        return db;
    }

    public void toast(String tips) {
        Toast.makeText(context, tips != null ? tips : "", Toast.LENGTH_SHORT).show();
    }

    public void toastDebug(String tips) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, tips != null ? tips : "", Toast.LENGTH_SHORT).show();
            Log.d("toastDebug", tips != null ? tips : "");
        }
    }

//    public void toast(String tips) {
//        if (GlobalConfig.isDebug())
//            copyToClipboard(getApplicationContext(), tips, "");
//        ToastUtil.ToastMsg(context, tips);
//    }
//
//    public void toastDebug(String tips) {
//        if (GlobalConfig.isDebug())
//            copyToClipboard(getApplicationContext(), tips, "");
//        if (GlobalConfig.isDebug()) {
//            ToastUtil.ToastMsg(context, tips);
//            Log.d("toastDebug", tips);
//        }
//    }

    public void toastLong(String tips) {
        ToastUtil.ToastLong(context, tips);
    }

    public void logD(String tag, String msg) {
        if (GlobalConfig.isDebug()) {
            Log.d(tag, msg);
        }
    }

    public void logE(String tag, String msg) {
        if (GlobalConfig.isDebug()) {
            Log.e(tag, msg);
        }
    }

    public void logJ(String msg) {
        if (GlobalConfig.isDebug()) {
            Log.d("Jamrave", msg);
        }
    }


    protected void loadHead(String url, int errorImg, ImageView iv) {
        Glide.with(this).load(url).circleCrop().error(errorImg).into(iv);
    }

    /**
     * 初始化ViewModel（子类需重写该方法）
     */
    protected abstract VM initViewModel();

    /**
     * ViewModel发生错误
     */
    protected void showError(Object o) {
    }
}
