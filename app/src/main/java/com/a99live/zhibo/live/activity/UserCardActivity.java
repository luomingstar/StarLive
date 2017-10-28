package com.a99live.zhibo.live.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.imchatc2c.ChatActivity;
import com.a99live.zhibo.live.event.FollowPlayerEvent;
import com.a99live.zhibo.live.event.ShutUpEvent;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.presenter.TCChatRoomMgr;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.Tools;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.NewCircleImageView;
import com.bumptech.glide.Glide;
import com.tencent.TIMConversationType;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/12/7.
 */

public class UserCardActivity extends BaseActivity  {

    private UserProtocol userProtocol;

    private Boolean is_attention = true;

    private Map<String, String> userMap;

    private String ucode = "";

    private String user_id = "";

    private boolean isZhuBo;

    @Bind(R.id.background)
    RelativeLayout background;

    @Bind(R.id.iv_card_head)
    NewCircleImageView iv_card_head;

    @Bind(R.id.tv_card_name)
    TextView tv_card_name;

    @Bind(R.id.iv_card_sex)
    ImageView iv_card_sex;

    @Bind(R.id.tv_location)
    TextView tv_location;

    @Bind(R.id.tv_single)
    TextView tv_single;

    @Bind(R.id.tv_card_attention_num)
    TextView tv_card_attention_num;

    @Bind(R.id.tv_card_fans_num)
    TextView tv_card_fans_num;

    @Bind(R.id.tv_card_attention)
    TextView tv_card_attention;

    @Bind(R.id.iv_card_rank)
    ImageView iv_card_rank;

    @Bind(R.id.shutup)
    TextView mShutUp;

    @Bind(R.id.tv_card_havegift_num)
    TextView tv_card_havegift_num;

    @Bind(R.id.tv_card_sendgift_num)
    TextView tv_card_sendgift_num;

    private boolean shutUp;
    private String groupId;
    private TCChatRoomMgr chatRoom;
    private boolean isShutUped;
    private String avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_card);
        ButterKnife.bind(this);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        chatRoom = TCChatRoomMgr.getInstance();
        initData();
    }

    public static void goCardActivity(Context context,String identity,boolean isZhuBo,String groupId,boolean isShowShutUp,boolean isShutUped){
        Intent intent = new Intent(context, UserCardActivity.class);
        intent.putExtra(TCConstants.UCODE, identity);
        if (isZhuBo){
            intent.putExtra("iszhubo","1");
        }else{
            intent.putExtra("iszhubo","0");
        }
        intent.putExtra("group_id",groupId);
        intent.putExtra("shut_up",isShowShutUp ? "1" : "0");
        intent.putExtra("is_shut_uped",isShutUped? "1":"0");
        context.startActivity(intent);
    }

    private void initData() {
        Intent intent = getIntent();
        ucode = intent.getStringExtra(TCConstants.UCODE);
        String iszhubo = intent.getStringExtra("iszhubo");
        if ("1".equals(iszhubo)){
            isZhuBo = true;
        }else{
            isZhuBo = false;
        }
        groupId = intent.getStringExtra("group_id");
        String isShutUp = intent.getStringExtra("shut_up");
        if ("1".equals(isShutUp)){
            shutUp = true;
        }else {
            shutUp = false;
        }
        String shutUped = intent.getStringExtra("is_shut_uped");
        if ("1".equals(shutUped)){
            isShutUped = true;
        }else {
            isShutUped = false;
        }

        userProtocol = new UserProtocol();
        getUserCardInfo(ucode);
        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getUserCardInfo(String ucode){
        LiveRequestParams params = new LiveRequestParams();
        params.put("uid", ucode);
        params.put("type","N");

        userProtocol.OtherHomeInfo(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                String data = map.get("data");
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                                if (dataList .size() >0){
                                    Map<String, String> dataMap = dataList.get(0);
                                    initView(dataMap);
                                }
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
                        Log.d("livelog", "!!!!!!!");
                        UIUtils.showToast(R.string.net_error);
                    }
                });
    }

    private void initView(Map<String ,String > dataMap) {
        if (dataMap != null) {
            String user = dataMap.get("user");
            String follow = dataMap.get("isFollow");


            ArrayList<Map<String, String>> userList = JsonUtil.getListMapByJson(user);
            userMap = userList.get(0);
            //用户user_id
            user_id = userMap.get("user_id");

            String total_get = userMap.get("total_get");
            String total_send = userMap.get("total_send");

            tv_card_havegift_num.setText(TextUtils.isEmpty(total_get)?"0":total_get);
            tv_card_sendgift_num.setText(TextUtils.isEmpty(total_send)?"0":total_send);
            avatar = userMap.get("avatar");
            //头像
            Glide.with(this)
                    .load(avatar)
                    .centerCrop()
                    .placeholder(R.mipmap.head)
                    .dontAnimate()
                    .into(iv_card_head);
            //名称
            final String nickName = userMap.get("nickname");
            tv_card_name.setText(nickName);
            //性别
            if (userMap.get("gender").equals("男")){
                iv_card_sex.setImageResource(R.mipmap.boy);
            } else if (userMap.get("gender").equals("女")){
                iv_card_sex.setImageResource(R.mipmap.girl);
            }
            //等级(默认都为1)
            String level = userMap.get("level");
            int levelIsOk = 1;
            try {
                levelIsOk = Integer.parseInt(level);
            }catch (Exception e){
                levelIsOk = 1;
            }
            if (levelIsOk >80){
                levelIsOk = 80;
            }
            int drawableId = Tools.getDrawableId(this, "level"+levelIsOk);
            iv_card_rank.setBackgroundResource(drawableId);
            //地址
            if (TextUtils.isEmpty(userMap.get("region"))) {
                tv_location.setText("火星");
            }else {
                tv_location.setText(userMap.get("region"));
            }
            //签名
            if (TextUtils.isEmpty(userMap.get("sign"))){
                tv_single.setText("这家伙很懒，没有签名~");
            }else {
                tv_single.setText(userMap.get("sign"));
            }
            //关注
            tv_card_attention_num.setText(userMap.get("follow"));
            //粉丝
            tv_card_fans_num.setText(userMap.get("fans"));

            //是否关注
            if ("0".equals(follow)){
                //没有关注
                tv_card_attention.setText("+关注");
//                tv_card_attention.setBackgroundResource(R.drawable.shape_card_attention);
                is_attention = false;
            }else {
                //已关注
                tv_card_attention.setText(R.string.attentioned);
//                tv_card_attention.setBackgroundResource(R.drawable.shape_card_attentioned);
                is_attention = true;
            }

            if (shutUp){
                mShutUp.setVisibility(View.VISIBLE);
                if (isShutUped){
                    mShutUp.setText("已禁言");
                }else{
                    mShutUp.setText("禁言");
                }
                mShutUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isShutUped){
                            mShutUp.setClickable(false);
                            chatRoom.setShutUp(groupId,ucode,3600,new ShutUp(){

                                @Override
                                public void callBack(int code, String id,String desc) {
                                    if (code == 0){
                                        isShutUped = true;
                                        mShutUp.setText("已禁言");
                                        EventBus.getDefault().post(new ShutUpEvent(id,nickName));
                                    }else{
                                        isShutUped = true;
                                        mShutUp.setText("已禁言");
                                        EventBus.getDefault().post(new ShutUpEvent(id,nickName));
                                    }
                                    mShutUp.setClickable(true);
                                }
                            });
                        }else{
                            //取消禁言
                            UIUtils.showToast("已禁言");
                        }
                    }
                });
            }else{
                mShutUp.setVisibility(View.GONE);
            }
        }
    }

    public interface ShutUp{
        void callBack(int code,String id,String s);
    }

    @OnClick({R.id.iv_card_finish, R.id.layout_card_attention, R.id.layout_card_fans,
            R.id.tv_card_attention,R.id.send_message})
    void onClick(View view){
        switch (view.getId()){
            case R.id.iv_card_finish:
                finish();
                break;
//            case R.id.layout_card_attention:
//                goAttentionActivity();
//                break;
//            case R.id.layout_card_fans:
//                goMyFansActivity();
//                break;
            case R.id.tv_card_attention:
                if (!TextUtils.isEmpty(user_id)) {
                    tv_card_attention.setClickable(false);
                    if (is_attention) {
                        unFollow(user_id);
                    } else {
                        follow(user_id);
                    }
                }
                break;
            case R.id.send_message:
                if (!TextUtils.isEmpty(ucode)){
                    if (ucode.equals(SPUtils.getString(SPUtils.USER_IDENTITY))){
                        UIUtils.showToast("不能给自己发消息");
                        return;
                    }
//                    try {
//                        long mobb = Long.parseLong(mobie);
//                        if (mobb <= 999999999){
//                            UIUtils.showToast("这是机器人");
//                            return;
//                        }
//                    }catch (Exception e){
//
//                    }

//                    if (FriendshipInfo.getInstance().isFriend(ucode)){
//                        ProfileActivity.navToProfile(this, identity);
                        ChatActivity.navToChat(UserCardActivity.this,ucode,tv_card_name.getText().toString(),avatar, TIMConversationType.C2C);
//                    }else{
//                        Intent person = new Intent(this,AddFriendActivity.class);
//                        person.putExtra("id",ucode);
//                        person.putExtra("name",tv_card_name.getText().toString());
//                        startActivity(person);
//                    }
//                    ChatActivity.navToChat(OtherHomeActivity.this,identity, TIMConversationType.C2C);
                }else{
                    UIUtils.showToast("用户数据未加载");
                }
                break;
        }
    }

    /**关注接口*/
    private void follow(String ucode){
        LiveRequestParams params = new LiveRequestParams();
        params.put("fid", ucode);

        userProtocol.getFollow(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                Log.d("livelog", "关注成功");
                                tv_card_attention.setText(R.string.attentioned);
//                                tv_card_attention.setBackgroundResource(R.drawable.shape_card_attentioned);
                                is_attention = true;
                                if (isZhuBo){
                                    EventBus.getDefault().post(new FollowPlayerEvent(true));
                                }
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else{
                            UIUtils.showToast(s);
                        }
                        tv_card_attention.setClickable(true);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        tv_card_attention.setClickable(true);
                        Log.d("livelog", "关注失败");
                    }
                });
    }

    /**
     * 取消关注接口
     */
    private void unFollow(String ucode){
        LiveRequestParams params = new LiveRequestParams();
        params.put("fid", ucode);

        userProtocol.getUnFollow(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                Log.d("livelog", "取消关注成功");
                                tv_card_attention.setText("+关注");
//                                tv_card_attention.setBackgroundResource(R.drawable.shape_card_attention);
                                is_attention = false;
                                if (isZhuBo){
                                    EventBus.getDefault().post(new FollowPlayerEvent(false));
                                }
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else{
                            UIUtils.showToast(s);
                        }
                        tv_card_attention.setClickable(true);

                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        tv_card_attention.setClickable(true);
                        Log.d("livelog", "取消关注失败");
                    }
                });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.scale_big_in , R.anim.tran_bottom_out);
    }
}
