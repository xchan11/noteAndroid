package com.example.note.base;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.databinding.ViewDataBinding;

import com.bumptech.glide.Glide;
import com.example.note.BuildConfig;
import com.example.note.manager.GlobalConfig;
import com.example.note.utils.ToastUtil;
import com.example.note.R;

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

    /**
     * 统一设置带返回键的通用标题栏（item_toolbar）。
     * 如果当前布局里没有该标题栏，调用也不会崩，只是无效果。
     */
    protected void setupToolbar(String title) {
        if (dataBinding == null) return;
        View root = dataBinding.getRoot();
        if (root == null) return;
        TextView tvTitle = root.findViewById(R.id.tvTitle);
        ImageView ivBack = root.findViewById(R.id.ivBack);
        if (tvTitle != null) {
            tvTitle.setText(title != null ? title : "");
        }
        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }
    }

    /**
     * 仅更新标题文本（适合需要动态修改标题的场景）。
     */
    protected void setToolbarTitle(String title) {
        if (dataBinding == null) return;
        View root = dataBinding.getRoot();
        if (root == null) return;
        TextView tvTitle = root.findViewById(R.id.tvTitle);
        if (tvTitle != null) {
            tvTitle.setText(title != null ? title : "");
        }
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
