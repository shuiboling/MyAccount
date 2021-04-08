package com.reihiei.firstapp.bean;

import java.io.Serializable;
import java.util.List;

public class AnalyseManageBeanResp implements Serializable {

    private List<AnalyseManageBean> list;
    private String sum;

    public AnalyseManageBeanResp(){

    }

    public AnalyseManageBeanResp(List<AnalyseManageBean> list, String sum) {
        this.list = list;
        this.sum = sum;
    }

    public List<AnalyseManageBean> getList() {
        return list;
    }

    public void setList(List<AnalyseManageBean> list) {
        this.list = list;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }
}
