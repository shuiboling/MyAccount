package com.reihiei.firstapp.util;

import android.content.Context;

import com.reihiei.firstapp.db.DbUtils;

public class CommonUtils {

    public static String getClassText(int classify, Context mContext) {
        if(classify == 0){
            return "็่ดข";
        }else if(classify == 1){
            return "ๅบ้";
        }else {
            return DbUtils.getInstance(mContext).queryTagById(classify+"").getName();
        }
    }
}
