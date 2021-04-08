package com.reihiei.firstapp.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.framework.SimpleFragment;
import com.reihiei.firstapp.ui.activity.FingerPrintActivity;
import com.reihiei.firstapp.ui.activity.FingerSetActivity;
import com.reihiei.firstapp.ui.activity.PasswordActivity;
import com.reihiei.firstapp.ui.activity.TagManageActivity;

import butterknife.OnClick;

public class MineFragment extends SimpleFragment {

    @Override
    protected void initEventAndView() {

    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_mine;
    }

    @OnClick({R.id.rl_tag,R.id.rl_data,R.id.rl_password,R.id.rl_finger,R.id.rl_gesture})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.rl_tag:
                Intent intent = new Intent(context, TagManageActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_password:
                Intent intentPwd = new Intent(context, PasswordActivity.class);
                startActivity(intentPwd);
                break;
            case R.id.rl_finger:
                Intent intentFinger = new Intent(context, FingerSetActivity.class);
                startActivity(intentFinger);
                break;
            default:
                Toast.makeText(context,"没空做啊",Toast.LENGTH_SHORT).show();
        }

    }
}
