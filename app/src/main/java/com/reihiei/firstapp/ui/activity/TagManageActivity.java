package com.reihiei.firstapp.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.reihiei.firstapp.R;
import com.reihiei.firstapp.bean.TagBean;
import com.reihiei.firstapp.db.DbUtils;
import com.reihiei.firstapp.framework.SimpleActivity;
import com.reihiei.firstapp.ui.activity.EditTagActivity;
import com.reihiei.firstapp.ui.apater.TagAdapter;
import com.reihiei.firstapp.widget.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class TagManageActivity extends SimpleActivity {

    @BindView(R.id.parent)
    RelativeLayout parent;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tab_tag)
    TabLayout tabLayout;
    @BindView(R.id.rc_tag)
    RecyclerView recyclerView;

    private PopupWindow popupWindow;
    private int position = 0;
    private List<TagBean> list = new ArrayList<>();
    private TagAdapter tagAdapter;

    private int type = 0;

    @Override
    protected int getLayout() {
        return R.layout.activity_tag;
    }

    @Override
    protected void initEventAndView() {

        tvTitle.setText("标签管理");
//        getData();
        initRecyclerView();
        initTab();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        list = DbUtils.getInstance(mContext).queryByType(position);
        tagAdapter.upDate(list);
    }

    private void initRecyclerView() {
        tagAdapter = new TagAdapter(mContext, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerView.setAdapter(tagAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        tagAdapter.setOnListener(new TagAdapter.OnClickListener() {
            @Override
            public void onEdit(String id,String name,int type) {
                Intent intent = new Intent(mContext,EditTagActivity.class);
                intent.putExtra("id",id);
                intent.putExtra("name",name);
                intent.putExtra("type",type);
                startActivity(intent);
            }

            @Override
            public void onDel(String id) {
                new CommonDialog(mContext, 0, new CommonDialog.OnClickBtnListener() {
                    @Override
                    public void onClick(Dialog dialog, boolean confirm) {
                        if(confirm){
                            DbUtils.getInstance(mContext).deleteTagById(id);
                            DbUtils.getInstance(mContext).deleteByClass(id);
                            getData();
                            dialog.dismiss();
                        }
                    }
                }).setTitle("提示").setContent("使用该标签的记录都会被删除\n是否确定删除").setCancelTxt("取消").setSubmitTxt("确定").show();

            }
        });
    }

    private void initTab() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                position = tab.getPosition();
                getData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @OnClick({R.id.add_tag, R.id.iv_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.add_tag:
                Intent intent = new Intent(mContext, EditTagActivity.class);
                startActivity(intent);
                break;
        }

    }

}
