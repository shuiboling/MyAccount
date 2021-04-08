package com.reihiei.firstapp.ui.activity;

import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.TextView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.db.DbUtils;
import com.reihiei.firstapp.framework.SimpleActivity;
import com.reihiei.firstapp.util.DateUtils;
import com.reihiei.firstapp.widget.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ShowMentionActivity extends SimpleActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.remark)
    TextView remark;
    @BindView(R.id.mention)
    TextView mention;

    private long startTimeStamp,endTimeStamp;
    private long eventId;
    private List<Long> mentionTimes = new ArrayList<>();
    private String addtime;
    private List<String> list = new ArrayList<>();

    @Override
    protected int getLayout() {
        return R.layout.activity_show_mention;
    }

    @Override
    protected void initEventAndView() {

        addtime = getIntent().getStringExtra("addtime");
        eventId = getIntent().getLongExtra("eventId", -1);
        queryCalendar(eventId);
    }

    public void queryCalendar(long eventId) {

        String selection = CalendarContract.Events._ID + " = ?";
        String[] selectionArgs = new String[]{eventId + ""};
        Cursor cur = getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, selection, selectionArgs, null);
        if (cur != null) {
            cur.moveToFirst();
            title.setText(cur.getString(cur.getColumnIndex(CalendarContract.Events.TITLE)));
            remark.setText(cur.getString(cur.getColumnIndex(CalendarContract.Events.DESCRIPTION)));
            startTimeStamp = cur.getLong(cur.getColumnIndex(CalendarContract.Events.DTSTART));
            endTimeStamp = cur.getLong(cur.getColumnIndex(CalendarContract.Events.DTEND));
            time.setText(DateUtils.stampToDate(startTimeStamp)+"-"+DateUtils.stampToDate(endTimeStamp));
        }
        cur.close();

        selection = CalendarContract.Reminders.EVENT_ID + "=?";
        Cursor cur2 = getContentResolver().query(CalendarContract.Reminders.CONTENT_URI, null, selection, selectionArgs, CalendarContract.Reminders.MINUTES);
        mentionTimes.clear();
        while (cur2.moveToNext()) {
            mentionTimes.add(cur2.getLong(cur2.getColumnIndex(CalendarContract.Reminders.MINUTES)));
            setMention();
        }
        cur2.close();
    }

    @OnClick({R.id.close})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.close:
                finish();
                break;
            case R.id.edit:
                Intent intent = new Intent(mContext, CalendarActivity.class);
                intent.putExtra("addtime", addtime);
                intent.putExtra("eventId",eventId);
                intent.putExtra("title",title.getText().toString());
                intent.putExtra("remark",remark.getText().toString());
                intent.putExtra("startTimeStamp",startTimeStamp);
                intent.putExtra("endTimeStamp",endTimeStamp);
                intent.putStringArrayListExtra("list", (ArrayList<String>) list);
                mContext.startActivity(intent);
                break;
            case R.id.del:
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

    private void delCalendar(long eventId) {
        getContentResolver().delete(CalendarContract.Reminders.CONTENT_URI, CalendarContract.Reminders.EVENT_ID+"="+eventId, null);
        Uri deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        getContentResolver().delete(deleteUri, null, null);
        DbUtils.getInstance(mContext).updateEventId(-1, addtime);

    }

    private void setMention() {
        list.clear();
        if (!mentionTimes.isEmpty()) {
            String txt = "";
            for (int i = 0; i < mentionTimes.size(); i++) {
                String tmp = convertMentionToTime(mentionTimes.get(i));
                list.add(tmp);
                txt = txt + tmp + ";";
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

}
