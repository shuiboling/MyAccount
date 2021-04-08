package com.reihiei.firstapp.bean;

import java.io.Serializable;

public class AnalyseManageBean implements Serializable {

    private String sumMoney;
    private int count;
    private int classify;

    public String getSumMoney() {
        return sumMoney;
    }

    public void setSumMoney(String sumMoney) {
        this.sumMoney = sumMoney;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getClassify() {
        return classify;
    }

    public void setClassify(int classify) {
        this.classify = classify;
    }
}
