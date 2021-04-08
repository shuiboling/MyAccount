package com.reihiei.firstapp.util;

import android.content.Context;

import com.reihiei.firstapp.db.DbUtils;

public class CommonUtils {

    public static String getClassText(int classify, Context mContext) {
        if(classify == 0){
            return "理财";
        }else if(classify == 1){
            return "基金";
        }else {
            return DbUtils.getInstance(mContext).queryTagById(classify+"").getName();
        }
    }
}
