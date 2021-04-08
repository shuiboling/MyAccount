package com.reihiei.firstapp.ui.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.AccountBean;
import com.reihiei.firstapp.bean.ManageBean;
import com.reihiei.firstapp.db.DbUtils;

import java.util.List;

public class ManageDecoration extends RecyclerView.ItemDecoration {
    private int headHeight,textSize;
    private List<ManageBean> list;
    private Paint paint,txtPaint,linePaint;
    private Context context;

    public ManageDecoration(Context context,List<ManageBean> list){
        this.context = context;
        this.list = list;
        init();
    }

    public void upDate(List<ManageBean> list){
        this.list = list;
    }

    private void init() {

        headHeight = (int) (40 * context.getResources().getDisplayMetrics().density);
        textSize = (int) (18 * context.getResources().getDisplayMetrics().density);
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.color_f2f2f2));

        txtPaint= new Paint();
        txtPaint.setColor(ContextCompat.getColor(context,R.color.color_666666));
        txtPaint.setTextSize(textSize);
        txtPaint.setAntiAlias(true);

        linePaint = new Paint();
        linePaint.setColor(ContextCompat.getColor(context,R.color.colorAccent));
        linePaint.setStrokeWidth(3);
//        linePaint.setTextSize(textSize);
//        linePaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    //对整体
    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);

        for(int i = 0;i<parent.getChildCount();i++){
            View child = parent.getChildAt(i);
            int index = parent.getChildAdapterPosition(child);

            //屏幕上第一个要画粘性头
            if(i == 0){
//                Log.d("zyy",index+"");
                float top = parent.getPaddingTop();
                //下面的头顶上来了
                View nextChild = parent.getChildAt(i+1);
                int nextIndex = parent.getChildAdapterPosition(nextChild);

                if(nextIndex > 0 && isFirstInGroup(nextIndex) && nextChild.getTop() -  top <= headHeight*2){
                    top = top - (headHeight - (nextChild.getTop()-headHeight));
                }
                c.drawRect(0,top,parent.getWidth(),top+headHeight,paint);
                float x = 20;
                float y = top+(headHeight/2 - textSize/2);

                c.drawRect(x,y,x+20,y+textSize,linePaint);

                Paint.FontMetrics fontMetrics=txtPaint.getFontMetrics();
                float distance=(fontMetrics.bottom - fontMetrics.top)/2 - fontMetrics.bottom;
                float baseline=(top+headHeight/2)+distance;
                c.drawText(getClassText(list.get(index).getClassify()),x+40,baseline,txtPaint);
            } else if(isFirstInGroup(index)){
                //非屏幕第一个，但是组内第一个要画头
                Log.d("zyy",index+"");
                c.drawRect(0,child.getTop()-headHeight,parent.getWidth(),child.getTop(),paint);
                float x = 20;
                float y = child.getTop()-headHeight/2 - textSize/2;

                c.drawRect(x,y,x+20,y+textSize,linePaint);

                Paint.FontMetrics fontMetrics=txtPaint.getFontMetrics();
                float distance=(fontMetrics.bottom - fontMetrics.top)/2 - fontMetrics.bottom;
                float baseline=(child.getTop()-headHeight/2)+distance;
                c.drawText(getClassText(list.get(index).getClassify()),x+40,baseline,txtPaint);
//
            }
        }
    }

    private String getClassText(int classify) {
        if(classify == 0){
            return "理财";
        }else if(classify == 1){
            return "基金";
        }else {
            return DbUtils.getInstance(context).queryTagById(classify+"").getName();
        }
    }

    //对每个item
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        int position = parent.getChildAdapterPosition(view);

        if(isFirstInGroup(position)){
            outRect.top = headHeight;
        }

        if(position == list.size()-1){
            outRect.bottom = headHeight;
        }
    }

    public boolean isFirstInGroup(int position){
        ManageBean pre,current;

        if(position != 0){
            pre = list.get(position - 1);
            current = list.get(position);

            if( pre.getClassify()!=current.getClassify()){
                return true;
            }else {
                return false;
            }

        }else {
            return true;
        }
    }
}
