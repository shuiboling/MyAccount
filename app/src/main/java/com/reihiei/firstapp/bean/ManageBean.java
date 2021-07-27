package com.reihiei.firstapp.bean;

import java.io.Serializable;

public class ManageBean implements Serializable {

    private int year;
    private int month;
    private int day;
    private int productid;
    private String money;   //金额
    private int classify;    //类别:理财、基金
    private String addTime;  //添加时间戳
    private int channelid;
    private long eventId;
    private int shuhui; //赎回 0没有，1赎回
    private String nameP;   //产品名
    private String nameC;   //渠道名
    private int typeP;  //产品类别：理财、股基...

    public String getNameP() {
        return nameP;
    }

    public void setNameP(String nameP) {
        this.nameP = nameP;
    }

    public String getNameC() {
        return nameC;
    }

    public void setNameC(String nameC) {
        this.nameC = nameC;
    }

    public int getTypeP() {
        return typeP;
    }

    public void setTypeP(int typeP) {
        this.typeP = typeP;
    }

    public int getShuhui() {
        return shuhui;
    }

    public void setShuhui(int shuhui) {
        this.shuhui = shuhui;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getClassify() {
        return classify;
    }

    public void setClassify(int classify) {
        this.classify = classify;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public int getProductid() {
        return productid;
    }

    public void setProductid(int productid) {
        this.productid = productid;
    }

    public int getChannelid() {
        return channelid;
    }

    public void setChannelid(int channelid) {
        this.channelid = channelid;
    }
}
