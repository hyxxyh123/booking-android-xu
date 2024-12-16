package com.my.myapplication.utils.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.my.myapplication.utils.SpUtils;
import com.my.myapplication.utils.global.Config;
import com.my.myapplication.utils.global.MyApplication;

//全局工具类 如获取存储本地token等
public class TokenUtil {

    public static String getToken() {
        //空格不能删 否则上传不了
        return "Authorization-Bearer " + SpUtils.getInstance(MyApplication.sInstance).getString(Config.TOKEN_KEY);
    }

    public static void setToken(String[] data) {
        JSONObject obj = JSON.parseObject(data[0]);
        String token = obj.getString("token");
        SpUtils.getInstance(MyApplication.sInstance).putString(Config.TOKEN_KEY, token);
    }
}
