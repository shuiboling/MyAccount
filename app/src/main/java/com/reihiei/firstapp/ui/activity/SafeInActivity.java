package com.reihiei.firstapp.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.reihiei.firstapp.MainApp;
import com.reihiei.firstapp.R;
import com.reihiei.firstapp.framework.SimpleActivity;
import com.reihiei.firstapp.util.SpUtils;
import com.reihiei.firstapp.widget.PassWord;

import butterknife.BindView;

public class SafeInActivity extends SimpleActivity {

    @BindView(R.id.pw)
    PassWord pw;
    @BindView(R.id.gr)
    GridLayout gridLayout;

    @Override
    protected int getLayout() {
        return R.layout.activity_safe;
    }

    @Override
    protected void initEventAndView() {

        pw.setPwInterface(new PassWord.PwInterface() {
            @Override
            public void jumpTo() {
                finish();

            }
        });

        for (int i = 0; i < gridLayout.getChildCount(); i++){
            int finalI = i;
            gridLayout.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.del:
                            pw.delNum();
                            break;
                        case R.id.back:
                            MainApp.getInstance().exitApp();
                            break;
                        default:
                            TextView textView = (TextView) gridLayout.getChildAt(finalI);
                            pw.inputNum(textView.getText().toString());
                    }

                }
            });
        }
        if(TextUtils.isEmpty(SpUtils.getInstance().getString("password",""))){

            finish();
        } else {
            pw.setPw(SpUtils.getInstance().getString("password",""));
        }
    }

}
