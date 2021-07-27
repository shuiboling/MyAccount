package com.reihiei.firstapp.ui.activity;

import android.app.Dialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.ChipGroup;
import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.TagBean;
import com.reihiei.firstapp.db.DbUtils;
import com.reihiei.firstapp.framework.SimpleActivity;
import com.reihiei.firstapp.widget.CommonDialog;

import butterknife.BindView;
import butterknife.OnClick;

public class EditTagActivity extends SimpleActivity {

    @BindView(R.id.et_name)
    EditText editText;
    @BindView(R.id.chipgroup)
    ChipGroup chipGroup;
    @BindView(R.id.tv2)
    TextView textView;

    private int type = -1;
    private String id;
    private String name;
    private boolean isEdit = false;

    @Override
    protected int getLayout() {
        return R.layout.activity_manage_tag;
    }

    @Override
    protected void initEventAndView() {

        if(getIntent().hasExtra("id")){
            chipGroup.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);

            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            type = getIntent().getIntExtra("type",0);

            editText.setText(name);

            isEdit = true;
        }else {
            chipGroup.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);

            isEdit = false;
        }

        chipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup chipGroup, int i) {
                switch (i) {
                    case R.id.chip1:
                        type = 0;
                        break;
                    case R.id.chip2:
                        type = 1;
                        break;
                    case R.id.chip3:
                        type = 2;
                        break;
                }
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
                if(isEdit){
                    name = editText.getText().toString();
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(mContext, "请输入标签名称", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new CommonDialog(mContext, 0, new CommonDialog.OnClickBtnListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {

                                if (DbUtils.getInstance(mContext).queryTagByName(name, type)) {
                                    Toast.makeText(mContext, "该标签已存在", Toast.LENGTH_SHORT).show();
                                } else {
                                    DbUtils.getInstance(mContext).updateTagById(id,name);
                                    finish();
                                }
                                dialog.dismiss();
                            }
                        }
                    }).setTitle("提示").setContent("是否修改").setCancelTxt("取消").setSubmitTxt("确定").show();

                } else {
                    String name = editText.getText().toString();
                    if (TextUtils.isEmpty(name)) {
                        Toast.makeText(mContext, "请输入标签名称", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (type == -1) {
                        Toast.makeText(mContext, "请选择标签类型", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new CommonDialog(mContext, 0, new CommonDialog.OnClickBtnListener() {
                        @Override
                        public void onClick(Dialog dialog, boolean confirm) {
                            if (confirm) {
                                TagBean tagBean = new TagBean();
                                tagBean.setType(type);
                                tagBean.setName(name);
                                if (DbUtils.getInstance(mContext).queryTagByName(name, type)) {
                                    Toast.makeText(mContext, "已添加过改标签", Toast.LENGTH_SHORT).show();
                                } else {
                                    DbUtils.getInstance(mContext).addTag(tagBean);
                                    finish();
                                }
                                dialog.dismiss();
                            }
                        }
                    }).setTitle("提示").setContent("是否添加").setCancelTxt("取消").setSubmitTxt("确定").show();

                }

                break;
        }

    }
}
