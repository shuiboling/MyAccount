package com.reihiei.firstapp.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    /*
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String s, String format) {

        try {
            String res;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            Date date = null;
            date = simpleDateFormat.parse(s);
            long ts = date.getTime();
            return ts;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /*
     * 将时间转换为时间戳
     */
    public static String stampToDate(long time) {

        Date date = new Date(time);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return simpleDateFormat.format(date);

    }

}
