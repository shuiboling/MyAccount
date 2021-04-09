package com.reihiei.firstapp.ui.apater;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.AccountBean;
import com.reihiei.firstapp.bean.TagBean;
import com.reihiei.firstapp.db.DbUtils;
import com.reihiei.firstapp.widget.DragLayout;

import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<AccountBean> list;
    private String[] namesIn,namesOut;
    private TypedArray iconsIn,iconsOut;

    public AccountAdapter(Context context, List<AccountBean> list) {
        this.context = context;
        this.list = list;

        namesOut = context.getResources().getStringArray(R.array.out_type_name);
        iconsOut = context.getResources().obtainTypedArray(R.array.out_type_icon);

        namesIn = context.getResources().getStringArray(R.array.in_type_name);
        iconsIn = context.getResources().obtainTypedArray(R.array.in_type_icon);

    }

    public void update(List<AccountBean> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_account,parent,false);

//        View view = View.inflate(context,R.layout.item_account, null);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;

        AccountBean bean = list.get(position);

        int index = bean.getClassify();

        if(bean.getType() == 0){
            if(index+1>namesOut.length ){
                myViewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.chun,null));
                myViewHolder.type.setText(DbUtils.getInstance(context).queryTagById(index+"").getName());
            }else {
                myViewHolder.icon.setImageDrawable(context.getResources().getDrawable(iconsOut.getResourceId(index,0),null));
                myViewHolder.type.setText(namesOut[index]);
            }
            myViewHolder.money.setText("-¥"+bean.getOutMoney());
        }else {
            if(index+1>namesIn.length ){
                myViewHolder.icon.setImageDrawable(context.getResources().getDrawable(R.drawable.chun,null));
                myViewHolder.type.setText(DbUtils.getInstance(context).queryTagById(index+"").getName());
            }else {
                myViewHolder.icon.setImageDrawable(context.getResources().getDrawable(iconsIn.getResourceId(index,0),null));
                myViewHolder.type.setText(namesIn[index]);
            }
            myViewHolder.money.setText("+¥"+bean.getInMoney());
        }
        myViewHolder.remark.setText(bean.getRemark());

        myViewHolder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(accountInterface != null){
                    accountInterface.onClickListener(myViewHolder.money,position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout item;
        private ImageView icon;
        private TextView type;
        private TextView money;
        private TextView remark;
        private DragLayout dragLayout;
        private RelativeLayout del;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item = (LinearLayout) itemView.findViewById(R.id.ll_item_account);
            icon = itemView.findViewById(R.id.iv_icon);
            type = itemView.findViewById(R.id.tv_type);
            money = itemView.findViewById(R.id.tv_money);
            remark = itemView.findViewById(R.id.tv_remark);
            dragLayout = itemView.findViewById(R.id.drag);
            del = itemView.findViewById(R.id.del);
        }
    }

    private AccountInterface accountInterface;

    public void setAccountInterface(AccountInterface accountInterface){
        this.accountInterface = accountInterface;
    }
    public interface AccountInterface{
        public void onClickListener(View view,int position);
    }

}
