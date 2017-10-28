package com.a99live.zhibo.live.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.utils.UIUtils;

/**
 * Created by fuyk on 2016/9/2.
 */
public class DownLoadDialog implements View.OnClickListener {

    private Activity mAct;
    private Dialog mDialog;
    private View mView;
    private TextView tv_ok;
    private TextView tv_msg;
    private ProgressBar pb_load;

    public DownLoadDialog(Activity activity) {
        this.mAct = activity;
        init();
    }

    private void init() {
        mDialog = new Dialog(mAct, R.style.mask_dialog);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
        mView = View.inflate(mAct, R.layout.view_notify_upgrade, null);
        tv_ok = (TextView) mView.findViewById(R.id.tv_ok);
        tv_msg = (TextView) mView.findViewById(R.id.textView1);
        pb_load = (ProgressBar) mView.findViewById(R.id.progressBar1);

        tv_ok.setOnClickListener(this);

        mDialog.setContentView(mView, new ViewGroup.LayoutParams(UIUtils.dip2px(270), ViewGroup.LayoutParams.MATCH_PARENT));
        mDialog.setFeatureDrawableAlpha(Window.FEATURE_OPTIONS_PANEL, 0);

        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setWindowAnimations(R.style.cdt_dialog_anim);
    }

    public DownLoadDialog setMessage(String strId) {
        tv_msg.setText(strId);
        return this;
    }

    public DownLoadDialog setMessage(int strId) {
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

    @Override
    public void onClick(View view) {
        dismiss();
    }

    public void setProgress(long loaded, long total) {
        pb_load.setMax((int) total);
        pb_load.setProgress((int) loaded);
    }
}
