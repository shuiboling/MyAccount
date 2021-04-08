package com.reihiei.firstapp.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class PassWord extends View {

    private int radius = 15;
    private int count = 6;
    private Paint paintStroke, paintFill;

    private List<Circle> circles;
    private List<Integer> list = new ArrayList<>();
    private ValueAnimator valueAnimator;
    private int step = 5;

    private String pw, inPw = "";

    public PassWord(Context context) {
        super(context);
    }

    public PassWord(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public PassWord(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PassWord(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        init();

        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void init() {
        paintStroke = new Paint();
        paintStroke.setColor(Color.WHITE);
        paintStroke.setStrokeWidth(2);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setAntiAlias(true);

        paintFill = new Paint();
        paintFill.setColor(Color.WHITE);
        paintFill.setStrokeWidth(2);
        paintFill.setAntiAlias(true);

        circles = new ArrayList<>();
        int x = radius + 2;
        int width = getMeasuredWidth();
        int gap = (width - (radius + 2) * 2 * count) / (count-1);
        for (int i = 0; i < count; i++) {
            list.add(x);
            Circle circle = new Circle(x, paintStroke);
            circles.add(circle);
            x = x + (radius + 2) * 2 + gap;
        }

        valueAnimator = ValueAnimator.ofInt(1, step * (count + 1));
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
//                Log.d("zyy1",animation.getAnimatedValue()+"");
                updateCircle((Integer) animation.getAnimatedValue());
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                inputNum = 0;
            }
        });
    }

    private void updateCircle(int progress) {

        if (progress < step * (count + 1)) {

            for (int i = 1; i <= count + 1; i++) {

                if (progress < step * i) {

                    inputNum = count - i;

                    if (i == 1) {
                        //左移
                        circles.get(inputNum).x -= 0.2 * radius;
                    } else {

                        if (i > 2) {
                            //归位
                            circles.get(inputNum + 2).x = list.get(inputNum + 2);
                            circles.get(inputNum + 2).paint = paintStroke;
                        }
                        //左移
                        circles.get(inputNum + 1).x += 0.2 * radius;
                        if (i != count + 1) {
                            //右移
                            circles.get(inputNum).x -= 0.2 * radius;
                        }
                    }
                    break;
                }
            }
        } else {
            inputNum = 0;
            circles.get(inputNum).x = list.get(inputNum);
            circles.get(inputNum).paint = paintStroke;
        }

//        if (progress < 5) {
//            circles.get(5).x -= 0.2 * radius;
//
//        } else if (progress < 10) {
//
//            circles.get(5).x += 0.2 * radius;
//            circles.get(4).x -= 0.2 * radius;
//
//        } else if (progress < 15) {
//            circles.get(5).x = list.get(5);
//            circles.get(5).paint = paintStroke;
//
//            circles.get(4).x += 0.2 * radius;
//            circles.get(3).x -= 0.2 * radius;
//
//        } else if(progress < 20){
//            circles.get(4).x = list.get(4);
//            circles.get(4).paint = paintStroke;
//
//            circles.get(3).x += 0.2 * radius;
//            circles.get(2).x -= 0.2 * radius;
//        }else if(progress < 25){
//            circles.get(3).x = list.get(3);
//            circles.get(3).paint = paintStroke;
//
//            circles.get(2).x += 0.2 * radius;
//            circles.get(1).x -= 0.2 * radius;
//        }else if(progress < 30){
//            circles.get(2).x = list.get(2);
//            circles.get(2).paint = paintStroke;
//
//            circles.get(1).x += 0.2 * radius;
//            circles.get(0).x -= 0.2 * radius;
//        }else if(progress < 35){
//            circles.get(1).x = list.get(1);
//            circles.get(1).paint = paintStroke;
//
//            circles.get(0).x += 0.2 * radius;
//
//        }else {
//            circles.get(0).x = list.get(0);
//            circles.get(0).paint = paintStroke;
//        }

    }

    class Circle {
        int x;
        Paint paint;

        public Circle(int x, Paint paint) {
            this.x = x;
            this.paint = paint;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            width = (radius + 2) * 2 * count + 80 * (count-1);
            height = (radius + 2) * 2;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = (radius + 2) * 2 * count + 80 * (count-1);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = (radius + 2) * 2;
        }
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int gap = (width - (radius + 2) * 2 * count) / (count-1);

        int x = radius + 2;

        for (int i = 0; i < count; i++) {

            canvas.drawCircle(circles.get(i).x, getHeight() / 2, radius, circles.get(i).paint);
            x = x + (radius + 2) * 2 + gap;
        }

        if (inputNum == count) {

            inputNum--;
            if (pw.equals(inPw)) {
                if (pwInterface != null) {
                    pwInterface.jumpTo();
                }

            } else {
                valueAnimator.start();
            }
            inPw = "";

        }

    }

    int inputNum = 0;

    public int inputNum(String pw) {

        inPw += pw;
        circles.get(inputNum).paint = paintFill;
        inputNum++;
        post(new Runnable() {
            @Override
            public void run() {
                invalidate();

            }
        });
        return inputNum;
    }

    public void delNum() {

        if (inputNum == 0)
            return;
        inputNum--;
        circles.get(inputNum).paint = paintStroke;

        post(new Runnable() {
            @Override
            public void run() {
                invalidate();

            }
        });
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    private PwInterface pwInterface;

    public void setPwInterface(PwInterface pwInterface) {
        this.pwInterface = pwInterface;
    }

    public interface PwInterface {
        public void jumpTo();
    }
}


















