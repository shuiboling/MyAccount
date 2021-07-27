package com.reihiei.firstapp.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.ManageProductBean;
import com.reihiei.firstapp.db.DbUtils;
import com.reihiei.firstapp.framework.SimpleActivity;
import com.reihiei.firstapp.ui.apater.ManageProductAdapter;
import com.reihiei.firstapp.widget.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ManageProductActivity extends SimpleActivity {

    @BindView(R.id.rc_product)
    RecyclerView recyclerView;

    private String flag = "";
    private int chipType;
    private List<ManageProductBean> manageProductBeanList = new ArrayList<>();
    private ManageProductAdapter manageProductAdapter;


    @Override
    protected int getLayout() {
        return R.layout.activity_manage_product;
    }

    @Override
    protected void initEventAndView() {
        flag = getIntent().getStringExtra("flag");
        chipType = getIntent().getIntExtra("chipType",-1);

        manageProductAdapter = new ManageProductAdapter(mContext,manageProductBeanList);
        recyclerView.setAdapter(manageProductAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);

        manageProductAdapter.setOnListener(new ManageProductAdapter.OnClickListener() {
            @Override
            public void onEdit(ManageProductBean bean) {
                Intent intent = new Intent(mContext,EditManageProductActivity.class);
                intent.putExtra("id",bean.getId());
                intent.putExtra("name",bean.getName());
                intent.putExtra("type",bean.getType());
                intent.putExtra("flag",flag);
                startActivity(intent);

            }

            @Override
            public void onDel(ManageProductBean bean) {
                new CommonDialog(mContext, 0, new CommonDialog.OnClickBtnListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if(confirm){
                            if ("N".equals(flag)){
                                DbUtils.getInstance(mContext).deleteProductById(bean.getId());
                            } else {
                                DbUtils.getInstance(mContext).deleteChannelById(bean.getId());
                            }
                            manageProductBeanList.remove(bean);
                            manageProductAdapter.upDate(manageProductBeanList);
                            dialog.dismiss();
                        }
                    }
                }).setTitle("提示").setContent("使用该数据的记录都会被删除\n是否确定删除").setCancelTxt("取消").setSubmitTxt("确定").show();

            }

            @Override
            public void onClick(ManageProductBean bean) {
                Intent intent = new Intent();
                intent.putExtra("bean",bean);
                if ("N".equals(flag)){
                    setResult(1,intent);
                } else {
                    setResult(2,intent);
                }
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if ("N".equals(flag)){
            manageProductBeanList = DbUtils.getInstance(mContext).queryMP(chipType);
        } else {
            manageProductBeanList = DbUtils.getInstance(mContext).queryMC();
        }
        manageProductAdapter.upDate(manageProductBeanList);
    }

    @OnClick({R.id.iv_back,R.id.add_tag})
    public void OnClick(View view){
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.add_tag:
                Intent intent = new Intent(mContext,EditManageProductActivity.class);
                intent.putExtra("flag",flag);
                startActivity(intent);
                break;
        }
    }


}
