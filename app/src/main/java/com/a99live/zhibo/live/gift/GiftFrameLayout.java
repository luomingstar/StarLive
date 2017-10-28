package com.a99live.zhibo.live.gift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.glide.GlideCircleTransform;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


/**
 * ${Author}:
 * ${CreateTime}:16/5/26
 * ${Description}:送礼的动画
 * ${EMail}:xukun1007@163.com
 */
public class GiftFrameLayout extends FrameLayout {

    private LayoutInflater mInflater;

    RelativeLayout anim_rl;
    ImageView anim_gift, anim_light, anim_header;
    TextView anim_nickname, anim_sign;
    StrokeTextView anim_num;
    private Context mContext;
    private Handler mHandler;

    /**
     * 礼物数量的起始值
     */
    int starNum = 1;
    int repeatCount = 0;
    private boolean isShowing = false;
    private ObjectAnimator scaleGiftNum;
    public GiftSendModel giftSendModel;
    private GiftCountDown giftCountDown;
    private AnimatorSet animatorStopSet;
    private AnimatorSet animatorStartSet;

    public GiftFrameLayout(Context context) {
        this(context, null);
    }

    public GiftFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        initView();
    }


    private void initView() {
        View view = mInflater.inflate(R.layout.animation, this, false);
        anim_rl = (RelativeLayout) view.findViewById(R.id.animation_person_rl);
        anim_gift = (ImageView) view.findViewById(R.id.animation_gift);
        anim_gift.setVisibility(GONE);
        anim_light = (ImageView) view.findViewById(R.id.animation_light);
        anim_num = (StrokeTextView) view.findViewById(R.id.animation_num);
        anim_header = (ImageView) view.findViewById(R.id.gift_userheader_iv);
        anim_nickname = (TextView) view.findViewById(R.id.gift_usernickname_tv);
        anim_sign = (TextView) view.findViewById(R.id.gift_usersign_tv);
        this.addView(view);
        hide();

    }

    public void setHandler(Handler handler){
        this.mHandler = handler;
        giftCountDown = new GiftCountDown(3000,100);
    }

    public void hideView() {
        anim_gift.setVisibility(GONE);
        anim_light.setVisibility(INVISIBLE);
        anim_num.setVisibility(INVISIBLE);
    }

    public void setModel(GiftSendModel model){
        giftSendModel = model;
        if (!TextUtils.isEmpty(model.getGiftImg())){
            if (mContext != null ) {
                Glide.with(mContext)
                        .load(model.getGiftImg())
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .dontAnimate()
//                    .load("http://99live-10063116.image.myqcloud.com/d6eb9b9d3978552a880be01c8d9f5349?imageMogr2/thumbnail/x200")
                        .centerCrop()
//                    .placeholder(R.mipmap.diamond2x)
//                    .crossFade()
//                    .transform(new GlideCircleTransform(mContext))
                        .into(anim_gift);
            }
        }else{
            anim_gift.setImageResource(R.mipmap.diamond2x);
        }
        if (0 != model.getGiftCount()) {
            this.repeatCount = model.getGiftCount();
            this.starNum = model.getGiftCount();
        }
        if (!TextUtils.isEmpty(model.getNickname())) {
            anim_nickname.setText(model.getNickname());
        }
        if (!TextUtils.isEmpty(model.getGiftName())) {
            anim_sign.setText("送了一个"+ model.getGiftName());
        }
        if (!TextUtils.isEmpty(model.getUserAvatarRes())){
            if (mContext != null){

            Glide.with(mContext)
                    .load(model.getUserAvatarRes())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .load("http://99live-10063116.image.myqcloud.com/d6eb9b9d3978552a880be01c8d9f5349?imageMogr2/thumbnail/x200")
                    .centerCrop()
                    .placeholder(R.mipmap.head)
                    .crossFade()
                    .transform(new GlideCircleTransform(mContext))
                    .into(anim_header);
            }
        }else{
            anim_header.setImageResource(R.mipmap.head);
        }

    }

    public boolean isShowing(){
        return isShowing;
    }
    public void hide(){
        GiftFrameLayout.this.setVisibility(View.INVISIBLE);
        isShowing =false;
    }

    public void startNum(){
        anim_num.setText("x " + (++starNum));
        if (scaleGiftNum == null){
            scaleGiftNum = GiftAnimationUtil.scaleGiftNum(anim_num, repeatCount);
        }
        scaleGiftNum.start();
        if (giftCountDown != null){
            giftCountDown.cancel();
            giftCountDown.start();
        }
    }

    private ObjectAnimator flyFromLtoR;
    private ObjectAnimator flyFromLtoR2;
    public AnimatorSet startAnimation( final int repeatCount) {
        hideView();
        //布局飞入
        if (flyFromLtoR==null) {
            flyFromLtoR = GiftAnimationUtil.createFlyFromLtoR(anim_rl, -getWidth(), 0, 600, new OvershootInterpolator());
            flyFromLtoR.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    GiftFrameLayout.this.setVisibility(View.VISIBLE);
                    GiftFrameLayout.this.setAlpha(1f);
                    isShowing = true;
                    anim_num.setText("x " + starNum);
                    Log.i("TAG", "flyFromLtoR A start");
                }
            });
        }
        //礼物飞入
        if (flyFromLtoR2==null) {
            flyFromLtoR2 = GiftAnimationUtil.createFlyFromLtoR(anim_gift, -getWidth(), 0, 400, new DecelerateInterpolator());
            flyFromLtoR2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    anim_gift.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    GiftAnimationUtil.startAnimationDrawable(anim_light);
                    anim_num.setVisibility(View.VISIBLE);
                }
            });
        }
        //数量增加
        if (scaleGiftNum==null)
            scaleGiftNum = GiftAnimationUtil.scaleGiftNum(anim_num, repeatCount);
//        scaleGiftNum.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//                anim_num.setText("x " + (starNum));
//            }
//        });
        //向上渐变消失
        if (fadeAnimator==null) {
            fadeAnimator = GiftAnimationUtil.createFadeAnimator(GiftFrameLayout.this, 0, -100, 300, 400);
            fadeAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    GiftFrameLayout.this.setVisibility(View.INVISIBLE);
                }
            });
        }
        // 复原
        if (fadeAnimator2==null)
            fadeAnimator2 = GiftAnimationUtil.createFadeAnimator(GiftFrameLayout.this, -100, 0, 20, 0);
//        if (animatorStartSet != null){
//            animatorStartSet.cancel();
//        }
        if (animatorStartSet==null) {
            animatorStartSet = GiftAnimationUtil.startAnimation(flyFromLtoR, flyFromLtoR2, scaleGiftNum, fadeAnimator, fadeAnimator2);
            animatorStartSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                if (giftCountDown==null){
                    giftCountDown = new GiftCountDown(3000,100);
                }
                giftCountDown.start();
//                starNum = 1;
//                isShowing = false;
            }

            });
        }else{
            animatorStartSet.start();
        }
        return animatorStartSet;
    }

    private ObjectAnimator fadeAnimator;
    private ObjectAnimator fadeAnimator2;
    public AnimatorSet stopAnimation(){
        //向上渐变消失
        if (fadeAnimator==null) {
            fadeAnimator = GiftAnimationUtil.createFadeAnimator(GiftFrameLayout.this, 0, -100, 300, 400);
            fadeAnimator.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
                    GiftFrameLayout.this.setVisibility(View.INVISIBLE);
                }
            });
        }
        // 复原
        if (fadeAnimator2 == null)
            fadeAnimator2 = GiftAnimationUtil.createFadeAnimator(GiftFrameLayout.this, -100, 0, 20, 0);
        if (animatorStopSet==null) {
            animatorStopSet = GiftAnimationUtil.stopAnimation(fadeAnimator, fadeAnimator2);

            animatorStopSet.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(Animator animation) {
//                giftCountDown = new GiftCountDown(3000,100);
//                giftCountDown.start();
                    starNum = 1;
                    isShowing = false;
                    giftSendModel = null;
                    if (giftCountDown != null)
                        giftCountDown.cancel();
                    if (mHandler != null) {
                        mHandler.obtainMessage(4).sendToTarget();
                    }
                }

            });
        }else{
            animatorStopSet.start();
        }
        return animatorStopSet;
    }

    private class GiftCountDown extends CountDownTimer{

        public GiftCountDown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            stopAnimation();
        }
    }


}
