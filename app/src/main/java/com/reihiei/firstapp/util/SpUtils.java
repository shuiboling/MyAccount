package com.reihiei.firstapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SpUtils {

    private static SpUtils spUtils = new SpUtils();
    private SharedPreferences sp;

    private SpUtils(){

    }

    public static SpUtils getInstance(){
        return spUtils;
    }

    public void init(Context context){
        if(sp == null){
            sp = context.getSharedPreferences("my_account",Context.MODE_PRIVATE);
        }
    }

    public boolean setString(String key,String value){
        return sp.edit().putString(key,value).commit();
    }

    public String getString(String key,String defaultStr){
        return sp.getString(key,defaultStr);
    }

    public boolean setBoolean(String key,boolean value){
        return sp.edit().putBoolean(key,value).commit();
    }

    public Boolean getBoolean(String key,boolean defaultStr){
        return sp.getBoolean(key,defaultStr);
    }
}
