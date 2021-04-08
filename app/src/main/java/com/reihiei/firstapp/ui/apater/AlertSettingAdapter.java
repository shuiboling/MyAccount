package com.reihiei.firstapp.ui.apater;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;

import java.util.ArrayList;
import java.util.List;

public class AlertSettingAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<String> list;
    private List<Integer> count = new ArrayList<>();

    public AlertSettingAdapter(Context context,List<String> list,List<Integer> count){
        this.context = context;
        this.list = list;
        if(count != null){
            this.count = count;
        }
    }

    public void updateList(List<String> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alert_setting,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder)holder;
        myViewHolder.textView.setText(list.get(position));
        if(count.contains(position)){
            myViewHolder.radioButton.setChecked(true);
        } else {
            myViewHolder.radioButton.setChecked(false);
        }
        myViewHolder.radioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
//                checkBox.setChecked(!checkBox.isChecked());
                if(checkBox.isChecked()){
                    if(count.size() == 5){
                        checkBox.setChecked(false);
                        Toast.makeText(context,"最多添加5个",Toast.LENGTH_SHORT).show();
                    } else {
                        count.add(position);

                    }
                }else {
                    count.remove(count.indexOf(position));

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView textView;
        private CheckBox radioButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.text);
            radioButton = itemView.findViewById(R.id.radio);
        }
    }

    public List<Integer> getChoose(){
        return count;

    }

}
