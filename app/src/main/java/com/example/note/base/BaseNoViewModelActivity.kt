package com.example.note.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.note.MyApplication
import com.example.note.R
import com.example.note.manager.GlobalConfig
import com.example.note.utils.AppManager
import com.google.android.material.snackbar.Snackbar

/**
 * Activity基类（无ViewModel且需要用到DataBinding）
 * 业务级别的放在这
 */
abstract class BaseNoViewModelActivity<DB : ViewDataBinding> :SystemBasicActivity() {
    lateinit var dataBinding: DB
    lateinit var context: Context // 上下文
    private lateinit var loadingView: View
    lateinit var handler: Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (GlobalConfig.isDebug()) {
            Log.d("OpenView", "现在显示Activity为 " + javaClass.simpleName)
        }
        context = this
        AppManager.getInstance().addActivity(this) // 将当前Activity添加到AppManager管理的统一堆栈中
//        if (this !is MessageActivity) {
//            ImmersionBar.with(this)
//                .transparentStatusBar()
//                .fullScreen(false)
//                .init()
//        }
        val layoutId = getLayoutId() // 获取布局id

        handler = Handler(mainLooper)
        dataBinding = initDataBing(layoutId) // 根据布局id初始化dataBinding（同时给当前Activity绑定布局）
        handleIntent(intent) // 获取跳转的intent
        // 初始化加载中视图
        loadingView = layoutInflater.inflate(R.layout.loading_view, null)
        loadingView.visibility = View.GONE

        // 添加加载中视图到根布局
        addContentView(
            loadingView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        initView() // 初始化视图
        initData() // 初始化数据
    }

    override fun onResume() {
        super.onResume()
//        MyApplication.currentAty = this
    }

    /**
     * 获取跳转的intent
     * （只定义规范，不强制要求重写该方法）
     */
    protected open fun handleIntent(intent: Intent?) {}

    /**
     * 初始化视图（子类必须重写该方法）
     */
    protected abstract fun initView()

    /**
     * 初始化数据（子类必须重写该方法）
     */
    protected abstract fun initData()
    protected fun onCreateFetcher() {}

    /**
     * 初始化 DataBinding
     */
    protected open fun initDataBing(layoutId: Int): DB {
        return DataBindingUtil.setContentView(this, layoutId)
    }

    fun goActivity(clazz: Class<*>?,vararg pairs:Pair<String,Any>) {
        startActivity(Intent(this, clazz).apply{
            pairs?.forEach {
                if(it.second is Int)
                    this.putExtra(it.first,it.second as Int)
                if(it.second is String)
                    this.putExtra(it.first,it.second as String)
                if(it.second is Float)
                    this.putExtra(it.first,it.second as Float)
                if(it.second is Double)
                    this.putExtra(it.first,it.second as Double)
                if(it.second is Long)
                    this.putExtra(it.first,it.second as Long)
                if(it.second is Boolean)
                    this.putExtra(it.first,it.second as Boolean)
            }
        })
    }

    protected abstract fun getLayoutId(): Int


    override fun onDestroy() {
        super.onDestroy()
        // 解绑布局
        dataBinding?.unbind()

        // 将当前Activity从AppManager统一管理的堆栈中移除
        AppManager.getInstance().removeActivity(this)

        handler?.removeCallbacksAndMessages(null)


    }

    fun showLoading() {
        loadingView?.bringToFront()
        loadingView?.visibility = View.VISIBLE

    }

    fun hideLoading() {
        loadingView?.visibility = View.GONE
    }

    protected fun showSnack(view: View?, message: String?) {
        Snackbar.make(view!!, message!!, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

//    fun CommonDialog.show(){
//        this.show(supportFragmentManager,"tag")
//    }
}