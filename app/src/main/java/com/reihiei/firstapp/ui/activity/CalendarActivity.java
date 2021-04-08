package com.reihiei.firstapp.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.db.DbUtils;
import com.reihiei.firstapp.framework.SimpleActivity;
import com.reihiei.firstapp.util.CommonDialogUtils;
import com.reihiei.firstapp.util.DateUtils;
import com.reihiei.firstapp.widget.CommonDialog;
import com.reihiei.firstapp.widget.DateAndTimePicker;
import com.reihiei.firstapp.widget.MyDatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

public class CalendarActivity extends SimpleActivity {

    @BindView(R.id.et_title)
    EditText etTitle;
    @BindView(R.id.et_desc)
    EditText etDesc;
    @BindView(R.id.mSwitch)
    Switch mSwitch;
    @BindView(R.id.tvStartTime)
    TextView tvStartTime;
    @BindView(R.id.tvEndTime)
    TextView tvEndTime;
    @BindView(R.id.mention)
    TextView mention;
    @BindView(R.id.writeEventButton)
    Button write;
    @BindView(R.id.delEventButton)
    Button del;
    @BindView(R.id.updateEventButton)
    Button update;
    @BindView(R.id.tv_title)
    TextView title;

    private boolean isAllDay = false;
    private int year, month, day;
    private int chooseSYear, chooseSMonth, chooseSDay, chooseSHour, chooseSMinute;
    private int chooseEYear, chooseEMonth, chooseEDay, chooseEHour, chooseEMinute;
    private long startTimeStamp, endTimeStamp, startATimeStamp, endATimeStamp;

    private static final String MY_ACCOUNT_NAME = "SHUIBOLING";

    private String addtime;
    private long eventId;

    private List<String> list = new ArrayList<>();
    private List<Integer> count = new ArrayList<>();
    private boolean isMention = true;
    private List<Long> mentionTimes = new ArrayList<>();

    private static int resultCode1 = 101;
    private static int resultCode2 = 102;

    @Override
    protected int getLayout() {
        return R.layout.activity_calendar;
    }

    @Override
    protected void initEventAndView() {

        title.setText("提醒事项");

        addtime = getIntent().getStringExtra("addtime");
        eventId = getIntent().getLongExtra("eventId", -1);

        etTitle.setText(getIntent().getStringExtra("title"));
        etDesc.setText(getIntent().getStringExtra("remark"));
        startTimeStamp = getIntent().getLongExtra("startTimeStamp",0);
        endTimeStamp = getIntent().getLongExtra("endTimeStamp",0);
        list = getIntent().getStringArrayListExtra("list");

        if (list != null && !list.isEmpty()){
            setTime();
            resolveData(list);
            for (int i=0;i<list.size();i++){
                count.add(i);
            }
        } else {
            initPickerDate();
        }

        initPermission();

        if (eventId != -1) {
            queryCalendar(eventId);
            del.setVisibility(View.GONE);
            update.setVisibility(View.VISIBLE);
            write.setVisibility(View.GONE);
        } else {
            del.setVisibility(View.GONE);
            update.setVisibility(View.GONE);
            write.setVisibility(View.VISIBLE);
        }

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isAllDay = true;
                    tvStartTime.setText(year + "年" + chooseSMonth + "月" + chooseSDay + "日");
                    tvEndTime.setText(year + "年" + chooseEMonth + "月" + chooseEDay + "日");
                    list.clear();
                    count.clear();
                    isMention = false;
                    mentionTimes.clear();
                    mention.setText("");

                } else {
                    isAllDay = false;
                    tvStartTime.setText(year + "年" + chooseSMonth + "月" + chooseSDay + "日" + chooseSHour + "时" + chooseSMinute + "分");
                    tvEndTime.setText(year + "年" + chooseEMonth + "月" + chooseEDay + "日" + chooseEHour + "时" + chooseEMinute + "分");
                    list.clear();
                    count.clear();
                    isMention = false;
                    mentionTimes.clear();
                    mention.setText("");

                }
            }
        });

    }

    private void initPermission() {
        List<String> permissionsList = new ArrayList<>();

        if ((checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED)) {
            permissionsList.add(Manifest.permission.READ_CALENDAR);
        }
        if ((checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED)) {
            permissionsList.add(Manifest.permission.WRITE_CALENDAR);
        }
        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]), 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length != 0){
            createNewCalendar();

        } else {
            CommonDialogUtils.showErrorDialog(mContext, "请在手机设置中开启权限后使用该功能", new CommonDialogUtils.DismissListener() {
                @Override
                public void dismiss() {
                    finish();
                }
            });
        }


    }

    private void initPickerDate() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DATE);

        chooseSMonth = month + 1;
        chooseSDay = day;
        chooseSHour = calendar.get(Calendar.HOUR_OF_DAY);
        chooseSMinute = calendar.get(Calendar.MINUTE);

        chooseEMonth = month + 1;
        chooseEDay = day;
        chooseEHour = calendar.get(Calendar.HOUR_OF_DAY);
        chooseEMinute = calendar.get(Calendar.MINUTE);

        startTimeStamp = DateUtils.dateToStamp(year + "-" + chooseSMonth + "-" + chooseSDay + " " + chooseSHour + ":" + chooseSMinute, "yyyy-MM-dd HH:mm");
        endTimeStamp = DateUtils.dateToStamp(year + "-" + chooseEMonth + "-" + chooseEDay + " " + chooseEHour + ":" + chooseEMinute, "yyyy-MM-dd HH:mm");

        startATimeStamp = DateUtils.dateToStamp(year + "-" + chooseSMonth + "-" + chooseSDay, "yyyy-MM-dd");
        endATimeStamp = DateUtils.dateToStamp(year + "-" + chooseEMonth + "-" + chooseEDay, "yyyy-MM-dd");

        tvStartTime.setText(year + "年" + chooseSMonth + "月" + chooseSDay + "日" + chooseSHour + "时" + chooseSMinute + "分");
        tvEndTime.setText(year + "年" + chooseEMonth + "月" + chooseEDay + "日" + chooseEHour + "时" + chooseEMinute + "分");
    }

    @OnClick({R.id.tvStartTime, R.id.tvEndTime, R.id.writeEventButton, R.id.mention, R.id.updateEventButton
            , R.id.delEventButton,R.id.iv_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tvStartTime:
                if (isAllDay) {
                    MyDatePicker myDatePicker = new MyDatePicker(mContext, picker -> {
                        chooseSMonth = picker.getMonth() + 1;
                        chooseSDay = picker.getDayOfMonth();

                        startATimeStamp = DateUtils.dateToStamp(year + "-" + chooseSMonth + "-" + chooseSDay, "yyyy-MM-dd");
                        tvStartTime.setText(year + "年" + chooseSMonth + "月" + chooseSDay + "日");

                        if (startATimeStamp > endATimeStamp) {
                            chooseEMonth = chooseSMonth;
                            chooseEDay = chooseSDay;
                            endATimeStamp = startATimeStamp;
                            tvEndTime.setText(year + "年" + chooseEMonth + "月" + chooseEDay + "日");
                        }
                    });
                    myDatePicker.showDataPicker(year, month, day, true);
                } else {
                    new DateAndTimePicker(mContext, "开始时间").setOnClickListener(picker -> {
                        chooseSYear = picker.getYear();
                        chooseSMonth = picker.getMonth();
                        chooseSDay = picker.getDay();
                        chooseSHour = picker.getHour();
                        chooseSMinute = picker.getMinute();

                        startTimeStamp = DateUtils.dateToStamp(chooseSYear + "-" + chooseSMonth + "-" + chooseSDay + " " + chooseSHour + ":" + chooseSMinute, "yyyy-MM-dd HH:mm");
                        tvStartTime.setText(chooseSYear + "年" + chooseSMonth + "月" + chooseSDay + "日" + chooseSHour + "时" + chooseSMinute + "分");

                        if (startTimeStamp > endTimeStamp) {
                            chooseEYear = chooseSYear;
                            chooseEMonth = chooseSMonth;
                            chooseEDay = chooseSDay;
                            chooseEHour = chooseSHour;
                            chooseEMinute = chooseSMinute;
                            endTimeStamp = startTimeStamp;
                            tvEndTime.setText(chooseEYear + "年" + chooseEMonth + "月" + chooseEDay + "日" + chooseEHour + "时" + chooseEMinute + "分");
                        }

                    });
                }
                break;
            case R.id.tvEndTime:
                if (isAllDay) {
                    MyDatePicker myDatePicker = new MyDatePicker(mContext, new MyDatePicker.onPickerClickListener() {
                        @Override
                        public void onConfirmClick(DatePicker picker) {

                            chooseEMonth = picker.getMonth() + 1;
                            chooseEDay = picker.getDayOfMonth();

                            endATimeStamp = DateUtils.dateToStamp(year + "-" + chooseEMonth + "-" + chooseEDay, "yyyy-MM-dd");

                            if (endATimeStamp < startATimeStamp) {
                                chooseEMonth = chooseSMonth;
                                chooseEDay = chooseSDay;
                                endATimeStamp = startATimeStamp;
                            }
                            tvEndTime.setText(year + "年" + chooseEMonth + "月" + chooseEDay + "日");

                        }
                    });
                    myDatePicker.showDataPicker(year, month, day, true);
                } else {
                    new DateAndTimePicker(mContext, "结束时间").setOnClickListener(picker -> {
                        chooseEYear = picker.getYear();
                        chooseEMonth = picker.getMonth();
                        chooseEDay = picker.getDay();
                        chooseEHour = picker.getHour();
                        chooseEMinute = picker.getMinute();

                        endTimeStamp = DateUtils.dateToStamp(chooseEYear + "-" + chooseEMonth + "-" + chooseEDay + " " + chooseEHour + ":" + chooseEMinute, "yyyy-MM-dd HH:mm");
                        if (endTimeStamp < startTimeStamp) {
                            chooseEYear = chooseSYear;
                            chooseEMonth = chooseSMonth;
                            chooseEDay = chooseSDay;
                            chooseEHour = chooseSHour;
                            chooseEMinute = chooseSMinute;
                            endTimeStamp = startTimeStamp;
                        }
                        tvEndTime.setText(chooseEYear + "年" + chooseEMonth + "月" + chooseEDay + "日" + chooseEHour + "时" + chooseEMinute + "分");

                    });
                }
                break;
            case R.id.mention:
                Intent intent = new Intent(mContext, AlertSettingActivity.class);
                intent.putExtra("isAllDay", isAllDay);
                intent.putExtra("isMention", isMention);
                intent.putIntegerArrayListExtra("count", (ArrayList<Integer>) count);
                intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                startActivityForResult(intent, 100);
                break;
            case R.id.writeEventButton:
                new CommonDialog(mContext, "是否新增提醒", 0, new CommonDialog.OnClickBtnListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            insertCalendar();
                        }
                        dialog.dismiss();
                        finish();
                    }
                }).setCancelTxt("取消").setSubmitTxt("确定").show();
                break;
            case R.id.updateEventButton:
                new CommonDialog(mContext, "是否更新提醒", 0, new CommonDialog.OnClickBtnListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            updateCalendar();
                        }
                        dialog.dismiss();
                    }
                }).setCancelTxt("取消").setSubmitTxt("确定").show();
                break;
            case R.id.delEventButton:
                new CommonDialog(mContext, "是否删除提醒", 0, new CommonDialog.OnClickBtnListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {
                            delCalendar(eventId);
                        }
                        dialog.dismiss();
                        finish();
                    }
                }).setCancelTxt("取消").setSubmitTxt("确定").show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100) {
            if (resultCode == resultCode1) {

                isMention = true;
                count = data.getIntegerArrayListExtra("count");
                list = data.getStringArrayListExtra("list");


                List<String> times = data.getStringArrayListExtra("times");
                resolveData(times);
                String time = "";
                for (String i : times) {
                    time = time + i + ";";
                }
                mention.setText(time);
                convertTimeToMention(times);
            } else if (resultCode == resultCode2) {
                isMention = false;
                mention.setText("");
                mentionTimes.clear();
            }
        }
    }

    private void resolveData(List<String> times ) {
        String time = "";
        for (String i : times) {
            time = time + i + ";";
        }
        mention.setText(time);
        convertTimeToMention(times);
    }

    private void insertCalendar() {
        //直接插入
        long calID = getCalendarId();

        long startMillis = 0;
        long endMillis = 0;

        if (isAllDay) {
            startMillis = startATimeStamp;
            endMillis = endATimeStamp;

        } else {
            startMillis = startTimeStamp;
            endMillis = endTimeStamp;
        }

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, startMillis);
        values.put(Events.DTEND, endMillis);
        values.put(Events.TITLE, etTitle.getText().toString());
        values.put(Events.DESCRIPTION, etDesc.getText().toString());//备注
        values.put(Events.CALENDAR_ID, calID);
        values.put(Events.EVENT_TIMEZONE, "Asia/Shanghai");
        values.put(Events.HAS_ALARM, true);
        Uri uri = cr.insert(Events.CONTENT_URI, values);

        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(uri.getLastPathSegment());
        Log.d("zyy", eventID + ":");

        for (int i = 0; i < mentionTimes.size(); i++) {
            ContentResolver contentResolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CalendarContract.Reminders.MINUTES, mentionTimes.get(i));
            contentValues.put(CalendarContract.Reminders.EVENT_ID, eventID);
            //部分设备只支持alert和default
            contentValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALARM);
            contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, contentValues);
        }
//             ... do something with event ID
//        if (uri1 == null) {
////                // 添加闹钟提醒失败直接返回
//            Toast.makeText(this, "插入事件失败!!!", Toast.LENGTH_LONG).show();
//            return;
//        }
        DbUtils.getInstance(mContext).updateEventId(eventID, addtime);
        Toast.makeText(this, "插入事件成功!!!", Toast.LENGTH_LONG).show();
    }

    public void queryCalendar(long eventId) {

        String selection = Events._ID + " = ?";
        String[] selectionArgs = new String[]{eventId + ""};
        Cursor cur = getContentResolver().query(Events.CONTENT_URI, null, selection, selectionArgs, null);
        if (cur != null) {
            cur.moveToFirst();
            etTitle.setText(cur.getString(cur.getColumnIndex(Events.TITLE)));
            etDesc.setText(cur.getString(cur.getColumnIndex(Events.DESCRIPTION)));
            startTimeStamp = cur.getLong(cur.getColumnIndex(Events.DTSTART));
            endTimeStamp = cur.getLong(cur.getColumnIndex(Events.DTEND));

            setTime();

        }
        cur.close();

        selection = CalendarContract.Reminders.EVENT_ID + "=?";
        Cursor cur2 = getContentResolver().query(CalendarContract.Reminders.CONTENT_URI, null, selection, selectionArgs, CalendarContract.Reminders.MINUTES);
        mentionTimes.clear();
        while (cur2.moveToNext()) {
            mentionTimes.add(cur2.getLong(cur2.getColumnIndex(CalendarContract.Reminders.MINUTES)));
        }
        setMention();
        cur2.close();
    }

    private void delCalendar(long eventId) {
        getContentResolver().delete(CalendarContract.Reminders.CONTENT_URI, CalendarContract.Reminders.EVENT_ID+"="+eventId, null);
        Uri deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
        getContentResolver().delete(deleteUri, null, null);
        DbUtils.getInstance(mContext).updateEventId(-1, addtime);

    }

    private void updateCalendar() {

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        Uri updateUri = null;

        values.put(Events.TITLE, etTitle.getText().toString());
        values.put(Events.DESCRIPTION, etDesc.getText().toString());
        values.put(Events.DTSTART, startTimeStamp);
        values.put(Events.DTEND, endTimeStamp);
        updateUri = ContentUris.withAppendedId(Events.CONTENT_URI, eventId);
        cr.update(updateUri, values, null, null);

        getContentResolver().delete(CalendarContract.Reminders.CONTENT_URI, CalendarContract.Reminders.EVENT_ID+"="+eventId, null);

        for (int i = 0; i < mentionTimes.size(); i++) {
            ContentResolver contentResolver = getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(CalendarContract.Reminders.MINUTES, mentionTimes.get(i));
            contentValues.put(CalendarContract.Reminders.EVENT_ID, eventId);
            //部分设备只支持alert和default
            contentValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALARM);
            contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, contentValues);
        }

        CommonDialogUtils.showErrorDialog(mContext,"更新成功",null);

    }

    private long getCalendarId() {
        String[] projection = new String[]{CalendarContract.Calendars._ID};
        String selection = CalendarContract.Calendars.ACCOUNT_NAME + " = ? ";
        String[] selArgs = new String[]{MY_ACCOUNT_NAME};
        Cursor cursor = getContentResolver().query(
                CalendarContract.Calendars.CONTENT_URI,
                projection,
                selection,
                selArgs,
                null);
        if (cursor != null) {
            if (cursor.moveToFirst()) return cursor.getLong(0);

            cursor.close();
        }
        return -1;
    }

    //    添加账户
    private void createNewCalendar() {
        long calendarId = getCalendarId();
        if (calendarId != -1) {
            return;
        }

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Calendars.ACCOUNT_NAME, MY_ACCOUNT_NAME);
        values.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(CalendarContract.Calendars.NAME, "MY_ACCOUNT_CALENDAR");
        values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "书契");
        values.put(CalendarContract.Calendars.CALENDAR_COLOR, getResources().getColor(R.color.color_light_pink, null));
        values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(CalendarContract.Calendars.OWNER_ACCOUNT, MY_ACCOUNT_NAME);
        values.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, "Asia/Shanghai");
        values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        values.put(CalendarContract.Calendars.VISIBLE, 1);

        //同步适配器，同步适配器拥有写入权限的列更多
        Uri.Builder builder = CalendarContract.Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, MY_ACCOUNT_NAME);
        builder.appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true");

        Uri uri = getContentResolver().insert(builder.build(), values);

        Log.d("zyy", "createNewCalendar: " + uri);
    }

    private void setMention() {
        if (!mentionTimes.isEmpty()) {
            count.clear();
            list.clear();
            String txt = "";
            for (int i = 0; i < mentionTimes.size(); i++) {
                count.add(i);
                String tmp = convertMentionToTime(mentionTimes.get(i));
                txt = txt + tmp + ";";
                list.add(tmp);
            }
            mention.setText(txt);

        }
    }

    private String convertMentionToTime(Long aLong) {
        if (aLong / 60 == 0) {
            if (aLong == 0) {
                return "开始时";
            }
            return "提前" + aLong + "分钟";
        } else {
            aLong /= 60;
        }
        if (aLong / 24 == 0) {
            return "提前" + aLong + "小时";
        } else {
            return "提前" + (aLong / 24) + "天";
        }

    }

    private void setTime() {
        Date dateS = new Date(startTimeStamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateS);

        chooseSYear = calendar.get(Calendar.YEAR);
        chooseSMonth = calendar.get(Calendar.MONTH) + 1;
        chooseSDay = calendar.get(Calendar.DATE);
        chooseSHour = calendar.get(Calendar.HOUR_OF_DAY);
        chooseSMinute = calendar.get(Calendar.MINUTE);
        tvStartTime.setText(chooseSYear + "年" + chooseSMonth + "月" + chooseSDay + "日" + chooseSHour + "时" + chooseSMinute + "分");

        Date dateE = new Date(endTimeStamp);
        calendar.setTime(dateE);
        chooseEYear = calendar.get(Calendar.YEAR);
        chooseEMonth = calendar.get(Calendar.MONTH) + 1;
        chooseEDay = calendar.get(Calendar.DATE);
        chooseEHour = calendar.get(Calendar.HOUR_OF_DAY);
        chooseEMinute = calendar.get(Calendar.MINUTE);
        tvEndTime.setText(chooseEYear + "年" + chooseEMonth + "月" + chooseEDay + "日" + chooseEHour + "时" + chooseEMinute + "分");
    }

    public void convertTimeToMention(List<String> times) {

        mentionTimes.clear();

        if (!isAllDay) {
            for (String time : times) {

                if ("开始时".equals(time)) {
                    mentionTimes.add(0l);
                } else {
                    long num = Long.parseLong(getNumeric(time));
                    if (time.contains("分钟")) {
                        mentionTimes.add(num);
                    } else if (time.contains("小时")) {
                        mentionTimes.add(num * 60);
                    } else {
                        mentionTimes.add(num * 24 * 60);
                    }
                }
            }
        }
    }

    public String getNumeric(String str) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
