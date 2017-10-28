package com.a99live.zhibo.live.gift;

import android.animation.AnimatorSet;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.a99live.zhibo.live.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by JJGCW on 2016/10/12.
 */

public class GiftAnimationView extends RelativeLayout {
    private Context mContext;

    private GiftFrameLayout giftFrameLayout1;
    private GiftFrameLayout giftFrameLayout2;
    List<GiftSendModel> giftSendModelList = new ArrayList<GiftSendModel>();
    List<GiftSendModel> bigGiftSendModelList = new ArrayList<>();
//    List<GiftSendModel> giftSendModels = new ArrayList<>();
    private Handler mHandler;
    private BigGiftFrameLayout bigGiftFrameLayout;
    private boolean isDestroy = false;

    public GiftAnimationView(Context context) {
        this(context,null);
    }

    public GiftAnimationView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GiftAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        View view = View.inflate(context, R.layout.gift_animation_view,this);

        giftFrameLayout1 = (GiftFrameLayout) findViewById(R.id.gift_layout1);
        giftFrameLayout2 = (GiftFrameLayout) findViewById(R.id.gift_layout2);
        bigGiftFrameLayout = (BigGiftFrameLayout) findViewById(R.id.gift_big);

    }

    public void setHandler(Handler handler){
        this.mHandler = handler;
        giftFrameLayout1.setHandler(mHandler);
        giftFrameLayout2.setHandler(mHandler);
        bigGiftFrameLayout.setHandler(mHandler);
    }

    public void sendGift(Map<String,String>data){
        starGiftAnimation(createGiftSendModel(data));
    }

    public void sendBigGift(Map<String,String>data){
        starBigGiftAnimation(createGiftSendModel(data));
    }

    private void starBigGiftAnimation(GiftSendModel model){
        if (isDestroy){
            return;
        }
        if (bigGiftFrameLayout.isShow()){
            bigGiftSendModelList.add(model);
        }else{
            bigGiftFrameLayout.setModel(model);
            bigGiftFrameLayout.startAnimator(model);
        }

    }


    private GiftSendModel createGiftSendModel(Map<String,String>data){
        GiftSendModel gsm = new GiftSendModel(1);
        gsm.setGift_id(data.get("gift_id"));
//        if (data.get("user_id").equals(SPUtils.getString(SPUtils.USER_ID))){
//            gsm.setNickname("我");
//        }else{
        gsm.setNickname(data.get("user_name"));
//        }
        gsm.setUserId(data.get("user_id"));
        gsm.setGiftName(data.get("gift_name"));
        gsm.setUserAvatarRes(data.get("user_avatar"));
        gsm.setGiftImg(data.get("img_path"));
        return gsm;
    }

    private void starGiftAnimation(GiftSendModel model){
        if (isDestroy){
            return;
        }
//        giftSendModels.add(model);
        if (giftFrameLayout1.isShowing() && giftFrameLayout1.giftSendModel != null ){
            if ( model.getUserId().equals(giftFrameLayout1.giftSendModel.getUserId())
                    && model.getGift_id().equals(giftFrameLayout1.giftSendModel.getGift_id())) {
                sendGiftAnimationContinue(giftFrameLayout1, model);
                return;
            }
        }

        if (giftFrameLayout2.isShowing() && giftFrameLayout2.giftSendModel != null ){
            if (model.getUserId().equals(giftFrameLayout2.giftSendModel.getUserId())
                    && model.getGift_id().equals(giftFrameLayout2.giftSendModel.getGift_id())){
                sendGiftAnimationContinue(giftFrameLayout2,model);
                return;
            }
        }

        if (!giftFrameLayout1.isShowing()) {//没有显示
            giftFrameLayout1.giftSendModel = null;
            sendGiftAnimation(giftFrameLayout1,model);
        }else if(giftFrameLayout1.isShowing() && giftFrameLayout1.giftSendModel != null
                && model.getUserId().equals(giftFrameLayout1.giftSendModel.getUserId())
                && model.getGift_id().equals(giftFrameLayout1.giftSendModel.getGift_id())){
            sendGiftAnimationContinue(giftFrameLayout1,model);
        }
        else if(!giftFrameLayout2.isShowing()){//没有显示
            giftFrameLayout2.giftSendModel = null;
            sendGiftAnimation(giftFrameLayout2,model);
        }else if (giftFrameLayout2.isShowing() &&giftFrameLayout2.giftSendModel != null
                && model.getUserId().equals(giftFrameLayout2.giftSendModel.getUserId())
                && model.getGift_id().equals(giftFrameLayout2.giftSendModel.getGift_id()) ){
            sendGiftAnimationContinue(giftFrameLayout2,model);
        }
        else{
            for (int i=0;i< giftSendModelList.size();i++){
                GiftSendModel modelGift = giftSendModelList.get(i);
                if (model.getGift_id().equals(modelGift.getGift_id()) && model.getUserId().equals(modelGift.getUserId())){
                    modelGift.getGiftCount();
                    giftSendModelList.get(i).setGiftCount(modelGift.getGiftCount()+1);

                    return;
                }
            }

            giftSendModelList.add(model);

        }
    }
    private void sendGiftAnimationContinue(final GiftFrameLayout view, GiftSendModel model){
            view.startNum();
    }

    private void sendGiftAnimation(final GiftFrameLayout view, final GiftSendModel model){
        if (isDestroy){
            return;
        }
        view.setModel(model);
        AnimatorSet animatorSet = view.startAnimation(model.getGiftCount());
//        animatorSet.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                if (model.getUserId().equals(giftFrameLayout1.getTag())){
//                    giftFrameLayout1.setTag(null);
//                }
//                if (model.getUserId().equals(giftFrameLayout2.getTag())){
//                    giftFrameLayout2.setTag(null);
//                }
//                synchronized (giftSendModelList) {
//                    if (giftSendModelList.size() > 0) {
//                        view.startAnimation(giftSendModelList.get(giftSendModelList.size() - 1).getGiftCount());
//                        giftSendModelList.remove(giftSendModelList.size() - 1);
//                    }
//                }
//            }
    }
    public void giftLoop(){
                synchronized (giftSendModelList) {
                    if (giftSendModelList.size() > 0) {
                        starGiftAnimation(giftSendModelList.get(giftSendModelList.size() - 1));
                        giftSendModelList.remove(giftSendModelList.size() - 1);
                    }
                }
    }

    public void bigGiftLoop() {
        synchronized (bigGiftSendModelList) {
            if (bigGiftSendModelList.size() > 0) {
                starBigGiftAnimation(bigGiftSendModelList.get(bigGiftSendModelList.size() - 1));
                bigGiftSendModelList.remove(bigGiftSendModelList.size() - 1);
            }
        }
    }

    public void destroyed(){
       isDestroy = true;
    }
}
