package com.reihiei.firstapp.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.db.DbUtils;
import com.reihiei.firstapp.framework.SimpleActivity;
import com.reihiei.firstapp.ui.fragment.AccountFragment;
import com.reihiei.firstapp.ui.fragment.ChartFragment;
import com.reihiei.firstapp.ui.fragment.ManageMoneyFragment;
import com.reihiei.firstapp.ui.fragment.MineFragment;
import com.reihiei.firstapp.util.SpUtils;
import com.reihiei.firstapp.widget.BottomTab;
import com.reihiei.firstapp.widget.CommonDialog;
import com.reihiei.firstapp.widget.MyBottomWidget;

import butterknife.BindView;

public class HomeActivity extends SimpleActivity implements MyBottomWidget.onItemClickListener {

    @BindView(R.id.fl_content)
    FrameLayout flContent;
    @BindView(R.id.bottom_bar)
    MyBottomWidget myBottomWidget;

    private String tag1 = "account";
    private String tag2 = "manage";
    private String tag3 = "chart";
    private String tag4 = "mine";

    AccountFragment accountFragment;
    ChartFragment chartFragment;
    ManageMoneyFragment manageMoneyFragment;
    MineFragment mineFragment;

    @Override
    public int getLayout() {
        return R.layout.activity_home;
    }

    @Override
    public void initEventAndView() {

        if(SpUtils.getInstance().getBoolean("first_in",true)){

            new CommonDialog(mContext,"是否设置密码",0,((dialog, confirm) -> {

                if(confirm){
                    Intent intent = new Intent(mContext, PasswordActivity.class);
                    startActivity(intent);
                    dialog.dismiss();
                }

            })).setSubmitTxt("确定").setCancelTxt("取消").show();

            SpUtils.getInstance().setBoolean("first_in",false);

        }

        myBottomWidget.post(new Runnable() {
            @Override
            public void run() {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) flContent.getLayoutParams();
                layoutParams.bottomMargin = myBottomWidget.getMinHeight();
                flContent.setLayoutParams(layoutParams);
            }
        });
        initBottomBar();
    }

    private void initBottomBar() {
        myBottomWidget.addItem(new BottomTab(mContext,R.drawable.mingxi,"明细",false))
                .addItem(new BottomTab(mContext,R.drawable.jilicai,"理财",false))
                .addItem(new BottomTab(mContext,R.drawable.ic_add_black_50dp,"添加",true))
                .addItem(new BottomTab(mContext,R.drawable.tongji,"统计",false))
                .addItem(new BottomTab(mContext,R.drawable.wo,"我",false));

        myBottomWidget.setOnItemClickListener(this);
    }

    @Override
    protected void initFragments(Bundle savedInstanceState) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (savedInstanceState != null) {
            accountFragment = (AccountFragment) fragmentManager.getFragment(savedInstanceState, tag1);
            manageMoneyFragment = (ManageMoneyFragment) fragmentManager.getFragment(savedInstanceState, tag2);
            chartFragment = (ChartFragment) fragmentManager.getFragment(savedInstanceState, tag3);
            mineFragment = (MineFragment) fragmentManager.getFragment(savedInstanceState,tag4);
        } else {
            accountFragment = new AccountFragment();

            fragmentTransaction.add(R.id.fl_content, accountFragment);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onClick(View view) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        switch (view.getId()) {
            case 0:
                hideFragment(fragmentTransaction);
                if (accountFragment == null) {
                    accountFragment = new AccountFragment();
                    fragmentTransaction.add(R.id.fl_content, accountFragment);
                } else {
                    fragmentTransaction.show(accountFragment);
                }

                break;
            case 1:
                hideFragment(fragmentTransaction);
                if (manageMoneyFragment == null) {
                    manageMoneyFragment = new ManageMoneyFragment();
                    fragmentTransaction.add(R.id.fl_content, manageMoneyFragment);
                } else {
                    fragmentTransaction.show(manageMoneyFragment);
                }
                break;
            case 2:
                Intent intent = new Intent(mContext, AddCountActivity.class);
                startActivity(intent);
                break;
            case 3:
                hideFragment(fragmentTransaction);
                if (chartFragment == null) {
                    chartFragment = new ChartFragment();
                    fragmentTransaction.add(R.id.fl_content, chartFragment);
                } else {
                    fragmentTransaction.show(chartFragment);
                }
                break;
            case 4:
                hideFragment(fragmentTransaction);
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                    fragmentTransaction.add(R.id.fl_content, mineFragment);
                } else {
                    fragmentTransaction.show(mineFragment);
                }
                break;
        }

        fragmentTransaction.commit();
    }

    private void hideFragment(FragmentTransaction fragmentTransaction) {

        if (accountFragment != null) {
            fragmentTransaction.hide(accountFragment);
        }

        if (manageMoneyFragment != null) {
            fragmentTransaction.hide(manageMoneyFragment);
        }

        if (chartFragment != null) {
            fragmentTransaction.hide(chartFragment);
        }

        if (mineFragment != null) {
            fragmentTransaction.hide(mineFragment);
        }
    }

    @Override
    protected void onDestroy() {
        DbUtils.getInstance(mContext).closeDb();
        super.onDestroy();
    }
}
