package com.reihiei.firstapp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.LongSparseArray;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.reihiei.firstapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MentionPickerView extends LinearLayout {

    private List<String> hours = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"
            , "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
    private List<String> minutes = new ArrayList<>();
    private List<String> days = new ArrayList<>();
    private List<String> units = new ArrayList<>();

    private TextView textView;
    private WheelView firstWheel;
    private WheelView unitWheel;

    private boolean isAllDay = false;

    private int firstIndex = 0, unitIndex = 0;

    public MentionPickerView(Context context, boolean isAllDay) {
        super(context);
        init();
        this.isAllDay = isAllDay;
    }

    public MentionPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getResources().obtainAttributes(attrs, R.styleable.MentionPickerView);
        isAllDay = typedArray.getBoolean(R.styleable.MentionPickerView_isAllAay, false);
        init();

    }

    public MentionPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public MentionPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();

    }

    private void init() {
        setOrientation(VERTICAL);

        if (isAllDay) {
            units.add("天");
        } else {
            units.add("小时");
            units.add("天");
            units.add("分钟");

        }
        getHoursAndMinutes();

        addText();
        addWheel();

    }

    private void getHoursAndMinutes() {
        for (int i = 1; i < 60; i++) {
            minutes.add(i + "");
        }
        for (int i = 1; i <= 30; i++) {
            days.add(i + "");
        }
    }


    private void addText() {
        textView = new TextView(getContext());
        textView.setText("提前");
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

        if (isAllDay) {
            firstWheel = new WheelView(getContext(), days, "");

        } else {
            firstWheel = new WheelView(getContext(), hours, "");

        }
        LayoutParams layoutParamsDate = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        firstWheel.setWheelInterface(new WheelView.WheelInterface() {
            @Override
            public void getIndex(int index) {
                firstIndex = index;

                String time;
                if (isAllDay){
                    time = days.get(firstIndex);
                } else if (unitIndex == 0) {
                    time = hours.get(firstIndex);
                } else if (unitIndex == 1) {
                    time = days.get(firstIndex);
                } else {
                    time = minutes.get(firstIndex);
                }
                textView.setText("提前" + time + units.get(unitIndex));
            }
        });

        unitWheel = new WheelView(getContext(), units, "");
        LayoutParams layoutParamsHour = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        unitWheel.setWheelInterface((i) -> {
            unitIndex = i;
            firstIndex = 0;
            String time;
            if (unitIndex == 0) {
                time = hours.get(firstIndex);
                firstWheel.changeList(hours);
            } else if (unitIndex == 1) {
                time = days.get(firstIndex);
                firstWheel.changeList(days);
            } else {
                time = minutes.get(firstIndex);
                firstWheel.changeList(minutes);
            }
            textView.setText("提前" + time + units.get(unitIndex));

        });

        linearLayout.addView(firstWheel, layoutParamsDate);
        linearLayout.addView(unitWheel, layoutParamsHour);

        addView(linearLayout);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public String getTime() {
        String time;
        if (isAllDay){
            time = days.get(firstIndex);
        } else if (unitIndex == 0) {
            time = hours.get(firstIndex);
        } else if (unitIndex == 1) {
            time = days.get(firstIndex);
        } else {
            time = minutes.get(firstIndex);
        }
        return ("提前" + time + units.get(unitIndex));
    }

//    public int getMonth(){
//        return Integer.valueOf(dates.get(firstIndex).substring(0,2));
//    }
//
//    public int getDay(){
//        return Integer.valueOf(dates.get(firstIndex).substring(3,5));
//    }
//
//    public int getHour(){
//        return Integer.valueOf(hours.get(unitIndex));
//    }
//
//    public int getMinute(){
//        return Integer.valueOf(minutes.get(minuteIndex));
//    }

}
