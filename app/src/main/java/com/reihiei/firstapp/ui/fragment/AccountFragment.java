package com.reihiei.firstapp.ui.fragment;

import android.os.Build;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.AccountBean;
import com.reihiei.firstapp.bean.AccountBillResp;
import com.reihiei.firstapp.db.DbUtils;
import com.reihiei.firstapp.framework.SimpleFragment;
import com.reihiei.firstapp.ui.apater.AccountAdapter;
import com.reihiei.firstapp.ui.decoration.AccountDecoration;
import com.reihiei.firstapp.widget.MyDatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AccountFragment extends SimpleFragment {
    @BindView(R.id.tv_year)
    TextView tvYear;
    @BindView(R.id.tv_month)
    TextView tvMonth;
    @BindView(R.id.rc_list)
    RecyclerView recyclerView;
    @BindView(R.id.tv_pay)
    TextView tvPay;
    @BindView(R.id.tv_income)
    TextView tvIncome;
    @BindView(R.id.in_flower)
    ImageView inFlower;
    @BindView(R.id.out_flower)
    ImageView outFlower;

    private int year,month;
    private List<AccountBean> list = new ArrayList<>();
    private AccountAdapter accountAdapter;
    private AccountDecoration decoration;

    @Override
    protected void initEventAndView() {

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        tvYear.setText(year+"年");
        month = calendar.get((Calendar.MONTH))+1;
        tvMonth.setText(month+"月");

        initRecyclerView();

    }

    private void initRecyclerView() {
        accountAdapter = new AccountAdapter(getContext(),list);
        accountAdapter.setAccountInterface(new AccountAdapter.AccountInterface() {
            @Override
            public void onLongClickListener(View view,int position) {
                PopupMenu popupMenu = new PopupMenu(context,view);
                popupMenu.inflate(R.menu.del_menu);
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        DbUtils.getInstance(context).deleteByTime(list.get(position).getAddTime());
                        getData();
                        popupMenu.dismiss();
                        return true;
                    }
                });
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        decoration = new AccountDecoration(context,list);
        recyclerView.setAdapter(accountAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(decoration);


    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_account;
    }

    @OnClick({R.id.ll_date,R.id.ll_in,R.id.ll_out})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.ll_date:
                showDataPicker();
                break;

            case R.id.ll_in:

                outFlower.setVisibility(View.GONE);
                if(View.VISIBLE == inFlower.getVisibility()){
                    inFlower.setVisibility(View.GONE);
                } else {
                    inFlower.setVisibility(View.VISIBLE);
                }
                getData();
                break;

            case R.id.ll_out:

                inFlower.setVisibility(View.GONE);
                if(View.VISIBLE == outFlower.getVisibility()){
                    outFlower.setVisibility(View.GONE);
                } else {
                    outFlower.setVisibility(View.VISIBLE);
                }
                getData();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
        recyclerView.scrollToPosition(0);

    }

    public void getData(){
        AccountBillResp accountBillResp = new AccountBillResp();
        if(inFlower.getVisibility() == View.VISIBLE){
            accountBillResp = DbUtils.getInstance(context).queryByMonth(year,month,1);
            tvIncome.setText("+¥"+accountBillResp.getSumIn());

        } else if(outFlower.getVisibility() == View.VISIBLE){
            accountBillResp = DbUtils.getInstance(context).queryByMonth(year,month,0);
            tvPay.setText("-¥"+accountBillResp.getSumOut());

        } else {
            accountBillResp = DbUtils.getInstance(context).queryByMonth(year,month,-1);
            tvIncome.setText("+¥"+accountBillResp.getSumIn());
            tvPay.setText("-¥"+accountBillResp.getSumOut());
        }
        list = accountBillResp.getList();

        accountAdapter.update(list);
        decoration.upDate(list);
    }

    private void showDataPicker() {

        new MyDatePicker(context,(datePicker -> {
            year = datePicker.getYear();
            tvYear.setText(year+"年");
            month = (datePicker.getMonth()+1);
            tvMonth.setText(month+"月");
            inFlower.setVisibility(View.GONE);
            outFlower.setVisibility(View.GONE);
            getData();
        })).showDataPicker(year,month-1,1,false);

    }
}
