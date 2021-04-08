package com.reihiei.firstapp.util;

import android.content.Context;

import com.reihiei.firstapp.widget.CommonDialog;

public class CommonDialogUtils {

    public interface DismissListener {
        public void dismiss();
    }
    public static void showErrorDialog(Context context, String errMsg,DismissListener dismissListener){
        new CommonDialog(context,  errMsg, 0,(dialog, confirm) -> {

            if (confirm) {
                if(dismissListener != null){
                    dismissListener.dismiss();
                }

                    dialog.dismiss();

            }
        })
                .setSubmitTxt("确定")
                .setTitle("提示")
                .show();
    }
}
