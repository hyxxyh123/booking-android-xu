package com.my.myapplication.utils.http;


import com.alibaba.fastjson.JSON;
import com.my.myapplication.bean.BookingBean;
import com.my.myapplication.bean.ItemBean;
import com.my.myapplication.bean.UserBean;
import com.my.myapplication.utils.global.Config;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;

//网络请求api
public class HttpRequest {

    /**
     * 取消网络请求
     */
    public static void cancel(String tag) {
        HttpClient.getInstance().cancel(tag);
    }


    public static JsonBean register(UserBean userBean) throws IOException {
        String beanToJson = JsonUtil.getBeanToJson(userBean);
        MediaType mediaType = MediaType.Companion.parse("application/json;charset=utf-8");
        RequestBody body = RequestBody.Companion.create(beanToJson, mediaType);
        return JSON.parseObject(HttpClient.getInstance().post(Config.REGISTER_URL, Config.REGISTER_TAG)
                .upRequestBody(body)
                .execute().body().string(), JsonBean.class);

    }


    public static JsonBean login(String name, String psw) throws IOException {

        return JSON.parseObject(
                HttpClient.getInstance().post(Config.LOGIN_URL, Config.LOGIN_TAG)
                        .params("username", name)
                        .params("password", psw)
                        .execute().body().string(), JsonBean.class);
    }

    // 获取数据 用户
    public static JsonBean getUser() throws IOException {
        return JSON.parseObject(
                HttpClient.getInstance().post(Config.USERINFO_URL, Config.USERINFO_TAG)
                        .headers("Authorization", " " + TokenUtil.getToken())
                        .execute().body().string(), JsonBean.class);
    }

    public static JsonBean IsRegister(String username) throws IOException {
        return JSON.parseObject(
                HttpClient.getInstance().post(Config.IS_REGISTER_URL, Config.I_SREGISTER_TAG)
                        .params("username", username)
                        .execute().body().string(), JsonBean.class);

    }

    public static JsonBean addData(BookingBean bean) throws IOException {
        String beanToJson = JsonUtil.getBeanToJson(bean);
        MediaType mediaType = MediaType.Companion.parse("application/json;charset=utf-8");
        RequestBody body = RequestBody.Companion.create(beanToJson, mediaType);
        return JSON.parseObject(HttpClient.getInstance().post(Config.ADDDATA_URL, Config.ADDDATA_TAG)
                .headers("Authorization", " " + TokenUtil.getToken())
                .upRequestBody(body)
                .execute().body().string(), JsonBean.class);


    }


    public static JsonBean addItemData(ItemBean bean) throws IOException {
        String beanToJson = JsonUtil.getBeanToJson(bean);
        MediaType mediaType = MediaType.Companion.parse("application/json;charset=utf-8");
        RequestBody body = RequestBody.Companion.create(beanToJson, mediaType);
        return JSON.parseObject(HttpClient.getInstance().post(Config.ADDITEMDATA_URL, Config.ADDITEMDATA_TAG)
                .headers("Authorization", " " + TokenUtil.getToken())
                .upRequestBody(body)
                .execute().body().string(), JsonBean.class);


    }

    // 添加一张图片 或视频 文件

    public static JsonBean oneUploadFile(String tupian) throws IOException {


        return JSON.parseObject(
                HttpClient.getInstance().post(Config.ONEUPLOADFILE_URL, Config.ONEUPLOADFILE_TAG)
                        .headers("Authorization", " " + TokenUtil.getToken())
                        .params("file", new File(tupian))
                        .execute().body().string(), JsonBean.class);


    }


    public static JsonBean getItemDataById(Long Id) throws IOException {

        return JSON.parseObject(
                HttpClient.getInstance().post(Config.GETITEMDATABYID_URL, Config.GETITEMDATABYID_TAG)
                        .headers("Authorization", " " + TokenUtil.getToken())
                        .params("Id", Id)
                        .execute().body().string(), JsonBean.class);

    }


    //查数据 座位 根据账号
    public static JsonBean getItemDataByUserName(String userName) throws IOException {
        return JSON.parseObject(
                HttpClient.getInstance().post(Config.GETITEMDATABYUSERNAME_URL, Config.GETITEMDATABYUSERNAME_TAG)
                        .headers("Authorization", " " + TokenUtil.getToken())
                        .execute().body().string(), JsonBean.class);

    }


    //    查所有预约 目的 判断当前预约时间是否到期
    public static JsonBean getAllYuYue() throws IOException {
        return JSON.parseObject(
                HttpClient.getInstance().post(Config.GETALLYUYUE_URL, Config.GETALLYUYUE_TAG)
                        .headers("Authorization", " " + TokenUtil.getToken())
                        .execute().body().string(), JsonBean.class);

    }


    /**
     * 根据分类查数据
     */

    public static JsonBean getDataByLeiXing(String leixing) throws IOException {
        return JSON.parseObject(
                HttpClient.getInstance().post(Config.GETDATABYLEIXING_URL, Config.GETDATABYLEIXING_TAG)
                        .headers("Authorization", " " + TokenUtil.getToken())
                        .params("leixing", leixing)
                        .execute().body().string(), JsonBean.class);
    }

    public static JsonBean getAllData() throws IOException {

        return JSON.parseObject(
                HttpClient.getInstance().post(Config.GETALLDATA_URL, Config.GETALLDATA_TAG)
                        .headers("Authorization", " " + TokenUtil.getToken())
                        .execute().body().string(), JsonBean.class);

    }

    public static JsonBean updateDataById(BookingBean bean) throws IOException {
        String beanToJson = JsonUtil.getBeanToJson(bean);
        MediaType mediaType = MediaType.Companion.parse("application/json;charset=utf-8");
        RequestBody body = RequestBody.Companion.create(beanToJson, mediaType);
        return JSON.parseObject(HttpClient.getInstance().post(Config.UPDATEDATABYID_URL, Config.UPDATEDATABYID_TAG)
                .headers("Authorization", " " + TokenUtil.getToken())
                .upRequestBody(body)
                .execute().body().string(), JsonBean.class);

    }

    /**
     * 根据id更新数据
     */
    public static JsonBean updateItemZhuangTaiAndUserNameById(Long id, Integer zhuangtai, String username, String yuyueshijian) throws IOException {

        return JSON.parseObject(
                HttpClient.getInstance().post(Config.UPDATEITEMZHUANGTAIANDUSERNAMEBYID_URL, Config.UPDATEITEMZHUANGTAIANDUSERNAMEBYID_TAG)
                        .headers("Authorization", " " + TokenUtil.getToken())
                        .params("Id", id)
                        .params("zhuangtai", zhuangtai)
                        .params("yuyueshijian", yuyueshijian)
                        .execute().body().string(), JsonBean.class);

    }


    /**
     * 根据id更新数据
     */
    public static JsonBean resetZhuangTai(Long id) throws IOException {

        return JSON.parseObject(
                HttpClient.getInstance().post(Config.RESETZHUANGTAI_URL, Config.RESETZHUANGTAI_TAG)
                        .headers("Authorization", " " + TokenUtil.getToken())
                        .params("Id", id)
                        .execute().body().string(), JsonBean.class);

    }


    public static JsonBean getDataById(Long Id) throws IOException {

        return JSON.parseObject(
                HttpClient.getInstance().post(Config.GETDATABYID_URL, Config.GETDATABYID_TAG)
                        .headers("Authorization", " " + TokenUtil.getToken())
                        .params("Id", Id)
                        .execute().body().string(), JsonBean.class);

    }

    public static JsonBean delDataById(Long Id) throws IOException {

        return JSON.parseObject(
                HttpClient.getInstance().post(Config.DELDATABYID_URL, Config.DELDATABYID_TAG)
                        .headers("Authorization", " " + TokenUtil.getToken())
                        .params("Id", Id)
                        .execute().body().string(), JsonBean.class);

    }

    public static JsonBean delItemDataById(Long Id) throws IOException {

        return JSON.parseObject(
                HttpClient.getInstance().post(Config.DELITEMDATABYID_URL, Config.DELITEMDATABYID_TAG)
                        .headers("Authorization", " " + TokenUtil.getToken())
                        .params("Id", Id)
                        .execute().body().string(), JsonBean.class);

    }

}
