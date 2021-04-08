package com.reihiei.firstapp.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.reihiei.firstapp.R;

public class RoundView extends View {
    private float radiusMax = 10;
    private float size = 50;
    private Paint paint;
    private float degree;
    private int count = 12;
    private float[] radius = new float[count];
    private int[] colors = new int[count];
    private ObjectAnimator objectAnimator;
    private int nowDegree = 0;

    public RoundView(Context context) {
        super(context);
        paint = new Paint();
        initCircle();
        initAnimate();

    }

    public RoundView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        initCircle();

        TypedArray typedArray = context.getResources().obtainAttributes(attrs,R.styleable.RoundView);
        radiusMax = typedArray.getDimension(R.styleable.RoundView_raduis,radiusMax);
        typedArray.recycle();

        initAnimate();
    }

    private void initAnimate() {
        objectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0f, 360f);//添加旋转动画，旋转中心默认为控件中点
        objectAnimator.setDuration(3000);//设置动画时间
        objectAnimator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
    }

    private void initCircle() {
        for(int i=count-1;i>=0;i--){
            if(i == 10){ //11
                radius[i] = radiusMax;
                colors[i] = getContext().getColor(R.color.color_pink_dark);
            }else if(i == 11 || i == 9){ //12,10
                radius[i] = (float) (radiusMax * 0.85);
                colors[i] = getContext().getColor(R.color.color_pink_dark_1);
            }else if(i>=0 && i<7){ //1-7
                radius[i] = radiusMax/2;
                colors[i] = getContext().getColor(R.color.color_pink_lowst);
            }else { //8,9
                radius[i] = (float) (radius[i+1]*0.88);
                colors[i] = getContext().getColor(R.color.color_81D81BA9);
            }
        }
    }

    public RoundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RoundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST){
            widthSize = heightSize = (int)radiusMax*10;
            setMeasuredDimension(widthSize,heightSize);
        }else if(widthMode == MeasureSpec.AT_MOST){
            widthSize = (int)radiusMax*10;
            setMeasuredDimension(widthSize,heightMeasureSpec);
        }else if(heightMode == MeasureSpec.AT_MOST){
            heightSize = (int)radiusMax*10;
            setMeasuredDimension(widthMeasureSpec,heightSize);
        }else {
            setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
        }

        size = Math.min(widthSize,heightSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        degree = 360 / count;
        canvas.rotate(nowDegree, size/2, size/2);

        for(int i = 0;i<count;i++) {
            paint.setColor(colors[i]);
            canvas.drawCircle(size/2, radiusMax, radius[i], paint);
            canvas.rotate(degree, size/2, size/2);
        }
        nowDegree += degree;
        postInvalidateDelayed(100);

    }

    public void start(){
        objectAnimator.start();
    }
}
