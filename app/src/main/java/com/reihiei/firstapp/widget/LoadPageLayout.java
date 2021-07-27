package com.reihiei.firstapp.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

public class LoadPageLayout extends LinearLayout implements NestedScrollingParent {

    private View mTarget; // the target of the gesture，可滑动目标
    private Scroller mLayoutScroller; //当前父布局的scroller
    private static final int SCROLL_RATIO = 5;//滑动阻力

    private int REFRESH_EFFECTIVE = 300;
    private int LOAD_EFFECTIVE = 300;

    // 普通状态
    private static final int NORMAL = 0;
    // 意图刷新
    private static final int TRY_REFRESH = 1;
    // 刷新状态
    private static final int REFRESH = 2;
    // 意图加载
    private static final int TRY_LOAD_MORE = 3;
    // 加载状态
    private static final int LOAD_MORE = 4;
    private int status = NORMAL;

    //使Android 5.0 Lollipop (API 21)之前的版本支持嵌套滑动
//    private final NestedScrollingParentHelper mNestedScrollingParentHelper;

    public LoadPageLayout(Context context) {
        super(context);
        mLayoutScroller = new Scroller(context);
    }

    public LoadPageLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mLayoutScroller = new Scroller(context);
    }

    public LoadPageLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLayoutScroller = new Scroller(context);
    }

    public LoadPageLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mLayoutScroller = new Scroller(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //寻找RecyclerView
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);

            if (child instanceof FrameLayout) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) child.getLayoutParams();
                layoutParams.height = getMeasuredHeight() / 2;
                child.setLayoutParams(layoutParams);

            } else if (child instanceof RecyclerView) {
                mTarget = child;
            }
        }
    }

    //mLayoutScroller.startScroll 不会触发布局滑动，只是计算滑动值，调用invalidate后会调用draw重绘，
    // draw方法中会调用computeScroll,computeScroll中通过调用Scroller的computeScrollOffset()方法
    // 可以知道滑动是否结束，没结束可以调用scrollTo或scrollBy方法,这两个方法会触发调用invalidate()引起
    // 布局重绘->调用draw->computeScroll直至computeScrollOffset为false时全部滑动结束
    @Override
    public void computeScroll() {
        super.computeScroll();
        Log.d("zyy", "computeScroll");
        if (mLayoutScroller.computeScrollOffset()) {
            scrollTo(0, mLayoutScroller.getCurrY());
//            postInvalidate();

        }
    }

    //子View询问父View是否接收嵌套滚动时调用
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes) {
        //纵向返回true
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    //父view接受了子View嵌套滑动邀请
    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
    }

    //父view是否先消耗滚动参数
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        Log.d("zyyd", "onNestedPreScroll dy:" + dy);
        Log.d("zyyd", "onNestedPreScroll getScrollY():" + getScrollY());

        if (!mTarget.canScrollVertically(1) && getScrollY() > 0) {

            //告诉子View消耗了多少
            pullUp(dy);
            consumed[1] = dy;
        }
    }

    //子View主动将消费的距离与未消费的距离通知父View
    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {

        Log.d("zyyu", "dyUnconsumed:" + dyUnconsumed);

        if (dyUnconsumed > 0) {
            pullUp(dyUnconsumed);
        }
    }

    //是否消耗fling事件
    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
//        当顶部控件显示时，fling可以让顶部控件隐藏或者显示。
        return false;
    }

    //处理惯性事件
    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return false;
    }

    //父view停止嵌套滑动
    @Override
    public void onStopNestedScroll(@NonNull View target) {
        Log.d("zyyu", "nested stop");
        pullFinish();
    }

    private int mLastMoveY;

    //返回false，后续事件会都先在此方法询问是否拦截
    //父View一旦拦截返回true，后续事件就都会交由父View的onTouchEvent处理
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

//        boolean intercept = false;

        //当前位置
        int y = (int) ev.getY();

//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                mLastMoveY = y;
//                intercept = false;
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.d("zyyi", "inter");
//
//                if (y < mLastMoveY) {  //上拉
//                    Log.d("zyyi", "up");
//
//                    //false表示rc已滚动底部，拦截
//                    intercept = !mTarget.canScrollVertically(1);
//
//                } else {
//                    Log.d("zyyi", "down");
//
//                    //false表示已滚动底部，拦截
//                    intercept = !mTarget.canScrollVertically(-1);
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                intercept = false;
//                break;
//        }

        mLastMoveY = y;
        return false;

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //当前位置
        int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastMoveY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                int dy = mLastMoveY - y;
                Log.d("zyyd", "dy:" + dy);

                if (dy > 0) {
                    pullUp(dy);
                } else if (getScrollY() >= 0) {
                    scrollBy(0, dy);
                }

                break;
            case MotionEvent.ACTION_UP:
//                Log.d("zyy","up");
                pullFinish();
                break;

        }

        mLastMoveY = y;

        return true;
    }

    private void pullFinish() {
        mLayoutScroller.startScroll(0, getScrollY(), 0, -getScrollY());
        postInvalidate();

        if (getScrollY() >= 100) {
            Log.d("zyy", "finish");
            if (loadListener != null) {
                loadListener.loadFinish();
            }
        }

    }

    private int pullUp(int dy) {

        int dyConsume = 0;

        if (Math.abs(getScrollY()) <= LOAD_EFFECTIVE) {
            dyConsume = dy / SCROLL_RATIO;
            scrollBy(0, dyConsume);

        } else if (Math.abs(getScrollY()) <= getHeight() / 2) {
            dyConsume = dy / (SCROLL_RATIO * 2);
            scrollBy(0, dyConsume);

        } else {

            dyConsume = dy / (SCROLL_RATIO * 10);
            scrollBy(0, dyConsume);

        }
        return dyConsume;
    }

    private LoadListener loadListener;

    public void setLoadListener(LoadListener loadListener) {
        this.loadListener = loadListener;
    }

    public interface LoadListener {
        public void loadFinish();
    }

}
