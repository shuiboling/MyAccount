package com.reihiei.firstapp.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.reihiei.firstapp.R;

public class MyBottomWidget extends LinearLayout {

    private onItemClickListener onItemClickListener;
    private int currentSelect = 0;

    public MyBottomWidget(Context context) {
        super(context);
    }

    public MyBottomWidget(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyBottomWidget(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    private int maxHeight=0,minHeight=0;

    public int getMinHeight(){
        return minHeight + getPaddingBottom() + getPaddingTop();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = MeasureSpec.getSize(heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, height+5);

        for(int i=0;i<getChildCount();i++){
            View child = getChildAt(i);

            if (i == 0) {
                minHeight = maxHeight = child.getMeasuredHeight();
            }

            if (child.getMeasuredHeight() > maxHeight) {
                maxHeight = child.getMeasuredHeight();
            }

            if (child.getMeasuredHeight() < minHeight) {
                minHeight = child.getMeasuredHeight();
            }
        }
    }


    @Override
    public void draw(Canvas canvas) {

        Paint paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.color_light_pink_light,null));

        int padding = getPaddingLeft();
        int radius = maxHeight-minHeight;

        Path path = new Path();
        path.moveTo(0,radius+2);
        path.lineTo((getWidth()/2)-radius-padding,radius+2);
        RectF oval = new RectF( (getWidth()/2)-radius-padding,2,
                (getWidth()/2)+radius+padding, radius*2+2);
        path.addArc(oval,-180,180);
        path.lineTo(getWidth(),radius+2);
        path.lineTo(getWidth(),getHeight());
        path.lineTo(0,getHeight());
        path.lineTo(0,radius+2);

        canvas.drawPath(path,paint);
        canvas.clipPath(path);
        super.draw(canvas);

    }

    public MyBottomWidget addItem(BottomTab tab){

        tab.setOnClickListener(new OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                if(onItemClickListener != null){
                    onItemClickListener.onClick(v);
                }
                if(tab.getId() != currentSelect && v.getId() != 2){
                    ((BottomTab)(getChildAt(currentSelect))).setSelected(false);
                    tab.setSelected(true);
                    currentSelect = tab.getId();
                }

            }
        });

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1;

        tab.setId(getChildCount());
        if(tab.getId() == 0){
            tab.setSelected(true);
        }
        tab.setLayoutParams(params);
        addView(tab);

        return this;

    }

    public void setOnItemClickListener(onItemClickListener listener){
        onItemClickListener = listener;
    }
    public interface onItemClickListener{
        public void onClick(View view);
    }
}
