package com.reihiei.firstapp.bean;

import java.util.List;

public class AccountBillResp {

    private String sumIn;
    private String sumOut;
    private List<AccountBean> list;

    public String getSumIn() {
        return sumIn;
    }

    public void setSumIn(String sumIn) {
        this.sumIn = sumIn;
    }

    public String getSumOut() {
        return sumOut;
    }

    public void setSumOut(String sumOut) {
        this.sumOut = sumOut;
    }

    public List<AccountBean> getList() {
        return list;
    }

    public void setList(List<AccountBean> list) {
        this.list = list;
    }
}
