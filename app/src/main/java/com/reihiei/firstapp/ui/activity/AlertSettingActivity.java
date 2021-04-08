package com.reihiei.firstapp.ui.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.framework.SimpleActivity;
import com.reihiei.firstapp.ui.apater.AlertSettingAdapter;
import com.reihiei.firstapp.widget.DateAndTimePicker;
import com.reihiei.firstapp.widget.DateAndTimeView;
import com.reihiei.firstapp.widget.MentionTimePicker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

public class AlertSettingActivity extends SimpleActivity {

    @BindView(R.id.rc_list)
    RecyclerView recyclerView;
    @BindView(R.id.mSwitch)
    Switch aSwitch;
    @BindView(R.id.edit)
    RelativeLayout relativeLayout;
    @BindView(R.id.time)
    RelativeLayout rlTime;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private AlertSettingAdapter alertSettingAdapter;
    private List<String> list;
    private List<Integer> count;

    private boolean isAllDay;

    private static int resultCode1 = 101;
    private static int resultCode2 = 102;

    @Override
    protected int getLayout() {
        return R.layout.activity_alert_setting;
    }

    @Override
    protected void initEventAndView() {

        tvTitle.setText("提醒设置");

        isAllDay = getIntent().getBooleanExtra("isAllDay", false);
        count = getIntent().getIntegerArrayListExtra("count");
        list = getIntent().getStringArrayListExtra("list");
        boolean isMention = getIntent().getBooleanExtra("isMention", true);

        if (list == null || list.isEmpty()) {
            list = new ArrayList<>();

            if (isAllDay) {
                list.add("当天");
                list.add("提前1天");
                list.add("提前2天");
                list.add("提前3天");
            } else {
                list.add("开始时");
                list.add("提前2分钟");
                list.add("提前5分钟");
                list.add("提前10分钟");
                list.add("提前1小时");
                list.add("提前1天");

            }
        }

        if (isAllDay) {
            rlTime.setVisibility(View.VISIBLE);

        } else {
            rlTime.setVisibility(View.GONE);

        }

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    recyclerView.setClickable(false);
                    recyclerView.setAlpha(0.5f);
                    relativeLayout.setClickable(false);
                    relativeLayout.setAlpha(0.5f);
                    tvTime.setClickable(false);
                    tvTime.setAlpha(0.5f);
                } else {
                    recyclerView.setClickable(true);
                    recyclerView.setAlpha(1);
                    relativeLayout.setClickable(true);
                    relativeLayout.setAlpha(1);
                    tvTime.setClickable(true);
                    tvTime.setAlpha(1);

                }
            }
        });

        alertSettingAdapter = new AlertSettingAdapter(mContext, list, count);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(alertSettingAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    @OnClick({R.id.edit, R.id.time, R.id.iv_back})
    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.edit:
                new MentionTimePicker(mContext, isAllDay).setOnClickListener(time -> {
                    if (!list.contains(time)) {
                        list.add(time);
                        alertSettingAdapter.updateList(list);
                    } else {
                        Toast.makeText(mContext, "已存在", Toast.LENGTH_SHORT).show();
                    }
                });
                break;

            case R.id.time:
                new DateAndTimePicker(mContext, "提醒时间").setOnClickListener(new DateAndTimePicker.OnClickListener() {
                    @Override
                    public void OnClickListener(DateAndTimeView picker) {
                        tvTime.setText(picker.getHour() + ":" + picker.getMinute());
                    }
                });
                break;
            case R.id.iv_back:
                back();
                break;
        }
    }

    public List<String> getTimes(List<Integer> counts) {
        List<String> times = new ArrayList<>();
        Collections.sort(counts);
        for (int i : counts) {

            String time = list.get(i);
            times.add(time);

        }
        return times;
    }

    @Override
    public void onBackPressed() {
        back();
        super.onBackPressed();
    }

    public void back(){
        if (aSwitch.isChecked()) {
            setResult(resultCode2);
        } else {
            List<Integer> counts = alertSettingAdapter.getChoose();

            Intent intent = new Intent();
            intent.putStringArrayListExtra("times", (ArrayList<String>) getTimes(counts));
            intent.putStringArrayListExtra("list", (ArrayList<String>) list);
            intent.putIntegerArrayListExtra("count", (ArrayList<Integer>) counts);
            if (isAllDay) {
                intent.putExtra("count", tvTime.getText().toString());

            }
            setResult(resultCode1, intent);
        }
        finish();
    }
}
