package com.a99live.zhibo.live.view.weight;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.TextView;

import com.a99live.zhibo.live.R;

/**
 * Created by JJGCW on 2016/11/1.
 */

public class MyProgressDialog extends Dialog {

    private CircleProgressView progressView;

    public MyProgressDialog(Context context, String strMessage) {
        this(context, R.style.CustomProgressDialog, strMessage);
    }

    public MyProgressDialog(Context context, int theme, String strMessage) {
        super(context, theme);
        this.setContentView(R.layout.myprogress_dialog_view);
        this.getWindow().getAttributes().gravity = Gravity.CENTER;
        progressView = (CircleProgressView) findViewById(R.id.loadingImageView);
        progressView.spin();
        TextView tvMsg = (TextView) this.findViewById(R.id.id_tv_loadingmsg);
        if (!TextUtils.isEmpty(strMessage)) {
            tvMsg.setText(strMessage);
        }else{
            tvMsg.setText("加载中...");
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        if (!hasFocus) {
            dismiss();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (progressView!=null){
            if (progressView.isSpinning()){
                progressView.stopSpinning();
            }
        }
    }
}
