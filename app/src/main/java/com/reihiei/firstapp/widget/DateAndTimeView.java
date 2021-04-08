package com.reihiei.firstapp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.reihiei.firstapp.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateAndTimeView extends LinearLayout {

    private List<String> hoursT = Arrays.asList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09"
            , "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
    private List<String> hours = new ArrayList<>();
    private List<String> minutes = new ArrayList<>();
    private List<String> dates = new ArrayList<>();
    private List<String> years = new ArrayList<>();
    private int year;

    public TextView textView;
    public WheelView wheelYear;
    public WheelView wheelDate;
    private WheelView wheelHour;
    private WheelView wheelMinute;

    private int yearIndex = 0,dateIndex = 0, hourIndex = 0, minuteIndex = 0;

    public DateAndTimeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DateAndTimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DateAndTimeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public DateAndTimeView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);

        getHoursAndMinutes();


        for(int i =0;i<10;i++){
            years.add(year+i+"年");
        }
        getYearDate(years.get(0));


        addText();
        addWheel();

    }

    private void getHoursAndMinutes() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        for (int i = hour; i < 24; i++) {
            hours.add(hoursT.get(i));
        }
        for (int i = 0; i < hour; i++) {
            hours.add(hoursT.get(i));
        }

        int minute = calendar.get(Calendar.MINUTE);
        int minuteTmp = minute;
        int shi = minute / 10;

        if (shi <= 0) {
            while (minute < 10) {
                minutes.add("0" + minute);
                minute++;
            }
        }
        while (minute < 60) {
            minutes.add(minute + "");
            minute++;
        }
        if (minutes.size() != 60) {
            int tmp = 0;
            while (tmp != minuteTmp) {
                if (tmp < 10) {
                    minutes.add("0" + tmp);
                } else {
                    minutes.add(tmp + "");
                }
                tmp++;
            }
        }
    }

    private void getYearDate(String year) {

        try {
            String dateStart = year + "01月01日";
            String dateEnd = year + "12月31日";
            SimpleDateFormat date = new SimpleDateFormat("yyyy年MM月dd日");
            long startTime;
            if(!dates.isEmpty()){
                startTime = date.parse(year+dates.get(dateIndex)).getTime();
            } else {
                startTime = new Date().getTime();
            }

            long endTime = date.parse(dateEnd).getTime();//end
            long day = 1000 * 60 * 60 * 24;
            dates.clear();
            for (long i = startTime; i <= endTime+day; i += day) {
                dates.add(date.format(new Date(i)).substring(5));
            }
            endTime = startTime - day;
            startTime = date.parse(dateStart).getTime();
            for (long i = startTime; i <= endTime; i += day) {
                dates.add(date.format(new Date(i)).substring(5));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void getDaysByYear(int year) {

        Calendar calendar = Calendar.getInstance();

        for (int i = 0; i < 12; i++) {

            calendar.set(year, i, 1);

            int lastDay = calendar.getActualMaximum(Calendar.DATE);

            for (int j = 1; j <= lastDay; j++) {

                String month = "";
                String day = "";

                if (i < 9) {
                    month = "-0" + (i + 1);
                } else {
                    month = "-" + (i + 1);
                }

                if (j < 10) {
                    day = "-0" + j;
                } else {
                    day = "-" + j;
                }

                String date = year + month + day;
                dates.add(date);
            }
        }
    }

    private void addText() {
        textView = new TextView(getContext());
        textView.setText(year + "年");
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(16);
        textView.setTextColor(getResources().getColor(R.color.color_666666, null));
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(0, 15, 0, 15);
        addView(textView, layoutParams);
    }

    private void addWheel() {

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(HORIZONTAL);

        wheelYear = new WheelView(getContext(), years, "");
        LayoutParams layoutParamsYear = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.5f);
        wheelYear.setWheelInterface(new WheelView.WheelInterface() {
            @Override
            public void getIndex(int index) {
                yearIndex = index;
                getYearDate(years.get(index));
                wheelDate.changeList(dates);
                textView.setText(years.get(index) + dates.get(dateIndex) + hours.get(hourIndex) + "时" + minutes.get(minuteIndex) + "分");
            }
        });

        wheelDate = new WheelView(getContext(), dates, "");
        LayoutParams layoutParamsDate = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 2f);
        wheelDate.setWheelInterface(new WheelView.WheelInterface() {
            @Override
            public void getIndex(int index) {
                dateIndex = index;
                textView.setText(years.get(yearIndex)  + dates.get(dateIndex) + hours.get(hourIndex) + "时" + minutes.get(minuteIndex) + "分");
            }
        });

        wheelHour = new WheelView(getContext(), hours, "时");
        LayoutParams layoutParamsHour = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        wheelHour.setWheelInterface((i) -> {
            hourIndex = i;
            textView.setText(years.get(yearIndex)  + dates.get(dateIndex) + hours.get(hourIndex) + "时" + minutes.get(minuteIndex) + "分");

        });

        wheelMinute = new WheelView(getContext(), minutes, "分");
        LayoutParams layoutParamsMinute = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        wheelMinute.setWheelInterface((i) -> {
            minuteIndex = i;
            textView.setText(years.get(yearIndex)  + dates.get(dateIndex) + hours.get(hourIndex) + "时" + minutes.get(minuteIndex) + "分");

        });

        linearLayout.addView(wheelYear, layoutParamsYear);
        linearLayout.addView(wheelDate, layoutParamsDate);
        linearLayout.addView(wheelHour, layoutParamsHour);
        linearLayout.addView(wheelMinute, layoutParamsMinute);

        addView(linearLayout);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public DateAndTimeView getTime(){
        return this;
    }

    public int getMonth(){
        return Integer.valueOf(dates.get(dateIndex).substring(0,2));
    }

    public int getDay(){
        return Integer.valueOf(dates.get(dateIndex).substring(3,5));
    }

    public int getHour(){
        return Integer.valueOf(hours.get(hourIndex));
    }

    public int getMinute(){
        return Integer.valueOf(minutes.get(minuteIndex));
    }

    public int getYear(){
        return Integer.valueOf(years.get(yearIndex).substring(0,4));
    }

}
