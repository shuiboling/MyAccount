package com.reihiei.firstapp.ui.activity;

import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.ManageProductBean;
import com.reihiei.firstapp.db.DbUtils;
import com.reihiei.firstapp.framework.SimpleActivity;
import com.reihiei.firstapp.widget.CommonDialog;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class EditManageProductActivity extends SimpleActivity {

    @BindView(R.id.chipgroup)
    ChipGroup chipGroup;
    @BindView(R.id.et_name)
    EditText editText;
    @BindView(R.id.tv)
    TextView textView;

    private boolean isEdit;
    private String name;
    private int id;
    private int type = -1;
    private List<Integer> idList = Arrays.asList(R.id.type1, R.id.type2, R.id.type3, R.id.type4, R.id.type5);
    private String flag = "";

    @Override
    protected int getLayout() {
        return R.layout.activity_edit_manage_product;
    }

    @Override
    protected void initEventAndView() {

        flag = getIntent().getStringExtra("flag");

        if (getIntent().hasExtra("id")) {

            id = getIntent().getIntExtra("id", 0);
            name = getIntent().getStringExtra("name");
            type = getIntent().getIntExtra("type", 0);
            editText.setText(name);

            if ("N".equals(flag)) {
                Chip chip = findViewById(idList.get(type));
                chip.setChecked(true);
            }

            isEdit = true;
        } else {

            isEdit = false;
        }

        if ("N".equals(flag)) {
            textView.setVisibility(View.VISIBLE);
            chipGroup.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
            chipGroup.setVisibility(View.GONE);
        }

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                type = idList.indexOf(i);
            }
        });
    }

    @OnClick({R.id.cancel, R.id.confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.confirm:
                name = editText.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(mContext, "请输入名称", Toast.LENGTH_SHORT).show();
                    return;
                }
                if ("N".equals(flag) && type == -1) {
                    Toast.makeText(mContext, "请选择产品类型", Toast.LENGTH_SHORT).show();
                    return;
                }
                new CommonDialog(mContext, 0, new CommonDialog.OnClickBtnListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if (confirm) {

                            if ("N".equals(flag)) {
                                if (DbUtils.getInstance(mContext).queryMPByName(name, type)) {
                                    Toast.makeText(mContext, "该产品已存在", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (isEdit) {
                                        DbUtils.getInstance(mContext).updateMPById(id, name, type);
                                    } else {
                                        ManageProductBean bean = new ManageProductBean(id, name, type);
                                        DbUtils.getInstance(mContext).addMP(bean);
                                    }
                                    finish();
                                }
                            } else {
                                if (DbUtils.getInstance(mContext).queryMCByName(name)) {
                                    Toast.makeText(mContext, "该渠道已存在", Toast.LENGTH_SHORT).show();
                                } else {
                                    if (isEdit) {
                                        DbUtils.getInstance(mContext).updateMCById(id, name);
                                    } else {
                                        ManageProductBean bean = new ManageProductBean(id, name, -1);
                                        DbUtils.getInstance(mContext).addMC(bean);
                                    }
                                    finish();
                                }
                            }

                        }
                        dialog.dismiss();
                    }

                }).setTitle("提示").setContent("是否添加/修改").setCancelTxt("取消").setSubmitTxt("确定").show();
                break;
        }
    }
}
