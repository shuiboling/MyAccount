package com.reihiei.firstapp.ui.apater;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.AccountBean;
import com.reihiei.firstapp.bean.TagBean;
import com.reihiei.firstapp.db.DbUtils;
import com.reihiei.firstapp.widget.LineChart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnalyseOutAdapter extends RecyclerView.Adapter {

    private List<AccountBean> beanList;
    private Context context;
    private float sumOutMoney;

    private TypedArray icons;
    private String[] names;
    private List<TagBean> tagBeans;
    private List<String> nameList;

    public AnalyseOutAdapter(List<AccountBean> beanList,Context context,float sumOutMoney){
        this.context = context;
        this.beanList = beanList;
        this.sumOutMoney = sumOutMoney;

        init();
    }

    private void init() {
        names = context.getResources().getStringArray(R.array.out_type_name);
        icons = context.getResources().obtainTypedArray(R.array.out_type_icon);

        tagBeans = DbUtils.getInstance(context).queryByType(0);
        nameList = new ArrayList<>(Arrays.asList(names));  //Arrays.asList不能修改
        for (TagBean bean : tagBeans) {
            nameList.add(bean.getName());
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.module_analyse_out,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder)holder;

        myViewHolder.tvOut.setText("-¥"+sumOutMoney);

        for (AccountBean bean : beanList) {
            View view = View.inflate(context, R.layout.item_analyse_out, null);
            TextView name = view.findViewById(R.id.name);

            if(bean.getClassify()+1>names.length){
                name.setText(DbUtils.getInstance(context).queryTagById(bean.getClassify()+"").getName());
            }else {
                name.setText(nameList.get(bean.getClassify()));
            }

            TextView money = view.findViewById(R.id.money);
            money.setText("-¥" + bean.getOutMoney());

            BigDecimal outMoney = new BigDecimal(bean.getOutMoney());
            float ratio = outMoney.divide(new BigDecimal(sumOutMoney),3, BigDecimal.ROUND_UP).floatValue();

            TextView percent = view.findViewById(R.id.percent);
            percent.setText(new BigDecimal(ratio * 100).setScale(1, BigDecimal.ROUND_FLOOR).floatValue() + "%");

            myViewHolder.llClassify.addView(view, ratio);
        }
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private LineChart llClassify;
        private TextView tvOut;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            llClassify = itemView.findViewById(R.id.ll_classify);
            tvOut = itemView.findViewById(R.id.tv_out);

        }
    }
}
