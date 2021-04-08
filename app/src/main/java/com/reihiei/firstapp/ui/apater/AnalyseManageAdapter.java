package com.reihiei.firstapp.ui.apater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.AnalyseInBean;
import com.reihiei.firstapp.bean.AnalyseManageBean;
import com.reihiei.firstapp.bean.AnalyseManageBeanResp;
import com.reihiei.firstapp.util.CommonUtils;
import com.reihiei.firstapp.widget.PieChart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AnalyseManageAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<AnalyseManageBean> list;
    private String sum;

    public AnalyseManageAdapter(Context context,AnalyseManageBeanResp resp){
        this.context = context;
        list = resp.getList();
        sum = resp.getSum();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.module_analyse_in,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder)holder;

        List<AnalyseInBean> analyseInBeans = new ArrayList<>();
        BigDecimal all = new BigDecimal(1);
        for (int i = 0;i<list.size();i++) {

            AnalyseManageBean bean =list.get(i);

            AnalyseInBean analyseInBean = new AnalyseInBean();
            analyseInBean.setMoney(bean.getSumMoney());
            analyseInBean.setName(CommonUtils.getClassText(bean.getClassify(),context) +" "+bean.getCount()+"笔");

            if(i == list.size()-1){
                analyseInBean.setPercent(all.floatValue());
                analyseInBeans.add(analyseInBean);
            } else {
                BigDecimal in = new BigDecimal(bean.getSumMoney());
                BigDecimal total = new BigDecimal(sum);
                BigDecimal result = in.divide(total,3,BigDecimal.ROUND_UP);
                analyseInBean.setPercent(result.floatValue());
                analyseInBeans.add(analyseInBean);
                all = all.subtract(result).setScale(3,BigDecimal.ROUND_UP);
            }

        }
        myViewHolder.pieChart.setList(analyseInBeans);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private PieChart pieChart;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            pieChart = itemView.findViewById(R.id.ll_pie);
            itemView.findViewById(R.id.tv_in).setVisibility(View.GONE);
            itemView.findViewById(R.id.tv1).setVisibility(View.GONE);
        }
    }
}
