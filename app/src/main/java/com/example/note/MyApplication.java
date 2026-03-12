package com.example.note;

import android.app.Application;
import android.content.Context;

import com.example.note.api.ApiService;
import com.example.note.api.RetrofitRequest;
import com.example.note.utils.cookie_tool.AppCookieJar;

public class MyApplication extends Application {

    // 全局 Application Context
    public static Context appContext;

    // 全局 CookieJar（由 RetrofitRequest 初始化并赋值）
    public static AppCookieJar cookieJar;

    // 全局 ApiService（可选，方便各处直接用）
    public static ApiService apiService;

    @Override
    public void onCreate() {
        super.onCreate();
        appContext = getApplicationContext();

        // 初始化网络层（内部会创建 OkHttpClient + Retrofit + CookieJar）
        RetrofitRequest.initClient();
        apiService = RetrofitRequest.getRetrofit().create(ApiService.class);
    }
}