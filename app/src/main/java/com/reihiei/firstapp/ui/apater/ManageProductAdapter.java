package com.reihiei.firstapp.ui.apater;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.ManageProductBean;

import java.util.List;

public class ManageProductAdapter extends RecyclerView.Adapter {

    private List<ManageProductBean> list;
    private Context context;
    private OnClickListener listener;
    private String[] types;

    public ManageProductAdapter(Context context,List<ManageProductBean> list){
        this.context = context;
        this.list = list;
        types = context.getResources().getStringArray(R.array.manage_product_type);
    }

    public void upDate(List<ManageProductBean> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_product,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder)holder;
        ManageProductBean bean = list.get(position);

        myViewHolder.name.setText(bean.getName());

        if(new Integer(bean.getType()) != null && bean.getType() != -1){
            myViewHolder.type.setText(types[bean.getType()]);
            myViewHolder.type.setVisibility(View.VISIBLE);

        } else {
            myViewHolder.type.setVisibility(View.GONE);

        }

        myViewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onEdit(bean);
                }
            }
        });

        myViewHolder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onDel(bean);
                }
            }
        });

        myViewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onClick(bean);
                }
            }
        });
    }

    public void getType(int type){

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private ImageView edit;
        private ImageView del;
        private TextView type;
        private LinearLayout item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tag);
            edit = itemView.findViewById(R.id.iv_edit);
            del = itemView.findViewById(R.id.iv_del);
            type = itemView.findViewById(R.id.type);
            item = itemView.findViewById(R.id.ll_item);
        }
    }

    public void setOnListener(OnClickListener listener){
        this.listener = listener;
    }

    public interface OnClickListener{
        public void onEdit(ManageProductBean bean);
        public void onDel(ManageProductBean bean);
        public void onClick(ManageProductBean bean);
    }
}
