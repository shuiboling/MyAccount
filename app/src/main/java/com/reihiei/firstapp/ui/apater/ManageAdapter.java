package com.reihiei.firstapp.ui.apater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.ManageBean;
import com.reihiei.firstapp.ui.activity.CalendarActivity;
import com.reihiei.firstapp.ui.activity.ShowMentionActivity;

import java.util.List;

public class ManageAdapter extends RecyclerView.Adapter {

    private List<ManageBean> list;
    private Context context;

    public ManageAdapter(Context context, List<ManageBean> list) {
        this.context = context;
        this.list = list;
    }

    public void upDate(List<ManageBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_money, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        ManageBean manageBean = list.get(position);

        myViewHolder.name.setText(manageBean.getName());
        myViewHolder.date.setText(manageBean.getMonth() + "月" + manageBean.getDay() + "日");
        myViewHolder.channel.setText(manageBean.getChannel());
        myViewHolder.money.setText("¥" + manageBean.getMoney());

        myViewHolder.del.setOnClickListener((v) -> {
            if (onClickListener != null) {
                onClickListener.onClick(v, position);
            }
        });

        myViewHolder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (manageBean.getEventId() == -1) {
                    intent = new Intent(context, CalendarActivity.class);
                } else {
                    intent = new Intent(context, ShowMentionActivity.class);
                }
                intent.putExtra("addtime", manageBean.getAddTime());
                intent.putExtra("eventId", manageBean.getEventId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView money;
        private TextView name;
        private TextView channel;
        private TextView date;
        private ConstraintLayout item;
        private RelativeLayout del;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            money = itemView.findViewById(R.id.money);
            name = itemView.findViewById(R.id.name);
            channel = itemView.findViewById(R.id.channel);
            date = itemView.findViewById(R.id.date);
            item = itemView.findViewById(R.id.item);
            del = itemView.findViewById(R.id.del);
        }
    }

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        public void onClick(View view, int position);
    }
}
