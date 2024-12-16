package com.my.myapplication.utils.http;

//网络返回数据基类
public class JsonBean {

    private String[] data;
    private int code;
    private String message;
    private boolean success;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String[] getData() {
        return data;
    }

    public void setData(String[] data) {
        this.data = data;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


}
