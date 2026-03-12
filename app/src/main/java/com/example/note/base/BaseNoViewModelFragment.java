package com.example.note.base;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;

import com.example.note.R;
import com.example.note.manager.GlobalConfig;


/**
 * Fragment 需要用到DataBinding的基类
 */
public abstract class BaseNoViewModelFragment<DB extends ViewDataBinding> extends Fragment {
    private static final String TAG = "OpenView";
    protected DB dataBinding;
    protected Context context;
    View loadingView;
    public Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (GlobalConfig.isDebug()) {
            Log.d(TAG, "现在显示Fragment为 " + getClass().getSimpleName());
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        dataBinding = initDataBinding(inflater, getLayoutId(), container);
        return dataBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initData();
    }

    private void initLoadingView(View rootView) {
        if (rootView instanceof ViewGroup && !(rootView instanceof ScrollView)) {
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            loadingView = getLayoutInflater().inflate(R.layout.loading_view, null);
            loadingView.setVisibility(View.GONE);
            ((ViewGroup) rootView).addView(loadingView, layoutParams);
        }
    }

    /**
     * 初始化DataBinding
     */
    protected DB initDataBinding(LayoutInflater inflater, int layoutId, ViewGroup container) {
        return DataBindingUtil.inflate(inflater, layoutId, container, false);
    }

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 初始化布局资源
     */
    protected abstract int getLayoutId();

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void showLoading() {
        if(loadingView==null){
            initLoadingView(this.dataBinding.getRoot());
        }
        if (loadingView != null) {
            loadingView.bringToFront();
            loadingView.setVisibility(View.VISIBLE);
        }
    }

    public void hideLoading() {
        if (loadingView != null)
            loadingView.setVisibility(View.GONE);
    }

    public void startAty(Class<?> cls) {
        if(this.isAdded()){
            Intent i = new Intent(requireActivity(), cls);
            startActivity(i);
        }
    }

}
