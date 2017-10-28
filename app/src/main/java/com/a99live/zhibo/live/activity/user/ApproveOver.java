package com.a99live.zhibo.live.activity.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 已认证页面
 * Created by JJGCW on 2016/11/15.
 */

public class ApproveOver extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_approve_over);
        ButterKnife.bind(this);
        mTvTitle.setText("用户认证");
    }

    public static void goApproveOver(Context context){
        Intent intent = new Intent(context,ApproveOver.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.layout_back})
    void onClick(View view){
        switch (view.getId()){
            case R.id.layout_back:
                onBackPressed();
                break;
        }
    }
}
