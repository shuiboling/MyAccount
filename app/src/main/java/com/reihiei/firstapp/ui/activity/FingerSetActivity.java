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
import android.provider.Settings;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.reihiei.firstapp.R;
import com.reihiei.firstapp.framework.SimpleActivity;
import com.reihiei.firstapp.util.CommonDialogUtils;
import com.reihiei.firstapp.util.SpUtils;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import butterknife.BindView;
import butterknife.OnClick;

public class FingerSetActivity extends SimpleActivity {

    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.switchBtn)
    Switch aSwitch;

    FingerprintManager fingerprintManager;
    Cipher cipher;
    CancellationSignal cancellationSignal;

    private static final String DEFAULT_KEY_NAME = "default_key";

    @Override
    protected int getLayout() {
        return R.layout.activity_set_finger;
    }

    @Override
    protected void onResume() {
        super.onResume();
        cancellationSignal = new CancellationSignal();

    }

    @Override
    protected void initEventAndView() {

        title.setText("设置指纹登陆");

        aSwitch.setChecked(SpUtils.getInstance().getBoolean("finger_switch", false));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    if (TextUtils.isEmpty(SpUtils.getInstance().getString("password", ""))) {

                        CommonDialogUtils.showErrorDialog(mContext, "请先设置密码", () -> {

                            Intent intent = new Intent(mContext, PasswordActivity.class);
                            startActivity(intent);

                        });
                        aSwitch.setChecked(false);
                        return;
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        startCheckP();
                    } else {
                        startCheckM();
                    }
                } else {
                    SpUtils.getInstance().setBoolean("finger_switch", false);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void startCheckP() {

        BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(this)
                .setTitle("请验证指纹")
                .setNegativeButton("取消", mContext.getMainExecutor(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        aSwitch.setChecked(false);
                    }
                }).build();

        biometricPrompt.authenticate(cancellationSignal, getMainExecutor(), new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {

                aSwitch.setChecked(false);
                if (errorCode == FingerprintManager.FINGERPRINT_ERROR_NO_FINGERPRINTS) {
                    doNotHaveFingerData(errString + "\n");

                } else {
                    CommonDialogUtils.showErrorDialog(mContext, errString.toString(), null);
                }
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                Toast.makeText(mContext, helpString, Toast.LENGTH_SHORT).show();
                aSwitch.setChecked(false);
            }

            @Override
            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                fingerSuccess();
            }

            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(mContext, "errString", Toast.LENGTH_SHORT).show();
                aSwitch.setChecked(false);
            }
        });
    }

    private void fingerSuccess() {
        CommonDialogUtils.showErrorDialog(mContext, "指纹认证成功", () -> {

            SpUtils.getInstance().setBoolean("finger_switch", true);
            finish();
        });
    }

    private void doNotHaveFingerData(String errString) {
        CommonDialogUtils.showErrorDialog(mContext, errString + "请到系统设置中设置指纹密码", () -> {

            jumpToSetting();
        });
    }

    private void startCheckM() {
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
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        aSwitch.setChecked(false);
                    }
                }).create();
        dialog.show();

        initKeyAndCipher();
        fingerprintManager = mContext.getSystemService(FingerprintManager.class);
        check();
        startListening();
    }

    public static Cipher initKeyAndCipher() {
        try {
            KeyStore keyStore;
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(DEFAULT_KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);
            keyGenerator.init(builder.build());
            keyGenerator.generateKey();

            SecretKey key = null;

            key = (SecretKey) keyStore.getKey(DEFAULT_KEY_NAME, null);
            Cipher cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher;
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
        return null;

    }

    private void check() {
        if (!fingerprintManager.isHardwareDetected()) {
            CommonDialogUtils.showErrorDialog(mContext, "没有指纹识别设备", new CommonDialogUtils.DismissListener() {
                @Override
                public void dismiss() {
                    aSwitch.setChecked(false);
                    return;
                }
            });
        } else if (!fingerprintManager.hasEnrolledFingerprints()) {

            doNotHaveFingerData("");
            aSwitch.setChecked(false);
            return;

        }

    }

    private void startListening() {

        fingerprintManager.authenticate(new FingerprintManager.CryptoObject(cipher), cancellationSignal, 0, new FingerprintManager.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {

                aSwitch.setChecked(false);
                CommonDialogUtils.showErrorDialog(mContext, errString.toString(), null);
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                aSwitch.setChecked(false);
                CommonDialogUtils.showErrorDialog(mContext, helpString.toString(), null);
            }

            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                fingerSuccess();
            }

            @Override
            public void onAuthenticationFailed() {
                aSwitch.setChecked(false);
                CommonDialogUtils.showErrorDialog(mContext, "指纹认证失败", null);

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

    public void jumpToSetting() {
        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        startActivity(intent);
    }

    @OnClick({R.id.iv_back, R.id.switchBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
        }
    }

}
