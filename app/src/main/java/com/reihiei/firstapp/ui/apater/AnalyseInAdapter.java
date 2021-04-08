package com.reihiei.firstapp.ui.apater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.AccountBean;
import com.reihiei.firstapp.bean.AnalyseInBean;
import com.reihiei.firstapp.bean.TagBean;
import com.reihiei.firstapp.db.DbUtils;
import com.reihiei.firstapp.widget.PieChart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnalyseInAdapter extends RecyclerView.Adapter {

    private List<AccountBean> beanList;
    private Context context;
    private float sumInMoney;

//    private TypedArray icons;
    private String[] names;
    private List<TagBean> tagBeans;
    private List<String> nameList;

    public AnalyseInAdapter(List<AccountBean> beanList,Context context,float sumInMoney){
        this.context = context;
        this.beanList = beanList;
        this.sumInMoney = sumInMoney;

        init();
    }

    private void init() {
        names = context.getResources().getStringArray(R.array.in_type_name);
//        icons = context.getResources().obtainTypedArray(R.array.in_type_icon);

        tagBeans = DbUtils.getInstance(context).queryByType(1);
        nameList = new ArrayList<>(Arrays.asList(names));  //Arrays.asList不能修改
        for (TagBean bean : tagBeans) {
            nameList.add(bean.getName());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.module_analyse_in,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder)holder;

        myViewHolder.tvIn.setText("+¥"+sumInMoney);

        List<AnalyseInBean> list = new ArrayList<>();
        BigDecimal all = new BigDecimal(1);
        for (int i = 0;i<beanList.size();i++) {

            AccountBean bean =beanList.get(i);

            AnalyseInBean analyseInBean = new AnalyseInBean();
            analyseInBean.setMoney(bean.getInMoney());
            if(bean.getClassify()+1>names.length ){
                analyseInBean.setName(DbUtils.getInstance(context).queryTagById(bean.getClassify()+"").getName());
            } else {
                analyseInBean.setName(nameList.get(bean.getClassify()));

            }

            if(i == beanList.size()-1){
                analyseInBean.setPercent(all.floatValue());
                list.add(analyseInBean);
            } else {
                BigDecimal in = new BigDecimal(bean.getInMoney());
                BigDecimal total = new BigDecimal(sumInMoney);
                BigDecimal result = in.divide(total,3,BigDecimal.ROUND_FLOOR);
                analyseInBean.setPercent(result.floatValue());
                list.add(analyseInBean);
                all = all.subtract(result).setScale(3,BigDecimal.ROUND_FLOOR);
            }

        }
        myViewHolder.pieChart.setList(list);

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        PieChart pieChart;
        TextView tvIn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            pieChart = itemView.findViewById(R.id.ll_pie);
            tvIn = itemView.findViewById(R.id.tv_in);

        }
    }
}
