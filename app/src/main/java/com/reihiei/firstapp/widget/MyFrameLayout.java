package com.reihiei.firstapp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyFrameLayout extends FrameLayout {
    public MyFrameLayout(@NonNull Context context) {
        super(context);
    }

    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private MyBottomWidget myBottomWidget;
    private int bottomHeight,deltHeight;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        for(int i=0;i<getChildCount();i++){

            View child = getChildAt(i);

            if(child instanceof MyBottomWidget){
                myBottomWidget = (MyBottomWidget)child;
                deltHeight = myBottomWidget.getMinHeight() + myBottomWidget.getPaddingTop()+ myBottomWidget.getPaddingBottom();
            }

            if(child instanceof FrameLayout){

                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)child.getLayoutParams();
                params.width = child.getMeasuredWidth();
                params.height = child.getMeasuredHeight()-deltHeight;
                child.setLayoutParams(params);
                measureChild(child,widthMeasureSpec,heightMeasureSpec);

            }
        }


    }
}
