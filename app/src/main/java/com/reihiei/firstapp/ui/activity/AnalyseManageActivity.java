package com.reihiei.firstapp.ui.activity;

import android.content.Intent;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.AnalyseManageBeanResp;
import com.reihiei.firstapp.db.DbUtils;
import com.reihiei.firstapp.framework.SimpleActivity;
import com.reihiei.firstapp.ui.apater.AnalyseManageAdapter;
import com.reihiei.firstapp.widget.LoadPageLayout;

import butterknife.BindView;
import butterknife.OnClick;

public class AnalyseManageActivity extends SimpleActivity implements LoadPageLayout.LoadListener {

    @BindView(R.id.load)
    LoadPageLayout loadPageLayout;
    @BindView(R.id.rc_module)
    RecyclerView recyclerView;
    @BindView(R.id.tv_sum)
    TextView tvSum;
    @BindView(R.id.tv_title)
    TextView title;

    private int year,month;
    private AnalyseManageBeanResp resp;

    private LinearLayoutManager linearLayoutManager;
    private AnalyseManageAdapter analyseManageAdapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_analyse_manage;
    }

    @Override
    protected void initEventAndView() {

        year = getIntent().getIntExtra("year",0);
        month = getIntent().getIntExtra("month",0);

        resp = DbUtils.getInstance(mContext).queryByMonthMangeClassify(year,month);

        title.setText(month+"月理财分析");
        tvSum.setText("¥"+resp.getSum());

        analyseManageAdapter = new AnalyseManageAdapter(mContext,resp);
        linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setAdapter(analyseManageAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadPageLayout.setLoadListener(this);
    }



    @Override
    public void loadFinish() {
        Intent intent = new Intent(mContext,AnalyseActivity.class);
        intent.putExtra("outMoney",getIntent().getStringExtra("outMoney"));
        intent.putExtra("inMoney",getIntent().getStringExtra("inMoney"));
        intent.putExtra("year",year);
        intent.putExtra("month",month);
        intent.putExtra("total",getIntent().getFloatExtra("total", 0));
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.iv_back)
    public void onClick(){
        finish();
    }
}
