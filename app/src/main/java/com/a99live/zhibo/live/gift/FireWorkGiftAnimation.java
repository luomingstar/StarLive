package com.a99live.zhibo.live.gift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.a99live.zhibo.live.R;

/**
 * Created by JJGCW on 2016/10/19.
 */

public class FireWorkGiftAnimation extends FrameLayout {

    private ImageView firework_flower1;
    private ImageView firework_flower2;
    private ImageView firework_flower3;
    private ImageView firework_flower4;
    private ImageView fireworks;
    private AnimationDrawable animationDrawable;
    private FireWorkGiftAnimationEnd fireWorkGiftAnimationEnd;
    private AnimatorSet animatorSet;
    private Animator fireWork1Animator;
    private Animator fireWork2Animator;
    private Animator fireWork3Animator;
    private Animator fireWork4Animator;


    public FireWorkGiftAnimation(Context context) {
        this(context,null);
    }

    public interface  FireWorkGiftAnimationEnd{
        void fireWorkEnd();
    }

    public void setFireWorkGiftAnimationEndCallBack(FireWorkGiftAnimationEnd fireWorkGiftAnimationEnd){
        this.fireWorkGiftAnimationEnd = fireWorkGiftAnimationEnd;
    }

    public FireWorkGiftAnimation(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FireWorkGiftAnimation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.gift_fire_work_animation,this,true);
        initView();
    }

    private void initView(){
        firework_flower1 = (ImageView) findViewById(R.id.fireworks_flower1);
        firework_flower2 = (ImageView) findViewById(R.id.fireworks_flower2);
        firework_flower3 = (ImageView) findViewById(R.id.fireworks_flower3);
        firework_flower4 = (ImageView) findViewById(R.id.fireworks_flower4);
        fireworks = (ImageView) findViewById(R.id.fireworks);
        firework_flower1.setVisibility(GONE);
        firework_flower2.setVisibility(GONE);
        firework_flower3.setVisibility(GONE);
        firework_flower4.setVisibility(GONE);
        fireworks.setVisibility(GONE);
    }

    public void start(){
        startFireWorkAnimation();
    }

    public void show(){
        this.setVisibility(VISIBLE);
    }

    public void hide(){
        this.setVisibility(GONE);
    }

    private void startFireWorkAnimation(){
        fireworks.setImageResource(R.drawable.gift_fire_list);
        animationDrawable = (AnimationDrawable) fireworks.getDrawable();
        if (animationDrawable != null){
            fireworks.setVisibility(VISIBLE);
        }
        animationDrawable.start();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fireworks.setVisibility(GONE);
                fireworks.setImageResource(R.drawable.fireworks_1);
                startFireWorkFlowerAnimation();
            }
        },1000);
    }
    private void startFireWorkFlowerAnimation(){


        fireWork1Animator = GiftAnimationUtil.getFireWorkAnimator(firework_flower1, 1000, 0);
        fireWork2Animator = GiftAnimationUtil.getFireWorkAnimator(firework_flower2, 1000, 100);
        fireWork3Animator = GiftAnimationUtil.getFireWorkAnimator(firework_flower3, 1000, 250);
        fireWork4Animator = GiftAnimationUtil.getFireWorkAnimator(firework_flower4, 1000, 250);

        animatorSet = new AnimatorSet();
        animatorSet.play(fireWork1Animator).with(fireWork2Animator).with(fireWork3Animator).with(fireWork4Animator);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
//                firework_flower1.setVisibility(VISIBLE);
//                firework_flower2.setVisibility(VISIBLE);
//                firework_flower3.setVisibility(VISIBLE);
//                firework_flower4.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                firework_flower1.setVisibility(GONE);
                firework_flower2.setVisibility(GONE);
                firework_flower3.setVisibility(GONE);
                firework_flower4.setVisibility(GONE);
                if (fireWorkGiftAnimationEnd != null){
                    fireWorkGiftAnimationEnd.fireWorkEnd();
                }
//                animatorSet.cancel();
            }
        });
        animatorSet.start();

    }

}
