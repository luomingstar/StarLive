package com.a99live.zhibo.live.gift;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.model.LevelAnimationModel;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.view.weight.NewCircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by JJGCW on 2017/3/27.
 */

public class ComeInAnimation extends FrameLayout {

    private RelativeLayout comein_bg;
    private RelativeLayout comein_vip_bg;
    private ComeInLevelView comeInLevelView;
    private int one,two,three;
    private ImageView bg;
    private TextView lv_tv;
    private TextView name_tv;
    private NewCircleImageView avatarImg;
    private TextView vip_lv_tv;
    private TextView vip_name_tv;
    private Context mContext;
    private ArrayList<LevelAnimationModel> levels = new ArrayList<>();
    private PropertyValuesHolder translationX;
    private ObjectAnimator objectAnimator;

    public ComeInAnimation(@NonNull Context context) {
        this(context,null);
    }

    public ComeInAnimation(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ComeInAnimation(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.comein_animation,this,true);
        initView();
    }

    public void setComeInView(ComeInLevelView comeInView){
        this.comeInLevelView = comeInView;
    }

    private void initView(){
        comein_bg = (RelativeLayout) findViewById(R.id.comein_bg);
        comein_vip_bg = (RelativeLayout) findViewById(R.id.comein_vip_bg);
        //nomal
        bg = (ImageView) findViewById(R.id.bg);
        lv_tv = (TextView) findViewById(R.id.lv_tv);
        name_tv = (TextView) findViewById(R.id.name_tv);
        //vip
        avatarImg = (NewCircleImageView) findViewById(R.id.avatar);
        vip_lv_tv = (TextView) findViewById(R.id.vip_lv_tv);
        vip_name_tv = (TextView) findViewById(R.id.vip_name_tv);

        comein_bg.setVisibility(INVISIBLE);
        comein_vip_bg.setVisibility(INVISIBLE);
        levels.clear();
    }

    /**
     * 21-40   41-80  80以上
     * @param level
     */
    public void showLevel(String identity , String level , String avatar , String name){
        if (mContext == null){
            return;
        }
        int lv = Integer.parseInt(level);
        String special = SPUtils.getString(SPUtils.LEVEL_SPECIAL);
        Log.d("livelog",special);
        one = 20;
        two = 40;
        three = 80;
        if (!TextUtils.isEmpty(special)){
            JSONArray jsonArray;
            String[] lll;
            try {
                jsonArray = new JSONArray(special);
                lll = new String[jsonArray.length()];
                for (int i=0;i<jsonArray.length();i++){
                    lll[i] = jsonArray.getString(i);
                }

                if (  lll.length >= 3){
                    one = Integer.parseInt(lll[0]);
                    two = Integer.parseInt(lll[1]);
                    three = Integer.parseInt(lll[2]);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
//            String replace = special.replace("[", "").replace("]", "");
//            String[] split = replace.split(",");
//            Arrays.sort(split);

//            for (int i=0;i < split.length;i++){
//
//
//            }

        }
        if (lv <= one)return;

        if (lv > one && lv <= two){
            startAnimation(0,identity,level,avatar,name);
        }else if (lv > two && lv <= three){
            startAnimation(1,identity,level,avatar,name);
        }else if (lv > three){
            startAnimation(2,identity,level,avatar,name);
        }
    }

    private void startAnimation(int model, String identity, String level, String avatar, String name){
        if (mContext == null){
            return;
        }
//        comein_bg.setVisibility(INVISIBLE);
//        comein_vip_bg.setVisibility(INVISIBLE);
        if (comein_bg.getVisibility() == VISIBLE || comein_vip_bg.getVisibility() == VISIBLE){
            LevelAnimationModel lv = new LevelAnimationModel(level,avatar,name,identity);
            levels.add(lv);
            return;
        }
        switch (model){
            case 0:
                lv_tv.setText("LV"+level);
                SpannableString spannableString = new SpannableString(name+"加入直播间");
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#ff6100"));
                spannableString.setSpan(colorSpan, 0, name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                name_tv.setText(spannableString);
                bg.setImageResource(R.drawable.level_21_40_animation);
                break;
            case 1:
                lv_tv.setText("LV"+level);
                SpannableString spannableString1 = new SpannableString(name+"加入直播间");
                ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor("#fe4334"));
                spannableString1.setSpan(colorSpan1, 0, name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                name_tv.setText(spannableString1);
                bg.setImageResource(R.drawable.level_41_80_animation);
                break;
            case 2:
                vip_lv_tv.setText("LV"+level);
                SpannableString spannableString2 = new SpannableString(name+"来到了你的直播间");
                ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(Color.parseColor("#fef432"));
                spannableString2.setSpan(colorSpan2, 0, name.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                vip_name_tv.setText(spannableString2);

                Glide.with(mContext)
                        .load(avatar)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .placeholder(R.mipmap.logo_head)
                        .dontAnimate()
                        .into(avatarImg);
                break;
        }
        if (translationX == null){
            getPropertyValuesHolder();
        }
        if (model >1){
            objectAnimator = ObjectAnimator.ofPropertyValuesHolder(comein_vip_bg, translationX);

            objectAnimator.setDuration(4000);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    comein_vip_bg.setVisibility(INVISIBLE);
                    animationLoop();
                    comein_vip_bg.clearAnimation();
                    animation.cancel();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    comein_vip_bg.setVisibility(VISIBLE);
                }
            });
            objectAnimator.start();
        }else{
            objectAnimator = ObjectAnimator.ofPropertyValuesHolder(comein_bg, translationX);

            objectAnimator.setDuration(4000);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    comein_bg.setVisibility(INVISIBLE);
                    animationLoop();
                    comein_bg.clearAnimation();
                    animation.cancel();
                }

                @Override
                public void onAnimationStart(Animator animation) {
                    super.onAnimationStart(animation);
                    comein_bg.setVisibility(VISIBLE);
                }
            });
            objectAnimator.start();
        }


    }

    private void getPropertyValuesHolder(){
        int width = getWidth();
        Keyframe keyframe1 = Keyframe.ofFloat(0.0f,width);
        Keyframe keyframe2 = Keyframe.ofFloat(0.3f,100);
//        Keyframe keyframe3 = Keyframe.ofFloat(0.2f,90);
//        Keyframe keyframe4 = Keyframe.ofFloat(0.3f,80);
//        Keyframe keyframe5 = Keyframe.ofFloat(0.4f,70);
//        Keyframe keyframe6 = Keyframe.ofFloat(0.5f,60);
//        Keyframe keyframe7 = Keyframe.ofFloat(0.6f,50);
//        Keyframe keyframe8 = Keyframe.ofFloat(0.7f,30);
//        Keyframe keyframe9 = Keyframe.ofFloat(0.8f,20);
        Keyframe keyframe10 = Keyframe.ofFloat(0.9f,0);
//        Keyframe keyframe11 = Keyframe.ofFloat(0.95f,0);
        Keyframe keyframe12 = Keyframe.ofFloat(1.0f,-width);

        //                keyframe3,
//                keyframe4, keyframe5,
//                keyframe6, keyframe7, keyframe8, keyframe9,
//                keyframe11,
        translationX = PropertyValuesHolder.ofKeyframe("translationX", keyframe1,
                keyframe2,
//                keyframe3,
//                keyframe4,
//                keyframe5,
//                keyframe6, keyframe7, keyframe8, keyframe9,
                keyframe10,
//                keyframe11,
                keyframe12);
    }

    private void animationLoop(){
        if (levels != null){
            if (levels.size()>0){
                LevelAnimationModel levelAnimationModel = levels.get(0);
                levels.remove(0);
                showLevel(levelAnimationModel.getIdentity(),levelAnimationModel.getLevel(),
                        levelAnimationModel.getAvatar(),levelAnimationModel.getName());
            }
        }
    }

    public void destroy(){
        mContext = null;
    }

}
