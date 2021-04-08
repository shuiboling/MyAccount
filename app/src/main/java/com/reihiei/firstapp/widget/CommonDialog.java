package com.reihiei.firstapp.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.reihiei.firstapp.R;

public class CommonDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private String title = "";
    private String submitTxt = "";
    private String cancelTxt = "";
    private OnClickBtnListener listener;
    private String content = "";

    public CommonDialog(@NonNull Context context) {
        super(context);
        this.context = context;

    }

    public CommonDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;

    }

    protected CommonDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;

    }

    public CommonDialog(@NonNull Context context, int themeResId,OnClickBtnListener listener) {
        super(context, themeResId);
        this.context = context;
        this.listener = listener;

    }

    public CommonDialog(@NonNull Context context, String content,int themeResId,OnClickBtnListener listener) {
        super(context, themeResId);
        this.context = context;
        this.listener = listener;
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_common);

        initView();
    }

    private void initView() {
        TextView contentTxt = findViewById(R.id.content);
        TextView titleTxt = findViewById(R.id.title);
        View horizontalLine = findViewById(R.id.horizontal_line);// 横线
        View verticalLine = findViewById(R.id.vertical_line);// 竖线
        TextView submitBtn = findViewById(R.id.submit);
        TextView cancelBtn = findViewById(R.id.cancel);

        if(!TextUtils.isEmpty(title)){
            titleTxt.setText(title);
        }
        if(!TextUtils.isEmpty(content)){
            contentTxt.setText(content);
        }
        if(!TextUtils.isEmpty(submitTxt)){
            submitBtn.setText(submitTxt);
        }else {
            submitBtn.setVisibility(View.GONE);
            verticalLine.setVisibility(View.GONE);
        }
        if(!TextUtils.isEmpty(cancelTxt)){
            cancelBtn.setText(cancelTxt);
        }else {
            cancelBtn.setVisibility(View.GONE);
            verticalLine.setVisibility(View.GONE);
        }

        cancelBtn.setOnClickListener(this);
        submitBtn.setOnClickListener(this);
    }

    public CommonDialog setTitle(String title){
        this.title = title;
        return this;
    }

    public CommonDialog setContent(String content){
        this.content = content;
        return this;
    }

    public CommonDialog setSubmitTxt(String submitTxt){
        this.submitTxt = submitTxt;
        return this;
    }

    public CommonDialog setCancelTxt(String cancelTxt){
        this.cancelTxt = cancelTxt;
        return this;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                if(listener != null){
                    listener.onClick(this,false);
                }
                dismiss();
                break;
            case R.id.submit:
                if(listener != null){
                    listener.onClick(this,true);
                }
                break;
        }

    }

    public interface OnClickBtnListener{
        public void onClick(Dialog dialog,boolean confirm);
    }
}
