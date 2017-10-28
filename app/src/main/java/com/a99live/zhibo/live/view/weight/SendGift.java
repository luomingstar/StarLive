package com.a99live.zhibo.live.view.weight;

import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.LivePlayerActivity;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JJGCW on 2016/10/11.
 */

public class SendGift extends RelativeLayout implements View.OnClickListener {


    private ViewPager vp;
    private LinearLayout log;
    private TextView diamond_num;
    private TextView pay;
    private TextView send_gift_tv;
    private View view;
    private RelativeLayout giftLayout;
    private HideListener hideListener;
    private Context context;
    private ArrayList<List<Map<String,String>>> listItem;
    private ArrayList<GiftItemView> giftItemViews;
    private ArrayList<View> views;
    private Handler mHandler;
    private RelativeLayout gold;
    private RelativeLayout send_continue;
    private TextView send_continue_time;
    private RelativeLayout send_continue_parent;
    public boolean isDataSuccess = false;

    /*当送礼页面取消的时候*/
    public interface HideListener{
        void hide();
        void onlyHideView();
    }

    public void setHideListener(HideListener hideListener){
        this.hideListener = hideListener;
    }

    public SendGift(Context context) {
        this(context,null);
    }

    public SendGift(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SendGift(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        view = View.inflate(context, R.layout.gift_send_layout,this);
        hide();
        findView();
    }
    public void findView(){
        vp = (ViewPager) findViewById(R.id.vp_gift);
        log = (LinearLayout) findViewById(R.id.log);
        diamond_num = (TextView) findViewById(R.id.diamond_num);
        pay = (TextView) findViewById(R.id.pay);
        send_gift_tv = (TextView) findViewById(R.id.send_gift_tv);
        giftLayout = (RelativeLayout) findViewById(R.id.gift_layout);
        gold = (RelativeLayout) findViewById(R.id.gold);
        send_continue_parent = (RelativeLayout) findViewById(R.id.send_continue_parent);
        send_continue = (RelativeLayout) findViewById(R.id.send_continue);
        send_continue_time = (TextView) findViewById(R.id.send_continue_time);
        send_continue_parent.setVisibility(GONE);
        giftLayout.setOnClickListener(this);
        send_gift_tv.setOnClickListener(this);
        gold.setOnClickListener(this);
        send_continue.setOnClickListener(this);
//        send_continue_parent.setOnClickListener(this);//不要点击事件
        pay.setOnClickListener(this);
    }

    public void setRefresh(int page, int position){
        for (int i=0; i < giftItemViews.size();i++){
                giftItemViews.get(i).refresh( page,  position);
        }
}

    /**
     * 设置发送按钮是否可点击
     * @param isEnable
     */
    public void setSendEnable(boolean isEnable){
        send_gift_tv.setEnabled(isEnable);
    }

    public void setSendButtonClickable(boolean isClickable){
        send_gift_tv.setEnabled(isClickable);
    }

    public void initData(Map<String, String> map, Handler handler){
        this.mHandler = handler;
        myClock = new MyClock(3100,100);
        int pageItem = 8;
        String data = map.get("data");
        String append = map.get("append");
        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(append);
        if (listMapByJson.size()>0){
            Map<String, String> stringStringMap = listMapByJson.get(0);
            String z = stringStringMap.get("Z");
            diamond_num.setText(z);
        }
        ArrayList<Map<String, String>> listMapByJson1 = JsonUtil.getListMapByJson(data);
        int size = listMapByJson1.size()/pageItem;
        if (size>0){
            int last = listMapByJson1.size() % pageItem;
            if (last>0){
                size += 1;
            }else{

            }
        }else{
            size = 1;
        }
//        size = 2;
        final ImageView[] navImg = new ImageView[size];
        log.removeAllViews();
        for (int i = 0;i<navImg.length;i++){
            navImg[i] = new ImageView(context);
            int dp_2_5 = UIUtils.getDimen(R.dimen.dp_2_5);
            navImg[i].setPadding(dp_2_5, 0,dp_2_5, 0);
            navImg[i].setBackgroundColor(Color.TRANSPARENT);
            navImg[i].setImageResource(R.drawable.round_bg_white);
            if (navImg.length > 1) {
                log.addView(navImg[i]);
                navImg[i].getLayoutParams().width = UIUtils.getDimen( R.dimen.dp_12_5);
                navImg[i].getLayoutParams().height = UIUtils.getDimen( R.dimen.dp_7_5);
            }
        }
        if(navImg.length > 0)
            navImg[0].setImageResource(R.drawable.round_bg_red);
        final int finalSize = size;
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < finalSize; i++) {
                    if (i == position) {
                        navImg[i].setImageResource(R.drawable.round_bg_red);
                    } else
                        navImg[i].setImageResource(R.drawable.round_bg_white);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        listItem = new ArrayList<>();
        giftItemViews = new ArrayList<>();
        views = new ArrayList<>();


        for (int i=1;i<=size;i++){
            List<Map<String,String>> maps = new ArrayList<>();
            int m = 0;
            for(int j = (size-i)*pageItem;j<listMapByJson1.size(); j++){
                m =m+1;
                maps.add(listMapByJson1.get(j));
                if (m == pageItem){
                    break;
                }
            }
            listItem.add(0,maps);
        }
        for (int i=0;i<listItem.size();i++){
            List<Map<String, String>> maps = listItem.get(i);
            GiftItemView giftItemView = new GiftItemView(context,maps,mHandler,i);
            giftItemViews.add(giftItemView);
            View view = giftItemView.initView();
            views.add(view);
        }
        MyAdapter adapter = new MyAdapter(views);
        vp.setAdapter(adapter);
        isDataSuccess = true;
    }

    public void setDiamondNum(int num){
        diamond_num.setText(num+"");
    }
    /**
     *
     * @param isSelect 是否选择礼物
     * @param giftMap 礼物的model 数据
     */
    public void selectGift(boolean isSelect, Map<String, String> giftMap){
        send_gift_tv.setVisibility(VISIBLE);
//        send_continue.setVisibility(GONE);
        send_continue_parent.setVisibility(GONE);
        if (myClock != null)
            myClock.cancel();
//        mHandler.obtainMessage(6).sendToTarget();
    }
    /*隐藏布局*/
    public void hide(){
        view.setVisibility(GONE);
//        if (mHandler != null)
//            mHandler.obtainMessage(6).sendToTarget();
    }

    /*显示布局*/
    public void show(){
        view.setVisibility(VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.gift_layout://点击背景
                hide();
                if (myClock != null)
                    myClock.cancel();
                send_gift_tv.setVisibility(VISIBLE);
//                send_continue.setVisibility(GONE);
                send_continue_parent.setVisibility(GONE);
                if (hideListener != null){
                    if (isDataSuccess) {
                        hideListener.hide();
                    }else{
                        hideListener.onlyHideView();
                    }
                }
            break;
            case R.id.gold://点击充值背景

                break;
            case R.id.send_gift_tv://点击发送按钮
                if (mHandler != null)
                    mHandler.obtainMessage(LivePlayerActivity.sendGift).sendToTarget();//发送礼物
                break;

            case R.id.send_continue://点击连送按钮
                if (myClock != null){
                    myClock.cancel();
                    myClock.start();
                }else{
                    myClock = new MyClock(3100,100);
                    myClock.start();
                }
                if (mHandler != null)
                    mHandler.obtainMessage(LivePlayerActivity.sendGiftContinue).sendToTarget();//发送礼物
                break;

            case R.id.send_continue_parent:

                break;

            case R.id.pay:
                if (mHandler != null)
                    mHandler.obtainMessage(7).sendToTarget();
                break;
        }
    }
    private MyClock myClock;
    public void sendContinue(){
        send_gift_tv.setVisibility(INVISIBLE);
//        send_continue.setVisibility(VISIBLE);
        send_continue_parent.setVisibility(VISIBLE);
        if (myClock == null){
            myClock = new MyClock(3100,100);
        }
        myClock.start();
    }

    private class MyClock extends CountDownTimer{

        public MyClock(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            long time = l / 100;
            send_continue_time.setText(""+ time);
        }

        @Override
        public void onFinish() {
            myClock.cancel();
            send_gift_tv.setVisibility(VISIBLE);
//            send_continue.setVisibility(GONE);
            send_continue_parent.setVisibility(GONE);
//            mHandler.obtainMessage(6).sendToTarget();
        }
    }


    public class MyAdapter extends PagerAdapter{

        private ArrayList<View> giftItemViews;
        public MyAdapter(ArrayList<View> giftItemViews){
            this.giftItemViews = giftItemViews;
        }

        @Override
        public int getCount() {
            return giftItemViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
             container.removeView(giftItemViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            return super.instantiateItem(container, position);
            View view = giftItemViews.get(position);
            container.addView(view);
            return view;
        }
    }
}
