package com.a99live.zhibo.live.view.weight;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.OrderListActivity;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.ReserveProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.UIUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * 作者：songYF on 2016/8/10 17:33
 */
public class NoticeView extends FrameLayout {
    //ArrayList<String> linkUrlArray = new ArrayList<String>();
    //<String> titleList = new ArrayList<String>();
    ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
    private LinearLayout notice_parent_ll;
    private LinearLayout notice_ll;
    private ViewFlipper notice_vf;
    private int mCurrPos;
    private Context context;
    private long interval = 5000;
    public Timer timer;
    private TextView yuyue;
    private ImageView arrow;


    public NoticeView(Context context) {
        super(context);
        init();
    }

    public NoticeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoticeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    TimerTask task;

    private void initRollNotice() {
        if (data != null && data.size() <= 1) {
            moveNext(false);
            if (timer != null){
                timer.cancel();
                timer = null;
            }
            if (task != null) {
                task.cancel();
                task = null;
            }
            return;
        }
        if (timer == null) {
            timer = new Timer();
        }
        if (task == null){
            task = new TimerTask() {
                @Override
                public void run() {
                    ((Activity) getContext()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            moveNext(true);
                            Log.d("Task", "下一个");
                        }
                    });

                }
            };
            //timer是不能暂停的，只能cancel之后置空 用的时候再从新设置
            timer.schedule(task, 0, interval);
        }
    }

    private void init() {
        notice_parent_ll = (LinearLayout) View.inflate(getContext(),
                R.layout.layout_notice, null);
        notice_ll = ((LinearLayout) this.notice_parent_ll
                .findViewById(R.id.homepage_notice_ll));
        notice_vf = ((ViewFlipper) this.notice_parent_ll
                .findViewById(R.id.homepage_notice_vf));
        yuyue = (TextView) this.notice_parent_ll.findViewById(R.id.yuyue);
        arrow = (ImageView) this.notice_parent_ll.findViewById(R.id.arrow);
        addView(notice_parent_ll);
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void setData(ArrayList<Map<String, String>> data) {
        this.data.clear();
        this.data.addAll(data);
        try {
            initRollNotice();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void moveNext(boolean isScroll) {
        setView(this.mCurrPos, this.mCurrPos + 1);
        if (isScroll) {
            this.notice_vf.setInAnimation(getContext(), R.anim.in_bottomtop);
            this.notice_vf.setOutAnimation(getContext(), R.anim.out_bottomtop);
            this.notice_vf.showNext();
        }
    }

    private void setView(int curr, int next) {

        View noticeView = inflate(getContext(), R.layout.notice_item,
                null);
        TextView notice_tv = (TextView) noticeView.findViewById(R.id.notice_tv);
        if ((curr < next) && (next > (data.size() - 1))) {
            next = 0;
        } else if ((curr > next) && (next < 0)) {
            next = data.size() - 1;
        }
        // TODO: 2016/8/10 改
        Map<String ,String> nMap = data.get(next);
        String title = nMap.get("title");
//        String status = nMap.get("status");
        String live_time = nMap.get("live_time");
//        String count = nMap.get("count");
//        String intro = nMap.get("intro");
//        String avatar = nMap.get("avatar");
        final String reserve_id = nMap.get("id");
        notice_tv.setText(live_time + " " + title);
        notice_tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
//                Bundle bundle = new Bundle();
//                String notice_id = data.get(mCurrPos).get("notice_id");
//                if ("null".equals(notice_id)) {
//                    return;
//                }
//                bundle.putString("notice_id", data.get(mCurrPos).get("notice_id"));
//                bundle.putString("title", data.get(mCurrPos).get("title"));
//                Intent intent = new Intent(getContext(),
//                        FunEnglishShowWeb.class);
//                intent.putExtras(bundle);
//                getContext().startActivity(intent);
//                OrderDetailActivity.goOfderDetrailActivity(getContext(),reserve_id);
                OrderListActivity.goOrderListActivity(getContext());
            }
        });
        yuyue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                setYuYue(reserve_id);
                OrderListActivity.goOrderListActivity(getContext());
            }
        });
        arrow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderListActivity.goOrderListActivity(getContext());
            }
        });
        if (notice_vf.getChildCount() > 1) {
            notice_vf.removeViewAt(0);
        }
        notice_vf.addView(noticeView, notice_vf.getChildCount());
        mCurrPos = next;

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
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else{
                            UIUtils.showToast(s);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast(R.string.net_error);
                    }
                });
    }
}
