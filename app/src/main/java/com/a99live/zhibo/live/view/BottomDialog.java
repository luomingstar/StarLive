package com.a99live.zhibo.live.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.a99live.zhibo.live.R;

/**
 * Created by JJGCW on 2017/4/12.
 */

public class BottomDialog extends Dialog implements View.OnClickListener {

    private TextView one;
    private TextView two;
    private TextView three;
    private TextView cancle;

    private DialogInterface.OnClickListener oneListener,twoListener,threeListener;

    public BottomDialog(@NonNull Context context) {
        this(context, R.style.MyAnimDialog);
    }

    public BottomDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        //加载布局并给布局的控件设置点击事件
        View contentView = getLayoutInflater().inflate(R.layout.dialog_custom_bottom, null);
        one = (TextView) contentView.findViewById(R.id.one);
        two = (TextView) contentView.findViewById(R.id.two);
        three = (TextView) contentView.findViewById(R.id.three);
        cancle = (TextView) contentView.findViewById(R.id.cancle);

        one.setVisibility(View.GONE);
        two.setVisibility(View.GONE);
        three.setVisibility(View.GONE);
        cancle.setVisibility(View.VISIBLE);
        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
//        contentView.findViewById(R.id.tv_1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(getContext(), "你好", Toast.LENGTH_SHORT).show();
//            }
//        });
        super.setContentView(contentView);
    }

    public void setOneListener(String text,DialogInterface.OnClickListener listener){
        one.setVisibility(View.VISIBLE);
        one.setText(text);
        oneListener = listener;
    }

    public void setTwoListener(String text,DialogInterface.OnClickListener listener){
        two.setVisibility(View.VISIBLE);
        two.setText(text);
        twoListener = listener;
    }



    public void setThreeListener(String text,DialogInterface.OnClickListener listener){
        three.setVisibility(View.VISIBLE);
        three.setText(text);
        threeListener = listener;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 预先设置Dialog的一些属性
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams p = dialogWindow.getAttributes();
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        getWindow().setAttributes(p);
//        p.height = (int) (d.getHeight() * 0.6);
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        p.width = d.getWidth();
        p.gravity = Gravity.BOTTOM;
        dialogWindow.setAttributes(p);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.one:
                if (oneListener != null){
                    oneListener.onClick(BottomDialog.this,DialogInterface.BUTTON_NEGATIVE);
                }
                break;
            case R.id.two:
                if (twoListener != null){
                    twoListener.onClick(BottomDialog.this,DialogInterface.BUTTON_NEGATIVE);
                }
                break;
            case R.id.three:
                if (threeListener != null){
                    threeListener.onClick(BottomDialog.this,DialogInterface.BUTTON_NEGATIVE);
                }
                break;
        }
    }
}
