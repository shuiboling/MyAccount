package com.reihiei.firstapp.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.TagBean;
import com.reihiei.firstapp.db.DbUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LineChart extends LinearLayout {

    private List<Float> ratios;
    private TypedArray icons;
    private String[] names;
    private List<TagBean> tagBeans;
    private List<String> nameList;
    private BigDecimal sumOut;

    public LineChart(Context context) {
        super(context);
        init(context);

    }

    public LineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public LineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }

    public LineChart(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void init(Context mContext){
        names = getResources().getStringArray(R.array.out_type_name);
        icons = getResources().obtainTypedArray(R.array.out_type_icon);

        tagBeans = DbUtils.getInstance(mContext).queryByType(0);
        nameList = new ArrayList<>(Arrays.asList(names));  //Arrays.asList不能修改
        for (TagBean bean : tagBeans) {
            nameList.add(bean.getName());
        }
    }

    public void addView(View child, float ratio){
        if(ratios == null){
            ratios = new ArrayList<>();
        }
        ratios.add(ratio);
        addView(child);

    }

    private int changeWidth = 0;
    private int lineWidth = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec,heightMeasureSpec);

        int paddingLeft= 0,paddingRight= 0,marginLeft= 0,marginRight= 0,width = 0;
        for(int i =0;i<getChildCount();i++){
            View child =  getChildAt(i);

            width = child.getMeasuredWidth();

            paddingLeft = child.getPaddingLeft();
            paddingRight = child.getPaddingRight();

//            TextView name = child.findViewById(R.id.name);
            TextView line = child.findViewById(R.id.line);
            TextView change = child.findViewById(R.id.change);
            TextView money = child.findViewById(R.id.money);

            marginLeft = ((MarginLayoutParams)line.getLayoutParams()).leftMargin;
            marginRight = ((MarginLayoutParams)line.getLayoutParams()).rightMargin;

            if(change.getMeasuredWidth() > changeWidth){
                changeWidth = change.getMeasuredWidth();
            }
            if(money.getMeasuredWidth() > changeWidth){
                changeWidth = money.getMeasuredWidth();
            }
        }
        lineWidth = width - paddingLeft - paddingRight-marginLeft-marginRight-changeWidth;

        for(int i = 0;i<getChildCount();i++){
            View child =  getChildAt(i);
            TextView line = child.findViewById(R.id.line);
            TextView money = child.findViewById(R.id.money);

            ViewGroup.LayoutParams moneyParams = (ViewGroup.LayoutParams) money.getLayoutParams();
            moneyParams.width =changeWidth;
            money.setLayoutParams(moneyParams);

            ViewGroup.LayoutParams lineParams = (ViewGroup.LayoutParams) line.getLayoutParams();
            lineParams.width = (int)(((float)lineWidth)*ratios.get(i));
            line.setLayoutParams(lineParams);

        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
