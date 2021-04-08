package com.reihiei.firstapp.ui.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.reihiei.firstapp.MainApp;
import com.reihiei.firstapp.R;
import com.reihiei.firstapp.framework.SimpleActivity;
import com.reihiei.firstapp.util.CommonDialogUtils;
import com.reihiei.firstapp.util.SpUtils;

import javax.crypto.Cipher;

import butterknife.BindView;

public class FingerPrintActivity extends SimpleActivity {

    @BindView(R.id.layout)
    ConstraintLayout layout;
    @BindView(R.id.parent)
    ConstraintLayout parent;

    FingerprintManager fingerprintManager;
    Cipher cipher;
    CancellationSignal cancellationSignal;
    BiometricPrompt biometricPrompt;

    @Override
    protected int getLayout() {
        return R.layout.activity_finger_print;
    }

    @Override
    protected void onResume() {
        super.onResume();

        cancellationSignal = new CancellationSignal();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            layout.setVisibility(View.GONE);

            startCheckP();
        }
    }

    @Override
    protected void initEventAndView() {

    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void startCheckP() {

        biometricPrompt = new BiometricPrompt.Builder(this)
                .setTitle("请使用指纹登陆")
                .setNegativeButton("取消", mContext.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(mContext, SafeInActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).build();

        biometricPrompt.authenticate(cancellationSignal, getMainExecutor(), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {

                if (errorCode == FingerprintManager.FINGERPRINT_ERROR_NO_FINGERPRINTS) {
                    CommonDialogUtils.showErrorDialog(mContext, errString.toString() + "\n请使用密码登陆", new CommonDialogUtils.DismissListener() {
                        @Override
                        public void dismiss() {
                            SpUtils.getInstance().setBoolean("finger_switch", false);
                            jumpToPw();
                        }
                    });
                }
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                Toast.makeText(mContext, helpString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                Toast.makeText(mContext, "指纹认证成功", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(mContext, "errString", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.exit:
                MainApp.getInstance().exitApp();
                break;
            case R.id.change:
                jumpToPw();
                break;
            case R.id.imageView:
                showDialog();
                break;
        }

    }

    private void showDialog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_finger, null);

        View line = view.findViewById(R.id.vLine);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(line, View.TRANSLATION_X, 0, 220);
        objectAnimator.setRepeatCount(-1);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.setDuration(1000);
        objectAnimator.start();

        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setView(view)
                .setCancelable(false)
                .setNegativeButton("取消", null).create();
        dialog.show();

        cipher = FingerSetActivity.initKeyAndCipher();
        fingerprintManager = mContext.getSystemService(FingerprintManager.class);
        check();
        startListening();

    }


    private void check() {
        if (!fingerprintManager.isHardwareDetected()) {
            CommonDialogUtils.showErrorDialog(mContext, "没有指纹识别设备", new CommonDialogUtils.DismissListener() {
                @Override
                public void dismiss() {
                    jumpToPw();
                    return;
                }
            });
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {
            CommonDialogUtils.showErrorDialog(mContext, "请到系统设置中设置指纹", new CommonDialogUtils.DismissListener() {
                @Override
                public void dismiss() {
                    Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                    startActivity(intent);
                    return;
                }
            });

        }

    }

    public void jumpToPw() {
        Intent intent = new Intent(mContext, SafeInActivity.class);
        startActivity(intent);
        finish();
    }

    public void jumpToSetting() {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
    }

    private void startListening() {

        fingerprintManager.authenticate(new FingerprintManager.CryptoObject(cipher), cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                Toast.makeText(mContext, errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                Toast.makeText(mContext, helpString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                Toast.makeText(mContext, "指纹认证成功", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 300);

            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(mContext, "errString", Toast.LENGTH_SHORT).show();

            }
        }, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        // 停止指纹认证监听
        stopListening();
    }

    private void stopListening() {
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            cancellationSignal = null;
        }
    }


}
