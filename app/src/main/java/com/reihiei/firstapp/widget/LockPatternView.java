package com.reihiei.firstapp.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.reihiei.firstapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LockPatternView extends View {

    int radius = 80;
    private List<Circle> circles;
    private Paint paintStroke;
    private Paint paintSolid;
    private List<Integer> choose = new ArrayList<>();
    private boolean isMove = false;
    private boolean isUp = false;

    class Circle {
        int x;
        int y;
        Paint paint;
        public Circle(int x, int y,Paint paint) {
            this.x = x;
            this.y = y;
            this.paint = paint;
        }
    }

    public LockPatternView(Context context) {
        super(context);
    }

    public LockPatternView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LockPatternView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LockPatternView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        init();

        super.onSizeChanged(w, h, oldw, oldh);
    }
    public void init(){
        paintStroke = new Paint();
        paintStroke.setColor(Color.BLACK);
        paintStroke.setStrokeWidth(2);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setAntiAlias(true);

        paintSolid = new Paint();
        paintSolid.setColor(Color.RED);
        paintSolid.setAntiAlias(true);

        circles = new ArrayList<>();
        int x = radius + 2;
        int y = radius + 2;
        int count = 3;
        int width = getMeasuredWidth();
        int gap = (width - (radius + 2) * 2 * 3) / (count-1);
        for (int i = 0; i < 9; i++) {
            if(i%3 == 0){
                y += gap + radius*2;
                x = radius+2;
            }
            Circle circle = new Circle(x, y,paintStroke);
            circles.add(circle);

            x = x + (radius + 2) * 2 + gap;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        for(Circle circle:circles){
            canvas.drawCircle(circle.x,circle.y,radius,circle.paint);
        }

        int startX = 0,startY=0;
        int stopX,stopY;

        if(choose.size()>0) {
            startX = circles.get(choose.get(0)).x;
            startY = circles.get(choose.get(0)).y;

            if(choose.size()>1){
                for(int i = 1;i<choose.size();i++){
                    stopX = circles.get(choose.get(i)).x;
                    stopY = circles.get(choose.get(i)).y;
                    canvas.drawLine(startX,startY,stopX,stopY,paintSolid);
                    startX= circles.get(choose.get(i)).x;
                    startY = circles.get(choose.get(i)).y;
                }
            }
            if(isMove) {
                canvas.drawLine(startX, startY, moveX, moveY, paintSolid);
            }
        }

        if(isUp){
            canvas.drawLine(startX,startY,startX,startY,paintSolid);

//            invalidate();
        }


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float ex = event.getX();
        float ey = event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                down(ex,ey);
                break;
            case MotionEvent.ACTION_MOVE:
                move(ex,ey);
                break;
            case MotionEvent.ACTION_UP:
                up();
                break;
        }
        this.postInvalidate();

        return true;

    }

    private void down(float ex,float ey){

        isMove = false;
        isUp = false;

        for(int i = 0;i<9;i++){
            Circle circle = circles.get(i);
            if(ex>=circle.x-radius && ex<=circle.x+radius){
                if(ey>=circle.y-radius && ey<=circle.y+radius) {
                    circle.paint= paintSolid;
                    if(!choose.contains(i)){
                        choose.add(i);
                    }
                }
            }
        }
    }

    private float moveX = 0,moveY = 0;
    private void move(float ex, float ey) {
        moveX = ex;
        moveY = ey;

        isMove = true;
        isUp = false;

        for(int i = 0;i<9;i++){
            Circle circle = circles.get(i);
            if(ex>=circle.x-radius && ex<=circle.x+radius){
                if(ey>=circle.y-radius && ey<=circle.y+radius) {
                    circle.paint= paintSolid;
                    if(!choose.contains(i)){
                        choose.add(i);
                    }
                }
            }
        }
    }

    private void up() {
        isMove = false;
        isUp = true;
//
//        circles.clear();
//        moveX = 0;
//        moveY = 0;
//        isUp = false;
//        isMove = false;
    }
}
