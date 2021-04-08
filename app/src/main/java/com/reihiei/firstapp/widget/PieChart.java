package com.reihiei.firstapp.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.AnalyseInBean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PieChart extends View {

    private int centerX, centerY;
    private int radius = 200, lRadius = 230;
    private int textSize = 50, tagTxtSize = 35;

    private Paint paint, txtPaint, dashPaint;

    private List<AnalyseInBean> list = new ArrayList<>();

    public PieChart(Context context) {
        super(context);
        init();
    }

    public PieChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public PieChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();

    }

    private void init() {
        paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setAntiAlias(true);
        paint.setTextSize(textSize);
//        paint.setStyle(Paint.Style.STROKE);

        txtPaint = new Paint();
        txtPaint.setStrokeWidth(1);
        txtPaint.setAntiAlias(true);
        txtPaint.setTextSize(tagTxtSize);

        dashPaint = new Paint();
        dashPaint.setStrokeWidth(3);
        dashPaint.setColor(getResources().getColor(R.color.color_light_pink_light, null));
        dashPaint.setPathEffect(new DashPathEffect(new float[]{13, 13}, 0));
    }

    public void setList(List<AnalyseInBean> list1) {

        post(new Runnable() {
            @Override
            public void run() {
                list = list1;
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.height = getHeight() + list.size() * tagTxtSize * 2;
                setLayoutParams(layoutParams);
                invalidate();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.d("zyy", "measure");
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (heightSize == 0) {
            heightSize = 1000;
        }

        setMeasuredDimension(widthMeasureSpec, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        centerX = getWidth() / 2;
        centerY = centerX;

        RectF rectF = new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        float startDegree = -90f;
        int textHeight = centerY + radius + 300 + tagTxtSize;
        for (int i = 0; i < list.size(); i++) {

            setPaintColor(paint, i);

            float percent = list.get(i).getPercent();
            if (percent < 0.01) {
                percent = 0.01f;
            }

            float sweepAngle = percent * 360;

            drawTag((int) (startDegree + sweepAngle / 2), canvas, paint
                    , new BigDecimal(list.get(i).getPercent() * 100).setScale(1, BigDecimal.ROUND_FLOOR).floatValue() + "%", i);
            canvas.drawArc(rectF, startDegree, sweepAngle, true, paint);

            startDegree += sweepAngle;

            txtPaint.setColor(paint.getColor());
//            canvas.drawLine(0,textHeight,getWidth(),textHeight,paint);
            canvas.drawRect(radius - 10 - tagTxtSize / 2, textHeight - (2 * tagTxtSize / 3), radius - 10, textHeight - (tagTxtSize / 5), txtPaint);
            canvas.drawText(list.get(i).getName(), radius, textHeight, txtPaint);
            canvas.drawText("¥" + list.get(i).getMoney(), radius + 4 * tagTxtSize + 10, textHeight, txtPaint);

            textHeight += (10 + tagTxtSize);
        }

        canvas.drawLine(0, textHeight + 50, getWidth(), textHeight + 50, dashPaint);

    }

    private void setPaintColor(Paint paint, int index) {
        int color = 0;
        switch (index) {
            case 0:
                color = getResources().getColor(R.color.color_purple, null);
                break;
            case 1:
                color = getResources().getColor(R.color.color_f6c2e4, null);
                break;
            case 2:
                color = getResources().getColor(R.color.color_92c8e0, null);
                break;
            case 3:
                color = getResources().getColor(R.color.color_89d7bc, null);
                break;
            case 4:
                color = getResources().getColor(R.color.color_d2d689, null);
                break;
            default:
                Random random = new Random();
                color = 0xff000000 | (random.nextInt(0x00ffffff) * index);

        }
        paint.setColor(color);

    }

    private float preStartX = 0, firstStartX = 0;

    private void drawTag(int degree, Canvas canvas, Paint paint, String text, int index) {

        float startX = centerX + (float) (radius * Math.cos(Math.toRadians(degree)));
        float startY = centerY + (float) (radius * Math.sin(Math.toRadians(degree)));
        float endX = (float) (lRadius * Math.cos(Math.toRadians(degree)));
        float endY = (float) (lRadius * Math.sin(Math.toRadians(degree)));

        if (endY > 0) {
            endY += textSize * (index % 5);
        } else {
            endY -= textSize * (index % 5);
        }

        canvas.drawLine(startX, startY, centerX + endX, centerY + endY, paint);

        if (endX >= 0) {
            canvas.drawLine(centerX + endX, centerY + endY, centerX + endX + textSize, centerY + endY, paint);
            canvas.drawText(text, centerX + endX + 3 * textSize / 2, centerY + endY + textSize / 2, paint);

        } else {
            canvas.drawLine(centerX + endX, centerY + endY, centerX + endX - textSize, centerY + endY, paint);
            canvas.drawText(text, centerX + endX - 7 * textSize / 2, centerY + endY + textSize / 2, paint);

        }
        preStartX = endY;
    }
}
