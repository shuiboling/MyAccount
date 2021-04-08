package com.reihiei.firstapp.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

public class MyScrollLayout extends NestedScrollView {

    private RecyclerView target;

    public MyScrollLayout(@NonNull Context context) {
        super(context);
    }

    public MyScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTarget(RecyclerView target){
        this.target = target;
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {

        if(!target.canScrollVertically(-1) && !target.canScrollVertically(1)){
            consumed[1] = dy;
        }

        boolean showTop = dy < 0 && !target.canScrollVertically(-1);

        boolean showBottom = dy > 0 && !target.canScrollVertically(1);
    }
}
