package com.reihiei.firstapp.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.reihiei.firstapp.R;

public class MyDatePicker {
    private Context context;

    public MyDatePicker(Context context,onPickerClickListener listener){
        this.context = context;
        this.listener = listener;

    }

    public void showDataPicker(int year,int month,int day,boolean showDay){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.datepicker_layout,null);
        builder.setView(v);
        DatePicker datePicker = v.findViewById(R.id.datepick);
        datePicker.init(year,month,day,null);

        TextView confirm = v.findViewById(R.id.confirm);
        TextView cancel = v.findViewById(R.id.cancel);
        int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
        if (daySpinnerId != 0) {
            View daySpinner = datePicker.findViewById(daySpinnerId);
            if (daySpinner != null && !showDay) {
                daySpinner.setVisibility(View.GONE);
            }
        }
        AlertDialog dialog = builder.create();
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null){
                    listener.onConfirmClick(datePicker);
                }
                dialog.dismiss();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private onPickerClickListener listener;

    public interface onPickerClickListener{
        public void onConfirmClick(DatePicker datePicker);
    }

}
