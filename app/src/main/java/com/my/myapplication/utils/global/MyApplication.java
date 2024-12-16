package com.my.myapplication.utils.global;

import android.app.Application;

import com.my.myapplication.utils.http.HttpClient;

//自定义Application 初始化数据
public class MyApplication extends Application {
    public static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        HttpClient.getInstance().init();

    }
}
