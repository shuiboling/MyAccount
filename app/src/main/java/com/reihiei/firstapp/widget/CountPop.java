package com.reihiei.firstapp.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.reihiei.firstapp.R;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CountPop implements View.OnClickListener {

    private Context context;

    private TextView tvInput;
    private TextView clear;
    private RelativeLayout del;
    private TextView jia;
    private TextView jian;
    private TextView cheng;
    private TextView chu;
    private TextView one;
    private TextView two;
    private TextView three;
    private TextView four;
    private TextView five;
    private TextView six;
    private TextView seven;
    private TextView eight;
    private TextView nine;
    private TextView zero;
    private TextView point;
    private TextView dengYu;
    private TextView confirm;
    private TextView cancel;

    private boolean isCancel;

    private String inNumber = "";
    private String result = "";
    private String[] nOpN = new String[3];

    private PopupWindow popupWindow;
    private OnDismissListener listener;

    private View selectItem = null;

    public CountPop(Context context, OnDismissListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void showPop(View parent, int gravity, int x, int y) {
        View view = LayoutInflater.from(context).inflate(R.layout.pop_count_money, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setContentView(view);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);

        tvInput = view.findViewById(R.id.tvInput);
        tvInput.setOnClickListener(this);
        clear = view.findViewById(R.id.clear);
        clear.setOnClickListener(this);
        del = view.findViewById(R.id.del);
        del.setOnClickListener(this);
        jia = view.findViewById(R.id.jia);
        jia.setOnClickListener(this);
        jian = view.findViewById(R.id.jian);
        jian.setOnClickListener(this);
        cheng = view.findViewById(R.id.cheng);
        cheng.setOnClickListener(this);
        chu = view.findViewById(R.id.chu);
        chu.setOnClickListener(this);
        one = view.findViewById(R.id.one);
        one.setOnClickListener(this);
        two = view.findViewById(R.id.two);
        two.setOnClickListener(this);
        three = view.findViewById(R.id.three);
        three.setOnClickListener(this);
        four = view.findViewById(R.id.four);
        four.setOnClickListener(this);
        five = view.findViewById(R.id.five);
        five.setOnClickListener(this);
        six = view.findViewById(R.id.six);
        six.setOnClickListener(this);
        seven = view.findViewById(R.id.seven);
        seven.setOnClickListener(this);
        eight = view.findViewById(R.id.eight);
        eight.setOnClickListener(this);
        nine = view.findViewById(R.id.nine);
        nine.setOnClickListener(this);
        zero = view.findViewById(R.id.zero);
        zero.setOnClickListener(this);
        point = view.findViewById(R.id.point);
        point.setOnClickListener(this);
        dengYu = view.findViewById(R.id.equal);
        dengYu.setOnClickListener(this);
        confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
        cancel = view.findViewById(R.id.cancel);
        cancel.setOnClickListener(this);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (listener != null && !isCancel) {
                    String num = tvInput.getText().toString();
                    if (num.charAt(num.length() - 1) == '.') {
                        num = num.substring(0, num.length() - 1);
                    }
                    listener.dismiss(num);
                }
            }
        });
        popupWindow.showAtLocation(parent, gravity, x, y);

    }

    @Override
    public void onClick(View v) {

        if (selectItem != null) {
            selectItem.setSelected(false);
        }
        selectItem = v;

        switch (v.getId()) {
            case R.id.zero:
                inputNum("0");
                break;
            case R.id.one:
                inputNum("1");
                break;
            case R.id.two:
                inputNum("2");
                break;
            case R.id.three:
                inputNum("3");
                break;
            case R.id.four:
                inputNum("4");
                break;
            case R.id.five:
                inputNum("5");
                break;
            case R.id.six:
                inputNum("6");
                break;
            case R.id.seven:
                inputNum("7");
                break;
            case R.id.eight:
                inputNum("8");
                break;
            case R.id.nine:
                inputNum("9");
                break;
            case R.id.point:
                inputPoint();
                break;
            case R.id.jia:
                v.setSelected(true);
                inputOp("+");
                break;
            case R.id.jian:
                v.setSelected(true);
                inputOp("-");
                break;
            case R.id.cheng:
                v.setSelected(true);
                inputOp("*");
                break;
            case R.id.chu:
                v.setSelected(true);
                inputOp("/");
                break;
            case R.id.equal:
                inputEqual();
                break;
            case R.id.confirm:
                inputEqual();
                isCancel = false;
                popupWindow.dismiss();
                break;
            case R.id.cancel:
                isCancel = true;
                popupWindow.dismiss();
                break;
            case R.id.clear:
                inNumber = "";
                tvInput.setText("0");
                nOpN = new String[3];
                break;
            case R.id.del:
                if (!TextUtils.isEmpty(inNumber)) {
                    inNumber = inNumber.substring(0, inNumber.length() - 1);
                    tvInput.setText(inNumber);

                    if (TextUtils.isEmpty(inNumber)) {
                        inNumber = "0";
                        tvInput.setText("0");
                    }
                }
                break;
        }
    }

    private void inputNum(String num) {
        if (!TextUtils.isEmpty(inNumber) && "0".equals(inNumber)) {
            if (!"0".equals(num)) {
                inNumber = num;
            }
        } else {
            inNumber = inNumber + num;
        }
        tvInput.setText(inNumber);

    }

    private void inputPoint() {
        if (TextUtils.isEmpty(inNumber)) {
            inNumber = "0.";
        } else if (!inNumber.contains(".")) {
            inNumber += ".";
        }
        tvInput.setText(inNumber);
    }

    private void inputOp(String op) {

        if (TextUtils.isEmpty(nOpN[0])) {
            if (TextUtils.isEmpty(inNumber)) {
                nOpN[0] = "0";
                tvInput.setText("0");
            } else {
                nOpN[0] = inNumber;
            }

        } else if (!TextUtils.isEmpty(nOpN[0]) && TextUtils.isEmpty(nOpN[1]) && !TextUtils.isEmpty(inNumber)) {
            nOpN[0] = inNumber;

        } else if (TextUtils.isEmpty(nOpN[2]) && !TextUtils.isEmpty(inNumber)) {

            nOpN[2] = inNumber;
            nOpN[0] = countNum();
            tvInput.setText(nOpN[0]);

            nOpN[2] = "";
        }

        nOpN[1] = op;
        inNumber = "";

    }

    private String countNum() {

        BigDecimal num1 = new BigDecimal(nOpN[0]);
        BigDecimal num2 = new BigDecimal(nOpN[2]);

        String op = nOpN[1];
        switch (op) {
            case "+":
                return formNum(num1.add(num2));
            case "-":
                return formNum(num1.subtract(num2));
            case "*":
                return formNum(num1.multiply(num2));
            case "/":
                return formNum(num1.divide(num2,3,RoundingMode.HALF_UP));
            default:
                return "";
        }
    }

    private String formNum(BigDecimal bigDecimal) {
        String num = bigDecimal.toString();
        if (!TextUtils.isEmpty(num) && num.contains(".")) {
            return bigDecimal.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toString();
        }
        return num;
    }

    private void inputEqual() {
        if (!TextUtils.isEmpty(nOpN[0]) && !TextUtils.isEmpty(nOpN[1]) && !TextUtils.isEmpty(inNumber)) {
            nOpN[2] = inNumber;
            nOpN[0] = countNum();
            tvInput.setText(nOpN[0]);

            nOpN[1] = "";
            nOpN[2] = "";
            inNumber = "";
        }
    }

    public interface OnDismissListener {
        public void dismiss(String num);
    }
}
