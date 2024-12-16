package com.my.myapplication.bean;

import java.io.Serializable;

//用户数据类
public class UserBean implements Serializable {


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    private String username;
    private Long id;
    private String password;

    public int getGuanli() {
        return guanli;
    }

    public void setGuanli(int guanli) {
        this.guanli = guanli;
    }

    private int guanli = 0;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
