package com.reihiei.firstapp.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.reihiei.firstapp.R;

public class DateAndTimePicker {

    private Context context;
    private String title;

    public DateAndTimePicker(Context context,String title){
        this.context = context;
        this.title = title;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.date_time_picker_layout,null);

        TextView textView = view.findViewById(R.id.text);
        textView.setText(title);

        Button cancel = view.findViewById(R.id.cancel);
        Button confirm = view.findViewById(R.id.confirm);

        DateAndTimeView dateAndTimeView = view.findViewById(R.id.wheel);
//        dateAndTimeView.wheelDate.setVisibility(View.GONE);
//        dateAndTimeView.textView.setVisibility(View.GONE);

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
                if(listener != null){
                    listener.OnClickListener(dateAndTimeView.getTime());
                }
                alertDialog.dismiss();

            }
        });

    }

    public OnClickListener listener;
    public void setOnClickListener(OnClickListener listener){
        this.listener = listener;
    }
    public interface OnClickListener{
        void OnClickListener(DateAndTimeView picker);
    }
}
