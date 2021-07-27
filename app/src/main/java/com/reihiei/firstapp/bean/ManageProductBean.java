package com.reihiei.firstapp.bean;

import java.io.Serializable;

public class ManageProductBean implements Serializable {
    private int type; //0理财，1股，2混，3债，4货
    private String name;
    private int id;

    public ManageProductBean(){

    }

    public ManageProductBean(int id,String name,int type){
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
