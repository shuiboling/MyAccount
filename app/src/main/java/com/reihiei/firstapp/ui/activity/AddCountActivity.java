package com.reihiei.firstapp.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.AccountBean;
import com.reihiei.firstapp.bean.ManageBean;
import com.reihiei.firstapp.bean.TagBean;
import com.reihiei.firstapp.db.DbUtils;
import com.reihiei.firstapp.framework.SimpleActivity;
import com.reihiei.firstapp.widget.CommonDialog;
import com.reihiei.firstapp.widget.CountPop;
import com.reihiei.firstapp.widget.MyDatePicker;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class AddCountActivity extends SimpleActivity {
    @BindView(R.id.chip_group)
    ChipGroup chipGroup;
    @BindView(R.id.tab)
    TabLayout tabLayout;
    @BindView(R.id.money)
    TextView etMoney;
    @BindView(R.id.remark)
    EditText etRemark;
    @BindView(R.id.date)
    TextView tvDate;
    @BindView(R.id.sc_parent)
    ScrollView parent;
    @BindView(R.id.ll_manage)
    LinearLayout llMange;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_channel)
    EditText etChannel;
    @BindView(R.id.ll_remark)
    LinearLayout llRemark;

    private int position = 0;
    private int chipType = -1;
    private int year, month, day;
    private List<TagBean> tagBeans;
    private String[] names;


    @Override
    protected void initEventAndView() {

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DATE);

        tvDate.setText(year + "-" + month + "-" + day);

        initTab();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initChipGroup();
    }

    private void initTab() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                hideInput();
                position = tab.getPosition();
                if (position == 2) {
                    llRemark.setVisibility(View.GONE);
                    llMange.setVisibility(View.VISIBLE);
                } else {
                    llRemark.setVisibility(View.VISIBLE);
                    llMange.setVisibility(View.GONE);
                }
                initChipGroup();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initChipGroup() {

        TypedArray icons;

        if (position == 0) {
            names = getResources().getStringArray(R.array.out_type_name);
            icons = getResources().obtainTypedArray(R.array.out_type_icon);
        } else if (position == 1) {
            names = getResources().getStringArray(R.array.in_type_name);
            icons = getResources().obtainTypedArray(R.array.in_type_icon);
        } else {
            names = getResources().getStringArray(R.array.manage_type_name);
            icons = getResources().obtainTypedArray(R.array.manage_type_icon);
        }

        tagBeans = DbUtils.getInstance(mContext).queryByType(position);
        List<String> nameList = new ArrayList<>(Arrays.asList(names));  //Arrays.asList不能修改
        for (TagBean bean : tagBeans) {
            nameList.add(bean.getName());
        }

        if (chipGroup != null) {
            chipGroup.removeAllViews();
        }
        for (int i = 0; i < nameList.size(); i++) {
            View view = View.inflate(mContext, R.layout.item_chip, null);
            Chip chip = view.findViewById(R.id.chip);

            chip.setText(nameList.get(i));
            if (i + 1 > names.length) {
                chip.setId(Integer.valueOf(tagBeans.get(i - names.length).getId()));
                chip.setChipIcon(getResources().getDrawable(R.drawable.chun, null));
            } else {
                chip.setId(i);
                chip.setChipIcon(getResources().getDrawable(icons.getResourceId(i, 0), null));
            }
            chip.setChipIconSize(50);
            chipGroup.addView(view, i);
        }
        chipGroup.setSingleSelection(true);

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                hideInput();
                chipType = i;
                Log.d("zyy", chipType + "");

            }
        });
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_add;
    }

    @OnClick({R.id.btn, R.id.date, R.id.iv_back, R.id.money, R.id.iv_manage})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                if (position == 2) {
                    addMange();
                } else {
                    addRecord();
                }
                break;

            case R.id.date:

                new MyDatePicker(mContext, datePicker -> {

                    year = datePicker.getYear();
                    month = datePicker.getMonth() + 1;
                    day = datePicker.getDayOfMonth();

                    tvDate.setText(year + "-" + month + "-" + day);

                }).showDataPicker(year, month - 1, day, true);
                break;

            case R.id.iv_back:
                finish();
                break;
            case R.id.money:
                hideInput();
                showPop();
                break;
            case R.id.iv_manage:
                Intent intent = new Intent(mContext, TagManageActivity.class);
                startActivity(intent);
                break;
        }

    }

    private void showPop() {

        CountPop countPop = new CountPop(mContext, new CountPop.OnDismissListener() {
            @Override
            public void dismiss(String num) {
                float n = Float.valueOf(num);
                etMoney.setText(String.format("%.2f",n));
            }
        });
        countPop.showPop(parent, Gravity.BOTTOM, 0, 0);
    }

    private void addRecord() {
        AccountBean accountBean = new AccountBean();
        accountBean.setType(position);
        accountBean.setYear(year);
        accountBean.setMonth(month);
        accountBean.setDay(day);
        if (TextUtils.isEmpty(etMoney.getText().toString())) {
            Toast.makeText(mContext, "金额不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(tvDate.getText().toString())) {
            Toast.makeText(mContext, "日期不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (position == 0) {
            accountBean.setOutMoney(etMoney.getText().toString());
            accountBean.setInMoney("0");
        } else {
            accountBean.setInMoney(etMoney.getText().toString());
            accountBean.setOutMoney("0");
        }
        if (TextUtils.isEmpty(etRemark.getText().toString())) {
            accountBean.setRemark("");
        } else {
            accountBean.setRemark(etRemark.getText().toString());
        }
        if (chipType == -1) {
            Toast.makeText(mContext, "请选择类别", Toast.LENGTH_SHORT).show();
            return;
        }
        new CommonDialog(mContext, 0, new CommonDialog.OnClickBtnListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    Date date = new Date();
                    String times = date.getTime() + "";
                    accountBean.setAddTime(times);
                    accountBean.setClassify(chipType);
                    DbUtils.getInstance(mContext).addAccount(accountBean);
                    dialog.dismiss();
                    finish();
                }
            }
        }).setTitle("提示").setContent("是否添加").setCancelTxt("取消").setSubmitTxt("确定").show();
    }

    private void addMange() {
        ManageBean manageBean = new ManageBean();
        manageBean.setYear(year);
        manageBean.setMonth(month);
        manageBean.setDay(day);
        if (TextUtils.isEmpty(etMoney.getText().toString())) {
            Toast.makeText(mContext, "金额不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(tvDate.getText().toString())) {
            Toast.makeText(mContext, "日期不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(etName.getText().toString())) {
            Toast.makeText(mContext, "产品名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(etChannel.getText().toString())) {
            Toast.makeText(mContext, "渠道不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        manageBean.setMoney(etMoney.getText().toString());
        manageBean.setName(etName.getText().toString());
        manageBean.setChannel(etChannel.getText().toString());

        if (chipType == -1) {
            Toast.makeText(mContext, "请选择类别", Toast.LENGTH_SHORT).show();
            return;
        }
        new CommonDialog(mContext, 0, new CommonDialog.OnClickBtnListener() {
            @Override
            public void onClick(Dialog dialog, boolean confirm) {
                if (confirm) {
                    Date date = new Date();
                    String times = date.getTime() + "";
                    manageBean.setAddTime(times);
                    manageBean.setClassify(chipType);
                    DbUtils.getInstance(mContext).addManage(manageBean);
                    dialog.dismiss();
                    finish();
                }
            }
        }).setTitle("提示").setContent("是否添加").setCancelTxt("取消").setSubmitTxt("确定").show();
    }

    public void hideInput() {
        //判断软键盘是否弹起，如果弹出状态把它隐藏
        InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (mInputMethodManager != null) {
            if (mInputMethodManager.isActive(etRemark)) {
                mInputMethodManager.hideSoftInputFromWindow(etRemark.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
            } else if (mInputMethodManager.isActive(etName)) {
                mInputMethodManager.hideSoftInputFromWindow(etName.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
            } else if (mInputMethodManager.isActive(etChannel)) {
                mInputMethodManager.hideSoftInputFromWindow(etChannel.getWindowToken(), WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED);
            }
        }
    }

}
