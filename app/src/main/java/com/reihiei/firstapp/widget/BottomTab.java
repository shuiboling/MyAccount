package com.reihiei.firstapp.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.reihiei.firstapp.R;

public class BottomTab extends RelativeLayout {

    private ImageView icon;
    private TextView title;
    private Context context;
    private int iconSelect;
//    private int iconUnselect;
    private String titleStr;
    private boolean isCenter;

    public BottomTab(Context context,int iconSelect,String titleStr,boolean isCenter) {
        super(context);
        this.context = context;
        this.iconSelect = iconSelect;
//        this.iconUnselect = iconUnselect;
        this.titleStr = titleStr;
        this.isCenter = isCenter;
        init();
    }

    @SuppressLint("ResourceType")
    private void init() {

        icon = new ImageView(context);
        icon.setImageDrawable(context.getDrawable(iconSelect));
        LayoutParams params1;
        if(isCenter){
            params1 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }else {
            params1 = new LayoutParams(56, 56);
        }
        params1.addRule(CENTER_HORIZONTAL);
        icon.setLayoutParams(params1);
        icon.setId(012345);
        addView(icon);

        title = new TextView(context);
        title.setText(titleStr);
        title.setTextSize(10);
        title.setTextColor(ContextCompat.getColor(context,R.color.color_333333));
        LayoutParams params2 = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.addRule(CENTER_HORIZONTAL);
        params2.addRule(BELOW,icon.getId());
        params2.topMargin = 5;
        title.setLayoutParams(params2);
        addView(title);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

        if(selected){
            icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.colorAccent)));
            title.setTextColor(ContextCompat.getColor(context,R.color.colorAccent));
        }else {
            icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.color_333333)));
            title.setTextColor(ContextCompat.getColor(context,R.color.color_333333));
        }
    }
}
