package com.example.note.api;

import static com.example.note.MyApplication.appContext;
import static com.example.note.MyApplication.cookieJar;

import com.example.note.BuildConfig;
import com.example.note.base.livedata.LiveDataCallAdapterFactory;
import com.example.note.utils.cookie_tool.AppCookieJar;
import com.example.note.utils.cookie_tool.cache.SetCookieCache;
import com.example.note.utils.cookie_tool.persistence.SharedPrefsCookiePersistor;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.example.note.api.LogPrinter;

/**
 * Retrofit统一配置类
 */
public class RetrofitRequest {
    private static RetrofitRequest mInstance;
    private static Retrofit retrofit;

    private OkHttpClient mOkHttpClient;

    private RetrofitRequest() {
        // 持久化 Spring Session Cookie（如 JSESSIONID）
        cookieJar = new AppCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(appContext));

        // 可选：公共请求头
        Interceptor headerInterceptor = chain -> {
            Request original = chain.request();
            Request withHeaders = original.newBuilder()
                    .header("OS", "Android")
                    .header("versionCode", BuildConfig.VERSION_NAME)
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(withHeaders);
        };

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .addInterceptor(headerInterceptor);

        // 可选：只在 debug 看网络日志（你也可以加个 if (BuildConfig.DEBUG) 再加）
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//        clientBuilder.addInterceptor(logging);

        HttpLoggingInterceptor logging =
                new HttpLoggingInterceptor(LogPrinter::printNetLog)
                        .setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(logging);

        mOkHttpClient = clientBuilder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory.create())
                .build();
    }

    // 统一从 BuildConfig 读取 BASE_URL（debug/release 分开在 Gradle 里配置）
    public static String getBaseUrl() {
        return BuildConfig.BASE_URL;
    }

    public static RetrofitRequest initClient() {
        if (mInstance == null) {
            synchronized (RetrofitRequest.class) {
                if (mInstance == null) {
                    mInstance = new RetrofitRequest();
                }
            }
        }
        return mInstance;
    }

    public static Retrofit getRetrofit() {
        return retrofit;
    }

    public static RetrofitRequest getInstance() {
        return initClient();
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }
}