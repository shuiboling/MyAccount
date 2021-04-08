package com.reihiei.firstapp.bean;

import java.util.List;

public class ManageBeanResp {
    List<ManageBean> list;
    String sum;

    public List<ManageBean> getList() {
        return list;
    }

    public void setList(List<ManageBean> list) {
        this.list = list;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }
}
