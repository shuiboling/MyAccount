package com.reihiei.firstapp.ui.apater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.AccountBean;
import com.reihiei.firstapp.ui.activity.AnalyseActivity;
import com.reihiei.firstapp.widget.PercentLine;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ChartAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<AccountBean> list;

    public ChartAdapter(Context context,List<AccountBean> list){
        this.context = context;
        this.list = list;
    }

    public void upDate(List<AccountBean> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chart,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder)holder;
        AccountBean bean = list.get(position);

        myViewHolder.tvIn.setText("+¥"+bean.getInMoney());
        myViewHolder.tvOut.setText("-¥"+bean.getOutMoney());

        if(bean.getType() == 0){

            if(bean.getYear() == -1){
                myViewHolder.tvNum.setText("总计");
            }else {
                myViewHolder.tvNum.setText(bean.getYear()+"年");
            }
        }else {
            myViewHolder.tvNum.setText(bean.getMonth()+"月");
        }

        float in = Float.valueOf(bean.getInMoney());
        float out = Float.valueOf(bean.getOutMoney());

        BigDecimal bigIn = new BigDecimal(in);
        BigDecimal bigOut = new BigDecimal(out);

        float total = bigIn.subtract(bigOut).setScale(2, RoundingMode.HALF_UP).floatValue();
        if(total>0){
            myViewHolder.tvTotal.setText("+¥"+Math.abs(total));
        }else {
            myViewHolder.tvTotal.setText("-¥"+Math.abs(total));
        }

        if(in == 0 && out != 0){
            myViewHolder.percentLine.setLength(1);
        }else if(out == 0 && in != 0){
            myViewHolder.percentLine.setLength(0);
        }else if(in == 0 && out == 0){
            myViewHolder.percentLine.setVisibility(View.GONE);
        }else {
            myViewHolder.percentLine.setLength(bigOut.divide(bigIn.add(bigOut),2, BigDecimal.ROUND_UP).floatValue());

        }

        if(position == list.size()-1){
            myViewHolder.flItem.setPadding(0,0,0,50);
        }else {
            myViewHolder.flItem.setPadding(0,0,0,0);
        }

        myViewHolder.flItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bean.getType() == 0) {
                    //年
                }else {
                    //月
                    Intent intent = new Intent(context, AnalyseActivity.class);
                    intent.putExtra("year", bean.getYear());
                    intent.putExtra("month", bean.getMonth());
                    intent.putExtra("outMoney", bean.getOutMoney());
                    intent.putExtra("inMoney", bean.getInMoney());
                    intent.putExtra("total", total);
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

//        private TextView tvType;
        private CardView flItem;
        private TextView tvOut;
        private TextView tvIn;
        private TextView tvTotal;
        private TextView tvNum;
        private PercentLine percentLine;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
//            tvType = itemView.findViewById(R.id.tv_type);
            flItem = itemView.findViewById(R.id.fl_chart_item);
            tvOut = itemView.findViewById(R.id.tv_out);
            tvIn = itemView.findViewById(R.id.tv_in);
            tvTotal = itemView.findViewById(R.id.tv_total);
            tvNum = itemView.findViewById(R.id.tv_num);
            percentLine = itemView.findViewById(R.id.percent_line);
        }
    }
}
