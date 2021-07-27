package com.reihiei.firstapp.ui.fragment;

import android.app.Dialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.ManageBean;
import com.reihiei.firstapp.bean.ManageBeanResp;
import com.reihiei.firstapp.db.DbUtils;
import com.reihiei.firstapp.framework.SimpleFragment;
import com.reihiei.firstapp.ui.apater.ManageAdapter;
import com.reihiei.firstapp.ui.decoration.AccountDecoration;
import com.reihiei.firstapp.ui.decoration.ManageDecoration;
import com.reihiei.firstapp.widget.CommonDialog;
import com.reihiei.firstapp.widget.MyDatePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ManageMoneyFragment extends SimpleFragment {

    @BindView(R.id.tv_year)
    TextView tvYear;
    @BindView(R.id.tv_month)
    TextView tvMonth;
    @BindView(R.id.rc_list)
    RecyclerView recyclerView;
    @BindView(R.id.tv_buy)
    TextView tvBuy;

    private int year, month;
    private List<ManageBean> list = new ArrayList<>();
    private ManageAdapter manageAdapter;
    private ManageDecoration decoration;

    @Override
    protected void initEventAndView() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        tvYear.setText(year + "年");
        month = calendar.get((Calendar.MONTH)) + 1;
        tvMonth.setText(month + "月");

        initRecyclerView();
    }

    private void initRecyclerView() {
        manageAdapter = new ManageAdapter(context, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        decoration = new ManageDecoration(context, list);
        recyclerView.setAdapter(manageAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(decoration);

        manageAdapter.setOnClickListener(new ManageAdapter.OnClickListener() {
            @Override
            public void onClick(View view, int position) {

                switch (view.getId()) {
                    case R.id.del:
                        new CommonDialog(context, "是否删除", 0, new CommonDialog.OnClickBtnListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {
                                    DbUtils.getInstance(context).deleteByTimeManage(list.get(position).getAddTime());
                                    getData();
                                }
                                dialog.dismiss();
                            }
                        }).setCancelTxt("取消").setSubmitTxt("确定").show();
                        break;
                    case R.id.rl_shuhui:
                        new CommonDialog(context, "是否更改赎回标记", 0, new CommonDialog.OnClickBtnListener() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if (confirm) {
                                    DbUtils.getInstance(context).updateShuHui(list.get(position).getShuhui()==1?0:1
                                            ,list.get(position).getAddTime());
                                    getData();
                                }
                                dialog.dismiss();
                            }
                        }).setCancelTxt("取消").setSubmitTxt("确定").show();
                        break;
                }

        }
    });
}


    @Override
    protected int getLayout() {
        return R.layout.fragment_manage_money;
    }

    @Override
    public void onResume() {
        super.onResume();

        getData();
        recyclerView.scrollToPosition(0);
    }

    private void getData() {
        ManageBeanResp manageBeanResp = DbUtils.getInstance(context).queryByMonthMange(year, month);
        tvBuy.setText("¥" + manageBeanResp.getSum());

        list = manageBeanResp.getList();

        manageAdapter.upDate(list);
        decoration.upDate(list);
    }

    @OnClick({R.id.ll_date})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_date:
                showDataPicker();
                break;
        }
    }

    private void showDataPicker() {

        new MyDatePicker(context, (datePicker -> {
            year = datePicker.getYear();
            tvYear.setText(year + "年");
            month = (datePicker.getMonth() + 1);
            tvMonth.setText(month + "月");
            getData();

        })).showDataPicker(year, month - 1, 1, false);

    }
}
