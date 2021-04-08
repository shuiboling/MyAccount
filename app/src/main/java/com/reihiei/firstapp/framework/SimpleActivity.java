package com.reihiei.firstapp.framework;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.reihiei.firstapp.MainApp;
import com.reihiei.firstapp.ui.activity.FingerPrintActivity;
import com.reihiei.firstapp.ui.activity.SafeInActivity;
import com.reihiei.firstapp.util.SpUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;

abstract public class SimpleActivity extends AppCompatActivity {
    private Unbinder unbinder;
    public Context mContext;
    private static int count = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayout());
        unbinder = ButterKnife.bind(this);
        mContext = this;

        initEventAndView();

        initFragments(savedInstanceState);

        MainApp.getInstance().addActivity(this);

    }

    protected void initFragments(Bundle savedInstanceState) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(unbinder != null){
            unbinder.unbind();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(count == 0){
            if( SpUtils.getInstance().getBoolean("finger_switch", false)){
                Intent intent = new Intent(mContext, FingerPrintActivity.class);
                startActivity(intent);

            } else if(!TextUtils.isEmpty(SpUtils.getInstance().getString("password",""))){
                Intent intent = new Intent(mContext, SafeInActivity.class);
                startActivity(intent);
            }
        }
        count ++;

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        count --;

    }

    protected abstract int getLayout();

    protected abstract void initEventAndView();

}
