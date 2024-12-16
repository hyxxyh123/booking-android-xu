package com.my.myapplication.helper;


import android.app.Activity;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.my.myapplication.bean.BookingBean;
import com.my.myapplication.bean.ItemBean;
import com.my.myapplication.bean.UserBean;
import com.my.myapplication.utils.LoadProgressDialog;
import com.my.myapplication.utils.http.HttpRequest;
import com.my.myapplication.utils.http.JsonBean;
import com.my.myapplication.utils.http.JsonUtil;
import com.my.myapplication.utils.http.TokenUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyHelper {

    private static MyHelper instance = null;
    private static LoadProgressDialog loadProgressDialog;
    private static Activity mActivity1;


    public MyHelper(Activity mActivity) {


    }

    public static MyHelper getInstance(Activity mActivity) {
        mActivity1 = mActivity;
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadProgressDialog = new LoadProgressDialog(mActivity1, "加载中……", false);

            }
        });

        if (instance == null) {
            instance = new MyHelper(mActivity1);
        }
        return instance;
    }

    private void showLoading() {
        mActivity1.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadProgressDialog.show();

            }
        });
    }

    private void hideLoading() {
        mActivity1.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadProgressDialog.dismiss();

            }
        });
    }

    private void hideErrLoading() {
        mActivity1.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mActivity1, "失败,请重试..", Toast.LENGTH_SHORT).show();
                loadProgressDialog.dismiss();

            }
        });
    }

    // 添加一条数据 用户
    public long addUser(UserBean user) throws IOException {
        showLoading();
        JsonBean jsonBean = HttpRequest.register(user);
        if (jsonBean.getCode() == 200) {
            hideLoading();
            return 1;
        }
        hideLoading();
        return -1;
    }


    /**
     * 登录校验账号密码 用户
     */
    public UserBean matchAccount(String name, String psw) throws IOException {
        showLoading();
        UserBean re_user = null;
        JsonBean jsonBean = HttpRequest.login(name, psw);
        if (jsonBean.getCode() == 200) {
            TokenUtil.setToken(jsonBean.getData());
            JsonBean jsonBean1 = HttpRequest.getUser();
            if (jsonBean1.getCode() == 200) {
                re_user = JsonUtil.getJsonToBean(jsonBean1.getData()[0], UserBean.class);
            }

        }
        hideLoading();
        return re_user;
    }

    /**
     * 判断账号是否被注册 用户
     */
    public boolean isRegister(String name) throws IOException {

        JsonBean jsonBean = HttpRequest.IsRegister(name);
        return jsonBean.getCode() == 200;
    }

    // 添加一条数据
    public long addData(BookingBean bean) throws IOException {
        Long id = 0L;
        JsonBean jsonBean = HttpRequest.addData(bean);
        if (jsonBean.getCode() == 200) {
            JSONObject obj = JSON.parseObject(jsonBean.getData()[0]);
            id = obj.getLong("id");
            return id;
        }
        return id;

    }

    // 添加一条数据 座位数据
    public long addItemData(ItemBean bean) throws IOException {

        Long id = 0L;
        JsonBean jsonBean = HttpRequest.addItemData(bean);
        if (jsonBean.getCode() == 200) {

            return 1;
        }

        return id;
    }

    /**
     * 查座位数据 根据id
     */

    public List<ItemBean> getItemDataById(Long id) throws IOException {
        List<ItemBean> list = new ArrayList<>();

        JsonBean jsonBean = HttpRequest.getItemDataById(id);
        if (jsonBean.getCode() == 200) {
            list = JsonUtil.getJsonToList(jsonBean.getData(), ItemBean.class);
        }
        return list;
    }

    //查数据 座位 根据账号
    public List<ItemBean> getItemDataByUserName(String userName) throws IOException {
        List<ItemBean> list = new ArrayList<>();

        JsonBean jsonBean = HttpRequest.getItemDataByUserName(userName);
        if (jsonBean.getCode() == 200) {
            list = JsonUtil.getJsonToList(jsonBean.getData(), ItemBean.class);
        }
        return list;
    }

    //    查所有预约 目的 判断当前预约时间是否到期
    public List<ItemBean> getAllYuYue() throws IOException {
        List<ItemBean> list = new ArrayList<>();

        JsonBean jsonBean = HttpRequest.getAllYuYue();
        if (jsonBean.getCode() == 200) {
            list = JsonUtil.getJsonToList(jsonBean.getData(), ItemBean.class);
        }
        return list;
    }

    // 添加一条数据
    public String oneUploadFile(String tupian) throws IOException {

        String url = "";
        JsonBean jsonBean = HttpRequest.oneUploadFile(tupian);
        if (jsonBean.getCode() == 200) {
            JSONObject obj = JSON.parseObject(jsonBean.getData()[0]);
            url = obj.getString("imgUrl");
            return url;
        }


        return url;

    }


    /**
     * 查所有数据 查
     */

    public List<BookingBean> getAllData() throws IOException {
        List<BookingBean> list = new ArrayList<>();

        JsonBean jsonBean = HttpRequest.getAllData();
        if (jsonBean.getCode() == 200) {
            list = JsonUtil.getJsonToList(jsonBean.getData(), BookingBean.class);
        }
        return list;
    }


    //模糊查数据
    public List<BookingBean> getDataByLeiXing(String leixing) throws IOException {
        if ("全部".equals(leixing)) {
            return getAllData();
        }
        List<BookingBean> list = new ArrayList<>();

        JsonBean jsonBean = HttpRequest.getDataByLeiXing(leixing);
        if (jsonBean.getCode() == 200) {
            list = JsonUtil.getJsonToList(jsonBean.getData(), BookingBean.class);
        }
        return list;
    }

    /**
     * 根据ID查一条数据
     */

    public BookingBean getDataById(Long id) throws IOException {
        List<BookingBean> list = new ArrayList<>();
        JsonBean jsonBean = HttpRequest.getDataById(id);
        if (jsonBean.getCode() == 200) {
            list = JsonUtil.getJsonToList(jsonBean.getData(), BookingBean.class);
            return list.get(0);
        }
        return null;
    }

    /**
     * 根据id更新数据
     */
    public int updateItemZhuangTaiAndUserNameById(Long id, Integer zhuangtai, String username, String yuyueshijian) throws IOException {

        JsonBean jsonBean = HttpRequest.updateItemZhuangTaiAndUserNameById(id, zhuangtai, username, yuyueshijian);
        if (jsonBean.getCode() == 200) {
            return 1;
        }
        return 0;
    }

    /**
     * 根据id更新数据
     */
    public int resetZhuangTai(Long id) throws IOException {

        JsonBean jsonBean = HttpRequest.resetZhuangTai(id);
        if (jsonBean.getCode() == 200) {
            return 1;
        }
        return 0;
    }


    /**
     * 根据id更新数据
     */
    public int updateDataById(BookingBean bean) throws IOException {

        JsonBean jsonBean = HttpRequest.updateDataById(bean);
        if (jsonBean.getCode() == 200) {

            return 1;
        }
        return -1;
    }

    /**
     * 根据id删除数据
     */

    public int delDataById(long id) throws IOException {
        JsonBean jsonBean = HttpRequest.delDataById(id);
        if (jsonBean.getCode() == 200) {
            return 1;
        }
        return -1;
    }


    /**
     * 根据id删除数据
     */

    public int delItemDataById(Long id) throws IOException {
        JsonBean jsonBean = HttpRequest.delItemDataById(id);
        if (jsonBean.getCode() == 200) {
            return 1;
        }
        return -1;
    }


}
