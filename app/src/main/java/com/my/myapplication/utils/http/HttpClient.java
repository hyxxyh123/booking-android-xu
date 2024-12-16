package com.my.myapplication.utils.http;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.my.myapplication.utils.global.MyApplication;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

//网络请求设置
public class HttpClient {

    private static final int TIMEOUT = 5000;
    private static HttpClient sInstance;
    private OkHttpClient mOkHttpClient;


    public static HttpClient getInstance() {
        if (sInstance == null) {
            synchronized (HttpClient.class) {
                if (sInstance == null) {
                    sInstance = new HttpClient();
                }
            }
        }
        return sInstance;
    }

    public void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.retryOnConnectionFailure(true);

        //输出HTTP请求 响应信息
        mOkHttpClient = builder.build();

        OkGo.getInstance().init(MyApplication.sInstance)
                .setOkHttpClient(mOkHttpClient)
                .setCacheMode(CacheMode.NO_CACHE)
                .setRetryCount(1);

    }

    public GetRequest<JsonBean> get(String serviceUrl, String tag) {
        return OkGo.<JsonBean>get(serviceUrl)
                .headers("Connection", "keep-alive")
                .tag(tag);

    }

    public PostRequest<JsonBean> post(String serviceUrl, String tag) {
        return OkGo.<JsonBean>post(serviceUrl)
                .headers("Connection", "keep-alive")
                .tag(tag);
    }

    public void cancel(String tag) {
        OkGo.cancelTag(mOkHttpClient, tag);
    }

}
