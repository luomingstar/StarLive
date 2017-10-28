package com.a99live.zhibo.live.gift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.LivePlayerActivity;
import com.a99live.zhibo.live.utils.UIUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

/**
 * Created by JJGCW on 2016/10/15.
 */

public class BigGiftFrameLayout extends FrameLayout implements FireWorkGiftAnimation.FireWorkGiftAnimationEnd, BigCarGiftAnimation.BigCarAnimationCallBack {

    private Context mContext;
    private ImageView bigGiftImg;
    private Handler mHandler;
    private boolean isShow = false;
    private FireWorkGiftAnimation fireWorkGiftAnimation;
    private BigCarGiftAnimation bigCarGiftAnimation;
    private ObjectAnimator bitGiftAnimator;
    private ObjectAnimator bigGiftAnimatorScale;
    private AnimatorSet animSet;

    public BigGiftFrameLayout(Context context) {
        this(context,null);
    }

    public BigGiftFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView(){
        View view = LayoutInflater.from(mContext).inflate( R.layout.gift_animation_big,this,true);
        bigGiftImg = (ImageView) view.findViewById(R.id.big_gift);
        fireWorkGiftAnimation = (FireWorkGiftAnimation) view.findViewById(R.id.fire_work_animation);
        bigCarGiftAnimation = (BigCarGiftAnimation) view.findViewById(R.id.bigcar);
        bigGiftImg.setVisibility(GONE);
        fireWorkGiftAnimation.setVisibility(GONE);
        hide();
        fireWorkGiftAnimation.setFireWorkGiftAnimationEndCallBack(this);
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    public void startAnimator(GiftSendModel gift){
//        String giftId = gift.getGift_id();
        String giftName = gift.getGiftName();
        show();

        if ("烟花".equals(giftName)){
            bigGiftImg.setVisibility(GONE);
            fireWorkGiftAnimation.show();
            fireWorkGiftAnimation.start();

        }else if("跑车".equals(giftName)){
            String name = gift.getNickname();
            bigCarGiftAnimation.showCar(this,name);
        }else{
            bigGiftImg.setVisibility(VISIBLE);
            fireWorkGiftAnimation.hide();
            if (bitGiftAnimator== null) {
                bitGiftAnimator = GiftAnimationUtil.getBitGiftAnimator(bigGiftImg, UIUtils.getScreenWidth(), -UIUtils.getDimen(R.dimen.dp_200), 3000, 0);
            }
            if (bigGiftAnimatorScale==null){
                bigGiftAnimatorScale = GiftAnimationUtil.getBigGiftAnimatorScale(bigGiftImg, 3000, 0);
            }
            animSet = new AnimatorSet();
            animSet.play(bitGiftAnimator).with(bigGiftAnimatorScale);
            animSet.setInterpolator(new MyTimeInterpolator());
            animSet.start();
            animSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    hide();
                    if (mHandler != null) {
                        mHandler.obtainMessage(LivePlayerActivity.BigGiftAnimationStop).sendToTarget();
                    }
                }
            });
        }



    }

    public boolean isShow(){
        return isShow;
    }

    public void hide(){
        BigGiftFrameLayout.this.setVisibility(INVISIBLE);
        isShow = false;
    }

    public void show(){
        BigGiftFrameLayout.this.setVisibility(VISIBLE);
        isShow = true;
    }

    public void setModel(GiftSendModel model) {
        if (!TextUtils.isEmpty(model.getGiftImg())){
            if (mContext != null){

            Glide.with(mContext)
                    .load(model.getGiftImg())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .load("http://99live-10063116.image.myqcloud.com/d6eb9b9d3978552a880be01c8d9f5349?imageMogr2/thumbnail/x200")
                    .fitCenter()
//                    .placeholder(R.mipmap.diamond2x)
                    .dontAnimate()
//                    .transform(new GlideCircleTransform(mContext))
                    .into(bigGiftImg);
            }
        }else{
            bigGiftImg.setImageResource(R.mipmap.diamond2x);
        }
    }

    @Override
    public void fireWorkEnd() {
        hide();
        if (mHandler != null) {
            mHandler.obtainMessage(LivePlayerActivity.BigGiftAnimationStop).sendToTarget();
        }
    }

    @Override
    public void carGiftStop() {
        hide();
        if (mHandler != null) {
            mHandler.obtainMessage(LivePlayerActivity.BigGiftAnimationStop).sendToTarget();
        }
    }
}
