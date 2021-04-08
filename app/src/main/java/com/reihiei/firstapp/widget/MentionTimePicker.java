package com.reihiei.firstapp.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.reihiei.firstapp.R;

public class MentionTimePicker {
    private Context context;
    private boolean isAllDay;

    public MentionTimePicker(Context context, boolean isAllDay) {
        this.context = context;
        this.isAllDay = isAllDay;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.mention_time_pickcer_layout, null);

        TextView textView = view.findViewById(R.id.text);
        textView.setText("提醒时间");

        Button cancel = view.findViewById(R.id.cancel);
        Button confirm = view.findViewById(R.id.confirm);

        MentionPickerView dateAndTimeView,goneV;
        if (isAllDay) {
            dateAndTimeView = view.findViewById(R.id.wheelA);
            view.findViewById(R.id.wheel).setVisibility(View.GONE);
        } else {
            dateAndTimeView = view.findViewById(R.id.wheel);
            view.findViewById(R.id.wheelA).setVisibility(View.GONE);
        }

        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(view).create();
        alertDialog.show();
        alertDialog.setCancelable(false);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnClickListener(dateAndTimeView.getTime());
                    ;
                }
                alertDialog.dismiss();

            }
        });

    }

    public OnClickListener listener;

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    public interface OnClickListener {
        void OnClickListener(String time);
    }
}
