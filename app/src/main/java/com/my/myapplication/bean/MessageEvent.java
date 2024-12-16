package com.my.myapplication.bean;

public class MessageEvent {
    public MessageEvent(Long yuyueshijian, Integer flag) {
        this.yuyueshijian = yuyueshijian;
        this.flag = flag;
    }

    public Long getYuyueshijian() {
        return yuyueshijian;
    }

    public void setYuyueshijian(Long yuyueshijian) {
        this.yuyueshijian = yuyueshijian;
    }

    private Long yuyueshijian;

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    private Integer flag;

    public MessageEvent(Integer flag, String msg) {
        this.flag = flag;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String msg;
}
