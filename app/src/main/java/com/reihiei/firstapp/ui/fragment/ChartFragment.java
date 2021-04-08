package com.reihiei.firstapp.ui.fragment;

import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.AccountBean;
import com.reihiei.firstapp.db.DbUtils;
import com.reihiei.firstapp.framework.SimpleFragment;
import com.google.android.material.tabs.TabLayout;
import com.reihiei.firstapp.ui.apater.ChartAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;

public class ChartFragment extends SimpleFragment {
    @BindView(R.id.tab_1st)
    TabLayout tab_1st;
    @BindView(R.id.tab_2nd)
    TabLayout tab_2nd;
    @BindView(R.id.rc_list)
    RecyclerView rcList;

    private int tab1Pos = 0,tab2PosYear = 0,tab2Pos = 0;
    private int[] years;
    private ChartAdapter chartAdapter;
    private List<AccountBean> yearList = new ArrayList<>();
    private List<AccountBean> monthList = new ArrayList<>();

    @Override
    protected void initEventAndView() {
        Calendar calendar = Calendar.getInstance();
        tab2PosYear = calendar.get(Calendar.YEAR);
        initTab1st();
        initTab2nd();
        initRecyclerView();
    }

    private void initTab1st() {
        tab_1st.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab1Pos = tab.getPosition();

                if(tab1Pos == 0){
                    tab_2nd.setVisibility(View.GONE);
                    yearList = DbUtils.getInstance(context).queryInAndOutByYear();
                    chartAdapter.upDate(yearList);

                } else {
                    if(years != null && years.length>0) {
                        monthList = DbUtils.getInstance(context).queryInAndOutByMonth(years[0]);
                        tab_2nd.setVisibility(View.VISIBLE);
                        tab_2nd.removeAllTabs();
                        for(int i:years) {
                            if(i == tab2PosYear){
                                tab_2nd.addTab(tab_2nd.newTab().setText(i+"年"),true);
                            }else {
                                tab_2nd.addTab(tab_2nd.newTab().setText(i+"年"),false);

                            }
                        }
                    }

                    chartAdapter.upDate(monthList);

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void initTab2nd() {

        tab_2nd.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab2Pos = tab.getPosition();
                tab2PosYear = years[tab.getPosition()];

                monthList = DbUtils.getInstance(context).queryInAndOutByMonth(tab2PosYear);
                chartAdapter.upDate(monthList);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initRecyclerView() {

        chartAdapter = new ChartAdapter(context,yearList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rcList.setLayoutManager(linearLayoutManager);
        rcList.setAdapter(chartAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();

        getDate();
    }

    //hide\show走这个方法回调，不会走生命周期的方法
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if(!hidden){
            getDate();
        }
    }

    private void getDate(){
        years = DbUtils.getInstance(context).queryYear();

        if(tab1Pos == 0){
            yearList = DbUtils.getInstance(context).queryInAndOutByYear();
            chartAdapter.upDate(yearList);
        } else {
            tab_2nd.removeAllTabs();
            for(int i:years) {
                if(i == tab2PosYear){
                    tab_2nd.addTab(tab_2nd.newTab().setText(i+"年"),true);
                }else {
                    tab_2nd.addTab(tab_2nd.newTab().setText(i+"年"),false);

                }
            }
            monthList = DbUtils.getInstance(context).queryInAndOutByMonth(tab2PosYear);
            chartAdapter.upDate(monthList);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_chart;
    }
}
