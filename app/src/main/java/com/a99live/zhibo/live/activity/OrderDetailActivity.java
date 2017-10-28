package com.a99live.zhibo.live.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.ReserveProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TimeUtils;
import com.a99live.zhibo.live.utils.UIUtils;
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
 * Created by fuyk on 2017/2/17.
 */

public class OrderDetailActivity extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView tv_title;

    @Bind(R.id.tv_finish)
    TextView tv_finish;

    @Bind(R.id.iv_order_detail_head)
    NewCircleImageView iv_order_detail_head;

    @Bind(R.id.tv_order_detail_name)
    TextView tv_order_detail_name;

    @Bind(R.id.tv_order_detail_label)
    TextView tv_order_detail_label;

    @Bind(R.id.tv_order_num)
    TextView tv_order_num;

    @Bind(R.id.tv_ordered)
    TextView tv_ordered;

    @Bind(R.id.tv_order_time_begin)
    TextView tv_order_time_begin;

    @Bind(R.id.tv_order_begin_detail)
    TextView tv_order_begin_detail;

    @Bind(R.id.tv_order_count_down)
    TextView tv_order_count_down;

    @Bind(R.id.tv_order_detail)
    TextView tv_order_detail;

    @Bind(R.id.top_bg)
    ImageView top_bg;

    @Bind(R.id.tv_order_detail_title)
    TextView tv_order_detail_title;

    @Bind(R.id.tv_order_now)
    TextView tv_order_now;
    private String reserve_id;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    public static void goOfderDetrailActivity(Context context,String reserve_id){
        Intent intent = new Intent(context,OrderDetailActivity.class);
        intent.putExtra("reserve_id",reserve_id);
        context.startActivity(intent);
    }

    private void initView() {
        tv_title.setText("");
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.order_more);
//        ImageSpan imageSpan = new ImageSpan(this, bitmap);
//        SpannableString spanStr = new SpannableString("icon");
//        spanStr.setSpan(imageSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tv_finish.setVisibility(View.VISIBLE);
//        tv_finish.setText(spanStr);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            if (bundle.containsKey("reserve_id")){
                reserve_id = bundle.getString("reserve_id");
                getDetail(reserve_id);
            }
        }else{

        }
    }

    private void getDetail(String reserve_id) {
        LiveRequestParams param = new LiveRequestParams();
        param.put("reserve_id",reserve_id);
        ReserveProtocol protocol = new ReserveProtocol();
        protocol.getYuYueDetail(param)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                String data = map.get("data");
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                                if (dataList.size()>0){
                                    Map<String, String> dataMap = dataList.get(0);
                                    setData(dataMap);
                                }else{

                                }

                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else{
                            UIUtils.showToast("预约已结束");
                            finish();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    /**
     * "id":"3",
     "image":"http://99live-10063116.image.myqcloud.com/1aa789749490343f70bc8e1ad0c6f247?imageMogr2/crop/358x/thumbnail/x1334",
     "title":"测试3",
     "status":"0",
     "live_time":"2017-02-21 10:25:24",
     "count":"1",
     "intro":"fdsafdsafdsafdsafd",
     "nickname":"高兴的向日葵_cc8ba5",
     "avatar":"http://99live-10063116.image.myqcloud.com/28630e0744f221613919e1577b869f4a?imageMogr2/thumbnail/x200"
     },
     * @param dataMap
     */
    private void setData(Map<String, String> dataMap) {
        String image = dataMap.get("image");
        String title = dataMap.get("title");
        String status = dataMap.get("status");
        String live_time = dataMap.get("live_time");
        String count = dataMap.get("count");
        String intro = dataMap.get("intro");
        String nickname = dataMap.get("nickname");
        String avatar = dataMap.get("avatar");
        String liveTimeInt = dataMap.get("live_time_int");

        tv_title.setText(title);

        Glide.with(this).load(image).diskCacheStrategy(DiskCacheStrategy.SOURCE).dontAnimate().into(top_bg);
        Glide.with(this).load(avatar).diskCacheStrategy(DiskCacheStrategy.SOURCE).dontAnimate().into(iv_order_detail_head);
        tv_order_detail_title.setText(title);
        tv_order_time_begin.setText(live_time+"开始");
        tv_order_num.setText(count+"人");
        tv_order_detail.setText(intro);
        tv_order_detail_name.setText(nickname);
        tv_order_begin_detail.setText("距离直播还有");

        tv_order_now.setVisibility(View.VISIBLE);
        if ("0".equals(status)){
            tv_order_now.setBackgroundColor(ContextCompat.getColor(this,R.color.text_5a5b));
            tv_order_now.setText("立即预约");
        }else{
            tv_order_now.setBackgroundColor(ContextCompat.getColor(this,R.color.text_999));
            tv_order_now.setText("已预约");
        }

       cuttDown(liveTimeInt);




    }

    private void cuttDown(String live_time) {
        try {
            long liveTime = Long.parseLong(live_time);
//            final long startTime = TimeUtils.stringToLong(live_time, "yyyy-MM-dd HH:mm:ss");
            final long startTime = liveTime*1000;
            Log.d("livelog",startTime+"");
            long nowTime = System.currentTimeMillis();
            Log.d("livelog",nowTime+"");
            long time = startTime - nowTime;
            Log.d("livelog",time+"");
            final String s = TimeUtils.timeToDayHourMilS(time);

            tv_order_count_down.setText(s);

            timer = new CountDownTimer(time,1000) {
                @Override
                public void onTick(long l) {
                    long nowTime = System.currentTimeMillis();

                    long time = startTime - nowTime;
                    String dh = TimeUtils.timeToDayHourMilS(time);
                    tv_order_count_down.setText(dh);
                }

                @Override
                public void onFinish() {
                    tv_order_count_down.setText("直播已开始，请查看");
                    if (timer != null)
                        timer.cancel();
                }
            };
            if (timer != null)
                timer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null){
            timer.onFinish();
            timer.cancel();
            timer = null;
        }
    }

    @OnClick({R.id.layout_back, R.id.tv_order_now})
    void onClick(View view){
        switch (view.getId()){
            case R.id.layout_back:
                finish();
                break;
            case R.id.tv_order_now:
                String tv = (String) tv_order_now.getText();
                if ("立即预约".equals(tv)){
                    tv_order_now.setClickable(false);
                    setYuYue(reserve_id);
                }else{
                    UIUtils.showToast("已预约");
                }

                break;
        }
    }

    private void setYuYue(String reserve_id) {
        LiveRequestParams params = new LiveRequestParams();
        params.put("ucode", SPUtils.getString(SPUtils.USER_CODE));
        params.put("reserve_id",reserve_id);
        ReserveProtocol protocol = new ReserveProtocol();
        protocol.setRecordUser(params)
                .observeOn(AndroidSchedulers.mainThread())
//                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                UIUtils.showToast("预约成功");
                                tv_order_now.setBackgroundColor(ContextCompat.getColor(OrderDetailActivity.this,R.color.text_999));
                                tv_order_now.setText("已预约");
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else{
                            UIUtils.showToast(s);
                        }
                        tv_order_now.setClickable(true);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast(R.string.net_error);
                        tv_order_now.setClickable(true);
                    }
                });
    }


}
