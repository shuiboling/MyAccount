package com.reihiei.firstapp.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.reihiei.firstapp.R;

public class PercentLine extends View {

    private float bluePer= (float) (1.0/2.0);

    public PercentLine(Context context) {
        super(context);
    }

    public PercentLine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PercentLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PercentLine(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        bluePer= (float) (1.0/2.0);

    }

    public void setLength(float bluePer1){
        //绘制完成后再重绘
        post(new Runnable() {
            @Override
            public void run() {
                bluePer = bluePer1;
                invalidate();
            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {

        Log.d("zyy draw",bluePer+"");

        int height = getMeasuredHeight();
        int width = getMeasuredWidth();
        int radius = height/2;
        int gap = 5;
        int linear = 15;

        float blueLen = bluePer*width;

        Paint paint1 = new Paint();
        LinearGradient lgBlue=new LinearGradient(0,0,blueLen-gap,height
                ,getResources().getColor(R.color.color_blue,null)
                ,getResources().getColor(R.color.color_blue_light,null)
                , Shader.TileMode.CLAMP); //参数一为渐变起初点坐标x位置，参数二为y轴位置，参数三和四分辨对应渐变终点，
        paint1.setShader(lgBlue);

        Path path = new Path();
        path.moveTo(radius,0);
        path.lineTo(blueLen-gap,0);
        path.lineTo(blueLen - gap- linear,height);
        path.lineTo(radius,height);

        RectF oval = new RectF(0,0,height,height);
        path.addArc(oval,270,450);

        canvas.drawPath(path,paint1);

        Paint paint2 = new Paint();
        LinearGradient lgPink=new LinearGradient(blueLen+gap,0,width,height
                ,getResources().getColor(R.color.color_light_pink_light,null)
                ,getResources().getColor(R.color.color_light_pink,null)
                , Shader.TileMode.CLAMP); //参数一为渐变起初点坐标x位置，参数二为y轴位置，参数三和四分辨对应渐变终点，
        paint2.setShader(lgPink);

        Path path1 = new Path();
        path1.moveTo(blueLen+gap,0);
        path1.lineTo(width-radius,0);
        RectF oval1 = new RectF(width-height,0,width,height);
        path1.addArc(oval1,270,180);
        path1.lineTo(blueLen + gap-linear,height);
        path1.lineTo(blueLen + gap,0);
//        paint2.setColor(getResources().getColor(R.color.color_light_pink,null));
        canvas.drawPath(path1,paint2);

//        bluePer = 0.2f;

//        bluePer += 0.1;
//        postInvalidateDelayed(100);
    }
}
