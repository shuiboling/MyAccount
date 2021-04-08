package com.reihiei.firstapp.ui.activity;

import android.content.Intent;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.MergeAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.AccountBean;
import com.reihiei.firstapp.db.DbUtils;
import com.reihiei.firstapp.framework.SimpleActivity;
import com.reihiei.firstapp.ui.apater.AnalyseInAdapter;
import com.reihiei.firstapp.ui.apater.AnalyseOutAdapter;
import com.reihiei.firstapp.widget.LoadPageLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AnalyseActivity extends SimpleActivity implements LoadPageLayout.LoadListener {

    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.rc_module)
    RecyclerView rcModule;
    @BindView(R.id.tv_sum)
    TextView sum;
    @BindView(R.id.in)
    TextView in;
    @BindView(R.id.out)
    TextView out;
    @BindView(R.id.load)
    LoadPageLayout loadPageLayout;

    private int year, month;
    private List<AccountBean> outBeanList,inBeanList;
    private float sumOutMoney,sumInMoney;

    private LinearLayoutManager linearLayoutManager;
    private AnalyseOutAdapter analyseOutAdapter;
    private AnalyseInAdapter analyseInAdapter;

    @Override
    protected void initEventAndView() {

        year = getIntent().getIntExtra("year", 0);
        month = getIntent().getIntExtra("month", 0);
        sumOutMoney = Float.valueOf(getIntent().getStringExtra("outMoney"));
        sumInMoney  = Float.valueOf(getIntent().getStringExtra("inMoney"));

        title.setText(month+"月收支分析");

        in.setText("+¥" + getIntent().getStringExtra("inMoney"));
        out.setText("-¥" + getIntent().getStringExtra("outMoney"));
        float total = getIntent().getFloatExtra("total", 0);
        if (total >= 0) {
            sum.setText("+¥" + Math.abs(total));

        } else {
            sum.setText("-¥" + Math.abs(total));

        }

        outBeanList = DbUtils.getInstance(mContext).queryClassifyOutByMonth(year, month,0);
        inBeanList = DbUtils.getInstance(mContext).queryClassifyOutByMonth(year, month,1);

        linearLayoutManager = new LinearLayoutManager(mContext);
        analyseOutAdapter = new AnalyseOutAdapter(outBeanList,mContext,sumOutMoney);
        analyseInAdapter = new AnalyseInAdapter(inBeanList,mContext,sumInMoney);

        MergeAdapter mergeAdapter = new MergeAdapter(analyseInAdapter,analyseOutAdapter);
        rcModule.setLayoutManager(linearLayoutManager);
        rcModule.setAdapter(mergeAdapter);

        loadPageLayout.setLoadListener(this);

    }

    @Override
    protected int getLayout() {
        return R.layout.activity_analyse_account;
    }

    @OnClick(R.id.iv_back)
    public void onClick(){
        finish();
    }

    @Override
    public void loadFinish() {
        Intent intent = new Intent(mContext,AnalyseManageActivity.class);
        intent.putExtra("outMoney",sumOutMoney+"");
        intent.putExtra("inMoney",sumInMoney+"");
        intent.putExtra("year",year);
        intent.putExtra("month",month);
        intent.putExtra("total",getIntent().getFloatExtra("total", 0));
        startActivity(intent);
        finish();
    }
}
