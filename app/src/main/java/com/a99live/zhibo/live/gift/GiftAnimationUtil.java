package com.a99live.zhibo.live.gift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.utils.UIUtils;

/**
 * ${Author}:
 * ${CreateTime}:16/5/24
 * ${Description}:
 * ${EMail}:xukun1007@163.com
 */
public class GiftAnimationUtil {


    /**
     * @param target
     * @param star         动画起始坐标
     * @param end          动画终止坐标
     * @param duration     持续时间
     * @return
     * 创建一个从左到右的飞入动画
     * 礼物飞入动画
     */
    public  static ObjectAnimator createFlyFromLtoR(final View target, float star, float end, int duration, TimeInterpolator interpolator) {
        //1.个人信息先飞出来
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(target, "translationX",
                star, end);
        anim1.setInterpolator(interpolator);
        anim1.setDuration(duration);
        return  anim1;
    }


    /**
     * @param target
     * @return
     * 播放帧动画
     */
    public static AnimationDrawable startAnimationDrawable(ImageView target){
        AnimationDrawable animationDrawable = (AnimationDrawable) target.getDrawable();
        if(animationDrawable!=null) {
            target.setVisibility(View.VISIBLE);
            animationDrawable.start();
        }
        return animationDrawable;
    }


    /**
     * @param target
     * @param drawable
     * 设置帧动画
     */
    public static void  setAnimationDrawable(ImageView target, AnimationDrawable drawable){

        target.setBackground(drawable);
    }


    /**
     * @param target
     * @param num
     * @return
     * 送礼数字变化
     */
    public static ObjectAnimator scaleGiftNum(final TextView target , int num){
        PropertyValuesHolder anim4 = PropertyValuesHolder.ofFloat("scaleX",
                1.7f, 0.8f,1f);
        PropertyValuesHolder anim5 = PropertyValuesHolder.ofFloat("scaleY",
                1.7f, 0.8f,1f);
        PropertyValuesHolder anim6 = PropertyValuesHolder.ofFloat("alpha",
                1.0f, 0f,1f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(target, anim4, anim5, anim6).setDuration(480);
        animator.setRepeatMode(ValueAnimator.RESTART);
//        animator.setRepeatCount(num);
        return animator;

    }



    /**
     * @param target
     * @param star
     * @param end
     * @param duration
     * @param startDelay
     * @return
     * 向上飞 淡出
     */
    public static ObjectAnimator createFadeAnimator(final View target, float star, float end, int duration, int startDelay){

        PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat("translationY", star,end);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1.0f,0f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(target, translationY, alpha);
        animator.setStartDelay(startDelay);
        animator.setDuration(duration);
        return animator;
    }


    /**
     * @param
     * @return
     * 按顺序播放动画
     */
    public static AnimatorSet startAnimation(ObjectAnimator animator1, ObjectAnimator animator2, ObjectAnimator animator3, ObjectAnimator animator4, ObjectAnimator animator5){
        AnimatorSet animSet = new AnimatorSet();
//        animSet.playSequentially(animators);
        animSet.play(animator2).after(animator1);
        animSet.play(animator3).after(animator2);
//        animSet.play(animator4).after(animator3);
//        animSet.play(animator5).after(animator4);
        animSet.start();
        return animSet;
    }

    public static AnimatorSet stopAnimation(ObjectAnimator animator1, ObjectAnimator animator2){
        AnimatorSet animSetStop = new AnimatorSet();
//        animSet.playSequentially(animators);
        animSetStop.play(animator1).before(animator2);
//        animSetStop.play(animator3).after(animator2);
//        animSet.play(animator4).after(animator3);
//        animSet.play(animator5).after(animator4);
        animSetStop.start();
        return animSetStop;
    }

    public static ObjectAnimator getBigGiftAnimatorScale(final View target, int duration, int startDelay){
        PropertyValuesHolder anim4 = PropertyValuesHolder.ofFloat("scaleX",
                0.7f, 1.1f,0.7f);
        PropertyValuesHolder anim5 = PropertyValuesHolder.ofFloat("scaleY",
                0.7f, 1.1f,0.7f);

//        ObjectAnimator animator =  ObjectAnimator.ofPropertyValuesHolder(anim4,anim5);
        ObjectAnimator animator2 =  ObjectAnimator.ofPropertyValuesHolder(target,anim4,anim5);
        animator2.setStartDelay(startDelay);
        animator2.setDuration(duration);

//        ObjectAnimator a = ObjectAnimator.ofFloat(target,"scaleX",1.0f,1.5f,1.0f);
        return animator2;
    }

    public static ObjectAnimator getBitGiftAnimator(final View target, float star, float end, int duration, int startDelay){
        PropertyValuesHolder translationY = PropertyValuesHolder.ofFloat("translationY", 0, UIUtils.getDimen(R.dimen.dp_200));
        PropertyValuesHolder translationX = PropertyValuesHolder.ofFloat("translationX", star,end);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0.5f,1.0f,0.5f);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(target, translationY,translationX, alpha);
        animator.setStartDelay(startDelay);
        animator.setDuration(duration);
//        animator.setInterpolator(interpolator);
        return animator;

    }

    public static AnimationDrawable getAnimationDrawable(ImageView target){
        AnimationDrawable animationDrawable = (AnimationDrawable) target.getDrawable();

        return animationDrawable;
    }

    public static Animator getFireWorkAnimator(final ImageView target, int duration, int startDelay){
        PropertyValuesHolder anim1 = PropertyValuesHolder.ofFloat("scaleX",
                1.0f, 2.0f);
        PropertyValuesHolder anim2 = PropertyValuesHolder.ofFloat("scaleY",
                1.0f, 2.0f);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 0.5f,1.0f,0.5f);
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(target, anim1,anim2, alpha);
        animator.setDuration(duration);
        animator.setStartDelay(startDelay);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                target.setVisibility(View.VISIBLE);
            }
        });

        return animator;
    }



}
