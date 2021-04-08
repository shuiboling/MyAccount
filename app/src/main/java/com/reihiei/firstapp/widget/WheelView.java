package com.reihiei.firstapp.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WheelView extends View {

    private List<String> timeList = new ArrayList<>();
    private String unit = "";

    private Paint txtPaint, unitPaint,paint;
    private int textSize = (int) (25 * getContext().getResources().getDisplayMetrics().density);
    private int unitTextSize = (int) (12 * getContext().getResources().getDisplayMetrics().density);
    private float baseline;

    private Scroller mScroller;
    private int mMaximumVelocity;

    private int gap;
    private int centerStarY;
    private int centerStopY;
    private int mHalfVisibleItemCount = 1;
    private int index;
    int pos;

    public WheelView(Context context, List<String> timeList, String unit) {
        super(context);
        this.timeList = timeList;
        this.unit = unit;
        init(context);
    }

    private void init(Context context) {
        txtPaint = new Paint();
        txtPaint.setTextSize(textSize);
        txtPaint.setColor(Color.BLACK);
        txtPaint.setStrokeWidth(2);
        txtPaint.setTextAlign(Paint.Align.CENTER);
        txtPaint.setAntiAlias(true);

        paint = new Paint(txtPaint);

        unitPaint = new Paint();
        unitPaint.setTextSize(unitTextSize);
        unitPaint.setColor(Color.BLACK);
        unitPaint.setStrokeWidth(1);
        unitPaint.setTextAlign(Paint.Align.CENTER);

        mScroller = new Scroller(context);
        mMaximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();

        dy = 0;
        lastY = 0;
        count = 1;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getDisplay().getMetrics(displayMetrics);
        int heightSize = (int) (displayMetrics.heightPixels * 0.3);

        gap = ((heightSize / (mHalfVisibleItemCount * 2 + 1)) - textSize) / 2;

        centerStarY = mHalfVisibleItemCount * (textSize + 2 * gap);
        centerStopY = (mHalfVisibleItemCount + 1) * (textSize + 2 * gap);

        //重新计算文字baseline以垂直居中
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        baseline = (2 * gap + textSize) / 2 + distance;

        setMeasuredDimension(widthMeasureSpec, heightSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //3.上下多绘制一个
        //四舍五入获取离中心区最近的点    上负下正
        pos = new BigDecimal(-dy).divide(new BigDecimal(centerStopY - centerStarY), 0, RoundingMode.HALF_DOWN).intValue();

        if(timeList.size() == 1){
            canvas.drawText(timeList.get(index), getWidth() / 2, baseline + (2 * gap + textSize) * (mHalfVisibleItemCount) , txtPaint);
            return;

        }
        //1.绘制一列数 i=-mHalfVisibleItemCount；i<=mHalfVisibleItemCount
        for (int i = pos - mHalfVisibleItemCount - 1, countItem = timeList.size(); i <= pos + mHalfVisibleItemCount + 1; i++) {
            //4.数字收尾循环
            index = i;
            if (i < 0) {
                index = i + (-i / countItem) * countItem;
                if (index != 0) {
                    index = countItem + index;
                }
            } else if (i > countItem - 1) {
                index = i - (i / countItem) * countItem;
            }

            //7.透明度，近大远小
            float nowB = baseline + (2 * gap + textSize) * (i + mHalfVisibleItemCount) + dy;
            float midB = baseline + (2 * gap + textSize) * (mHalfVisibleItemCount);

            float alphaRatio = 1 - ((Math.abs(nowB - midB)) / 500);
            float textSizeRatio = 1 - ((Math.abs(nowB - midB)) / 800);

            txtPaint.setAlpha((int) (alphaRatio * 255));
            txtPaint.setTextSize(textSize * textSizeRatio);
            //2.添加dy滚起来
            canvas.drawText(timeList.get(index), getWidth() / 2, baseline + (2 * gap + textSize) * (i + mHalfVisibleItemCount) + dy, txtPaint);

        }

        if (count == 1){
            Log.d("zyy","index:"+index);

            if(wheelInterface != null){
                if(index<0){
                    wheelInterface.getIndex(index+timeList.size() -1);
                    Log.d("zyy","index1");

                } else if (index < mHalfVisibleItemCount+1){//2
                    wheelInterface.getIndex(timeList.size() + index-mHalfVisibleItemCount-1 );
                    Log.d("zyy","index2");


                } else {
                    wheelInterface.getIndex(index - mHalfVisibleItemCount - 1);
                    Log.d("zyy","index3");

                }
            }
        }

        if (!TextUtils.isEmpty(unit)) {
            canvas.drawText(unit, getWidth() / 2 + textSize, baseline + (2 * gap + textSize) * mHalfVisibleItemCount - unitTextSize, unitPaint);
        }
        canvas.drawLine(textSize, centerStarY, getWidth() - textSize, centerStarY, txtPaint);
        canvas.drawLine(textSize, centerStopY, getWidth() - textSize, centerStopY, txtPaint);

    }

    int lastY = 0;
    int dy = 0;
    private VelocityTracker velocityTracker;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int y = (int) event.getY();

        if (velocityTracker == null) {
            // Retrieve a new VelocityTracker object to watch the velocity of a motion
            velocityTracker = VelocityTracker.obtain();
        }
        // Add a user's movement to the tracker.
        velocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Revert any animation currently in progress
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                lastY = y;
                // Reset the velocity tracker back to its initial state.
                velocityTracker.clear();
                break;
            case MotionEvent.ACTION_MOVE:
                dy = y - lastY + dy;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                //6.惯性滑动
                // Compute the current velocity
                velocityTracker.computeCurrentVelocity(500, mMaximumVelocity);
                // Retrieve the last computed Y velocity.
                int vY = (int) (2 * velocityTracker.getYVelocity()) / 3;
                mScroller.fling(0, 0, 0, vY,
                        0, 0, -Integer.MAX_VALUE, Integer.MAX_VALUE);
                // Be sure to call {@link #recycle} when done.
                velocityTracker.recycle();
                velocityTracker = null;

                invalidate();
                break;
        }

        lastY = y;
        return true;
    }

    int count = 1;

    @Override
    public void computeScroll() {

        if (mScroller.computeScrollOffset()) {
            dy += (mScroller.getCurrY() / count++);

            if (mScroller.isFinished()) {
                //5.回弹到最近点
                pos = new BigDecimal(-dy).divide(new BigDecimal(centerStopY - centerStarY), 0, RoundingMode.HALF_DOWN).intValue();
                dy = -pos * (centerStopY - centerStarY);
                count = 1;
            }
            postInvalidate();

        }
    }

    public int getIndex() {
        return index - mHalfVisibleItemCount - 1;
    }

    private WheelInterface wheelInterface;
    public void setWheelInterface(WheelInterface wheelInterface){
        this.wheelInterface = wheelInterface;
    }
    public interface WheelInterface{
        public void getIndex(int index);
    }

    public void changeList(List<String> list){
        timeList = list;

        init(getContext());
        invalidate();
    }
}
