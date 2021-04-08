package com.reihiei.firstapp.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.framework.SimpleActivity;
import com.reihiei.firstapp.util.CommonDialogUtils;
import com.reihiei.firstapp.util.SpUtils;
import com.reihiei.firstapp.widget.CommonDialog;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.OnClick;

public class PasswordActivity extends SimpleActivity {

    @BindView(R.id.old)
    RelativeLayout old;
    @BindView(R.id.password)
    EditText newPwd;
    @BindView(R.id.confirm_pwd)
    EditText confirmPwd;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.old_pwd)
    EditText oldPwd;
    @BindView(R.id.rl_switch)
    RelativeLayout rlSwitch;
    @BindView(R.id.switchBtn)
    Switch aSwitch;
    @BindView(R.id.ll_set_pw)
    LinearLayout llSetPw;

    @Override
    protected int getLayout() {
        return R.layout.activity_password;
    }

    @Override
    protected void initEventAndView() {

        title.setText("密码设置");

        if(TextUtils.isEmpty(SpUtils.getInstance().getString("password",""))){
            aSwitch.setChecked(false);
            llSetPw.setVisibility(View.GONE);
        }else {
            aSwitch.setChecked(true);
            llSetPw.setVisibility(View.VISIBLE);
        }

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if( isChecked){
                    llSetPw.setVisibility(View.VISIBLE);
                    old.setVisibility(View.GONE);
                }else {
                    llSetPw.setVisibility(View.GONE);
                }
            }
        });
    }

    @OnClick({R.id.btn,R.id.iv_back})
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn:
                if(aSwitch.isChecked()){
                    setPw();
                } else {
                    SpUtils.getInstance().setString("password","");
                    CommonDialogUtils.showErrorDialog(mContext,"密码登陆已关闭",()->{
                        finish();
                    });

                }

                break;
        }

    }

    private void setPw() {
        String oldPwdStr = SpUtils.getInstance().getString("password", "");
        if (!TextUtils.isEmpty(oldPwdStr)) {
            if(!oldPwd.getText().toString().equals(oldPwdStr)){
                Toast.makeText(mContext,"旧密码输入错误",Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if(TextUtils.isEmpty(newPwd.getText().toString()) || !newPwd.getText().toString().equals(confirmPwd.getText().toString())){
            Toast.makeText(mContext,"设置密码为空或两次密码输入不一致",Toast.LENGTH_SHORT).show();
            return;
        }

        SpUtils.getInstance().setString("password", newPwd.getText().toString());

        new CommonDialog(mContext,  "密码设置成功", 0,(dialog, confirm) -> {

            if (confirm) {
                dialog.dismiss();
                finish();
            }
        })
                .setSubmitTxt("确定")
                .setTitle("提示")
                .show();
    }
}
