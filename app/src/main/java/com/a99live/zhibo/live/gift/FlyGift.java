package com.a99live.zhibo.live.gift;

import android.content.Context;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.UIUtils;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JJGCW on 2016/12/21.
 */

public class FlyGift extends RelativeLayout {

    private boolean mIsShow = false;
    private List<String> flyGifts;
    private TextView msg;
    private Context mContext;
    private int screenWidth;
    private ImageView flygiftImg;

    public FlyGift(Context context) {
        this(context,null);
    }

    public FlyGift(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FlyGift(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View view = View.inflate(context, R.layout.flygift,this);
        show(false);
        init();
    }

    private void init(){
        msg = (TextView) findViewById(R.id.msg);
        flygiftImg = (ImageView) findViewById(R.id.flygiftimg);
        flyGifts = new ArrayList<>();
        screenWidth = UIUtils.getScreenWidth();
    }

    /**
     * {"from":1,"to":"\u660e\u7406\u7684\u9999\u83c7\ud83d\ude04",
     * "gift_img_url":"http:\/\/99live-10063116.image.myqcloud.com\/3?imageMogr2\/thumbnail\/x200"}
     * @param flyGift
     */
    public void setFlyGift(String flyGift){
        if (isShow()){
            flyGifts.add(flyGift);
        }else{
            ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(flyGift);
            if (listMapByJson.size()>0){
                Map<String, String> map = listMapByJson.get(0);
                String gift_img_url = map.get("gift_img_url");
                Glide.with(mContext).load(gift_img_url).centerCrop()
                        .dontAnimate().into(flygiftImg);
                String from = map.get("from");
                String to = map.get("to");
//                String hint = "<font color='#999999'>注：请仔细确认支付宝账户和姓名</font><font color='#FF5A5B'>[是否正确]</font>" +
//                        "</font><font color='#999999'>否则绑定后的所有</font></font><font color='#FF5A5B'>[提现操作都将失败]</font>";
                String formTo = "<font color='#ff5a5b'>" + to + "</font>" + "<font color='#444444'>"+ "收到了"+ "</font>"  + "<font color='#0091d1'>"
                        + from + "</font>"  + "<font color='#444444'>"+ "送的"+ "</font>" ;
                msg.setText(Html.fromHtml(formTo));
//                msg.setText("大头大头下雨不愁，人家有伞我有大头，哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈啊啊哈啊啊哈啊啊哈 ");
                msg.setFocusable(true);
                msg.setSelected(true);
                fly();
            }
        }
    }

    TranslateAnimation animation;
    private void getAnima(){
        if (screenWidth == 0){
            screenWidth = UIUtils.getScreenWidth();
        }
        animation = new TranslateAnimation(screenWidth,-screenWidth,0,0);
        animation.setDuration(7000);
        animation.setFillAfter(true);
//        animation.setRepeatMode(Animation.RESTART);
//        animation.setRepeatCount(3);
        animation.setInterpolator(new MyTimeInterpolator());
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                show(true);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                show(false);
                animation.cancel();
                animation.reset();
                flyGiftLoop();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void fly(){
        if (animation==null){
            getAnima();
            this.startAnimation(animation);
        }else{
            this.startAnimation(animation);
        }
    }

    public void show(boolean isShow){
        mIsShow = isShow;
        if (isShow){
            this.setVisibility(VISIBLE);
        }else{
            this.setVisibility(GONE);
        }
    }

    public boolean isShow(){
        return mIsShow;
    }

    private void flyGiftLoop(){
        synchronized (flyGifts){
            if (flyGifts.size()>0){
                String s = flyGifts.get(0);
                setFlyGift(s);
                flyGifts.remove(0);
            }
        }
    }
}
