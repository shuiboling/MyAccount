package com.reihiei.firstapp.ui.apater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.TagBean;
import com.reihiei.firstapp.db.DbUtils;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter {

    private List<TagBean> list;
    private Context context;
    private OnClickListener listener;

    public TagAdapter(Context context,List<TagBean> list){
        this.context = context;
        this.list = list;
    }

    public void upDate(List<TagBean> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tag,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder)holder;
        TagBean tagBean = list.get(position);

        myViewHolder.name.setText(tagBean.getName());

        myViewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onEdit(tagBean.getId(),tagBean.getName(),tagBean.getType());
                }
            }
        });

        myViewHolder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null){
                    listener.onDel(tagBean.getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private ImageView edit;
        private ImageView del;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tag);
            edit = itemView.findViewById(R.id.iv_edit);
            del = itemView.findViewById(R.id.iv_del);
        }
    }

    public void setOnListener(OnClickListener listener){
        this.listener = listener;
    }

    public interface OnClickListener{
        public void onEdit(String id,String name,int type);
        public void onDel(String id);
    }
}
