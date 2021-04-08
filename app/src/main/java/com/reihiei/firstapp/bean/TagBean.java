package com.reihiei.firstapp.bean;

import java.io.Serializable;

public class TagBean implements Serializable {

    private int type; //0支出，1收入，2理财
    private String name;
    private String id;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
