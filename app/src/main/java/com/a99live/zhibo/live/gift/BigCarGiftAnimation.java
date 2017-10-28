package com.a99live.zhibo.live.gift;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.utils.UIUtils;

/**
 * Created by JJGCW on 2016/12/22.
 */

public class BigCarGiftAnimation extends LinearLayout {

    private Context mContext;
    private boolean mIsShow;
    private ImageView carGift;
    private int screenWidth;
    private int yDimen;
    private TextView name;
    private TranslateAnimation translateAnimation;
    private ScaleAnimation scaleAnimation;

    public interface BigCarAnimationCallBack{
        void carGiftStop();
    }

    public BigCarGiftAnimation(Context context) {
        this(context,null);
    }

    public BigCarGiftAnimation(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BigCarGiftAnimation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        View view = View.inflate(context, R.layout.big_car_animation,this);
        setIsShow(false);
        initView();
    }

    private void initView(){
        carGift = (ImageView) findViewById(R.id.car_gift);
        name = (TextView) findViewById(R.id.name);
        screenWidth = UIUtils.getScreenWidth();
        yDimen = UIUtils.getDimen(R.dimen.dp_200);
    }

    public void showCar(final BigCarAnimationCallBack bigCarAnimationCallBack,String na){
//        Glide.with(mContext)
//                .load(R.drawable.big)
//                .dontAnimate()
//                .into(carGift);
        name.setText(na);
        carGift.setImageResource(R.drawable.bigcaranimationdrawable);
        final AnimationDrawable background = (AnimationDrawable) carGift.getDrawable();
        background.start();

        if (screenWidth == 0){
            screenWidth = UIUtils.getScreenWidth();
        }if (yDimen == 0){
            yDimen = UIUtils.getDimen(R.dimen.dp_200);
        }

        //位移动画
        if (translateAnimation==null) {
            translateAnimation = new TranslateAnimation(screenWidth, -screenWidth, 0, yDimen);
            translateAnimation.setDuration(5000);
            translateAnimation.setInterpolator(new MyTimeInterpolator());
            translateAnimation.setFillAfter(true);
            translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    setIsShow(true);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setIsShow(false);
                    clearAnimation();
                    animation.cancel();
                    animation.reset();
                    if (bigCarAnimationCallBack != null){
                        bigCarAnimationCallBack.carGiftStop();
                    }
                    background.stop();
                    carGift.setImageResource(R.drawable.car1);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        //缩放动画
        if (scaleAnimation == null) {
            scaleAnimation = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        final ScaleAnimation scaleAnimation2 = new ScaleAnimation(1.0f, 0.5f, 1.0f, 0.5f,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(2000);
            scaleAnimation.setFillAfter(true);
//        scaleAnimation2.setDuration(2500);
//        scaleAnimation.setRepeatMode(Animation.REVERSE);
//        scaleAnimation.setRepeatCount(1);
            scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
//                carGift.startAnimation(scaleAnimation2);
                    animation.cancel();
                    carGift.clearAnimation();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        this.startAnimation(translateAnimation);
        carGift.setAnimation(scaleAnimation);
        scaleAnimation.startNow();

    }

    private void setIsShow(boolean isShow){
        mIsShow = isShow;
        if (isShow){
            this.setVisibility(VISIBLE);
        }else{
            this.setVisibility(GONE);
        }
    }

    private boolean getIsShow(){
        return mIsShow;
    }
}
