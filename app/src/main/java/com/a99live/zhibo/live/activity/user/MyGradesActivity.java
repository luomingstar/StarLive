package com.a99live.zhibo.live.activity.user;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.MyLevelSeekBar;
import com.a99live.zhibo.live.view.weight.NewCircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2017/2/10.
 */

public class MyGradesActivity extends BaseActivity {

    private UserProtocol userProtocol;

    @Bind(R.id.layout_title)
    RelativeLayout layout_title;

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.iv_head)
    NewCircleImageView iv_head;

    @Bind(R.id.tv_grades_detail)
    TextView tv_grades_detail;

    @Bind(R.id.tv_my_grades_detail)
    TextView tv_my_grades_detail;

    @Bind(R.id.tv_grades_now)
    TextView tv_grades_now;

    @Bind(R.id.tv_grades_next)
    TextView tv_grades_next;

    @Bind(R.id.seekBar_grades)
    MyLevelSeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_grades);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        layout_title.setBackgroundResource(R.color.text_5a5b);
        tv_title.setText(R.string.my_grades);
        Glide.with(this)
                .load(SPUtils.getString(SPUtils.USER_AVATAR))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .placeholder(R.mipmap.head)
                .dontAnimate()
                .into(iv_head);
    }

    private void initData() {
        userProtocol = new UserProtocol();
        getMyGradesInfo();
    }

    private void getMyGradesInfo(){
        LiveRequestParams params = new LiveRequestParams();

        userProtocol.getMyGrades(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            int code = Integer.parseInt(map.get("code"));
                            if (code == 0){
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(map.get("data"));
                                if (dataList.size() > 0){
                                    Map<String, String> dataMap = dataList.get(0);
                                    initGradesInfo(dataMap);
                                }else {

                                }
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else {
                            UIUtils.showToast(R.string.net_error);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast(R.string.net_error);
                    }
                });
    }

    private void initGradesInfo(Map<String, String> dataMap) {
        tv_grades_detail.setText("您已击败了" + dataMap.get("ko") + "%用户");
        String point = dataMap.get("point");
        String next_level_point = dataMap.get("next_level_point");
        String next_level_point_max = dataMap.get("next_level_point_max");
        int pointInt = Integer.parseInt(point);
        int nextLevelInt = Integer.parseInt(next_level_point);
        int nextLevelPointMax = Integer.parseInt(next_level_point_max);
        int nextInt = nextLevelPointMax - pointInt;
        tv_my_grades_detail.setText("我的经验值" + point + "   离升级还差" + nextInt);

        tv_grades_now.setText("LV" + dataMap.get("level"));
        tv_grades_next.setText("LV" + dataMap.get("next_level"));


        seekBar.setMax(100);
        final int bl = (int) ((1.0*(nextLevelInt - nextInt)/nextLevelInt)*100);
        seekBar.setProgress(bl);
    }

    @OnClick(R.id.layout_back)
    void onClick(){
        back();
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        overridePendingTransition(R.anim.scale_big_in, R.anim.tran_right_out);
        finish();
    }


}
