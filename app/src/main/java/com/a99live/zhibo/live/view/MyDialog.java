package com.a99live.zhibo.live.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.utils.UIUtils;

/**
 * Created by ljb on 2016/5/12.
 */
public class MyDialog implements View.OnClickListener {

    private Activity mAct;
    private Dialog mDialog;

    private LinearLayout mView;
    private TextView tv_msg;
    private Button btn_left, btn_right;
    private DialogInterface.OnClickListener mRightButtonClickListener, mLeftButtonClickListener;

    public MyDialog(Activity activity) {
        this.mAct = activity;
        init();
    }


    private void init() {
        mDialog = new Dialog(mAct, R.style.mask_dialog);
        mDialog.setCanceledOnTouchOutside(true);
        mView = (LinearLayout) View.inflate(mAct, R.layout.dialog_cdt, null);
        tv_msg = (TextView) mView.findViewById(R.id.tv_msg);
        btn_left = (Button) mView.findViewById(R.id.btn_left);
        btn_right = (Button) mView.findViewById(R.id.btn_right);

        btn_left.setOnClickListener(this);
        btn_right.setOnClickListener(this);

        mDialog.setContentView(mView, new ViewGroup.LayoutParams(UIUtils.getDimen(R.dimen.dp_276), ViewGroup.LayoutParams.MATCH_PARENT));
        mDialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);

        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setWindowAnimations(R.style.dialog_anim);
    }


    public MyDialog setLeftButtonState(int strId, DialogInterface.OnClickListener listener) {
        btn_left.setVisibility(View.VISIBLE);
        return setLeftButtonState(mAct.getString(strId), listener);
    }

    public MyDialog setRightButtonState(int strId, DialogInterface.OnClickListener listener) {
        btn_right.setVisibility(View.VISIBLE);
        return setRightButtonState(mAct.getString(strId), listener);
    }

    public MyDialog setLeftButtonState(String str, DialogInterface.OnClickListener listener) {
        if (!TextUtils.isEmpty(str)) {
            btn_left.setText(str);
        }
        if (listener != null) {
            this.mLeftButtonClickListener = listener;
        }
        btn_left.setVisibility(View.VISIBLE);
        return this;
    }

    public MyDialog setRightButtonState(String str, DialogInterface.OnClickListener listener) {
        btn_right.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(str)) {
            btn_right.setText(str);
        }
        if (listener != null) {
            this.mRightButtonClickListener = listener;
        }
        return this;
    }


    public MyDialog setMessage(String strId) {
        tv_msg.setText(strId);
        return this;
    }

    public MyDialog setMessage(int strId) {
        tv_msg.setText(strId);
        return this;
    }


    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        if (mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public MyDialog setCanceledOnTouchOutside(boolean flag) {
        mDialog.setCanceledOnTouchOutside(flag);
        return this;
    }

    @Override
    public void onClick(View v) {
        dismiss();

        if (v.getId() == btn_left.getId() && mLeftButtonClickListener != null) {
            mLeftButtonClickListener.onClick(mDialog, DialogInterface.BUTTON_NEGATIVE);
        } else if (v.getId() == btn_right.getId() && mRightButtonClickListener != null) {
            mRightButtonClickListener.onClick(mDialog, DialogInterface.BUTTON_POSITIVE);
        }

    }

    public MyDialog setCancelable(boolean flag) {
        mDialog.setCancelable(flag);
        return this;
    }

    public MyDialog setTextGravity(int type) {
        tv_msg.setGravity(type);
        return this;
    }
}
