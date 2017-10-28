package com.a99live.zhibo.live.fragment.User;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.LoginActivity;
import com.a99live.zhibo.live.activity.imchatc2c.PrivateLetter;
import com.a99live.zhibo.live.activity.setting.SetActivity;
import com.a99live.zhibo.live.activity.user.AccountActivity;
import com.a99live.zhibo.live.activity.user.Approve;
import com.a99live.zhibo.live.activity.user.ApproveOver;
import com.a99live.zhibo.live.activity.user.ApproveSuccess;
import com.a99live.zhibo.live.activity.user.AttentionActivity;
import com.a99live.zhibo.live.activity.user.EarnActivity;
import com.a99live.zhibo.live.activity.user.FansActivity;
import com.a99live.zhibo.live.activity.user.FansContribution;
import com.a99live.zhibo.live.activity.user.MyGradesActivity;
import com.a99live.zhibo.live.activity.user.MyLiveActivity;
import com.a99live.zhibo.live.activity.user.UserInfoActivity;
import com.a99live.zhibo.live.adapter.FansAvatarAdapter;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.RankProtocol;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.LoginManager;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.Tools;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.utils.YMClick;
import com.a99live.zhibo.live.view.weight.NewCircleImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tencent.android.tpush.XGPushManager;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/8/24.
 */
public class UserFragment extends RxFragment {
    private UserProtocol userProtocol;
    private RankProtocol rankProtocol;

//    @Bind(R.id.layout_edit)
//    RelativeLayout layout_edit;

    @Bind(R.id.layout_earning)
    RelativeLayout layout_earning;

    @Bind(R.id.layout_account)
    RelativeLayout layout_account;

    @Bind(R.id.iv_head)
    NewCircleImageView iv_head;

    @Bind(R.id.iv_approve_me)
    ImageView iv_approve_me;

    @Bind(R.id.tv_name)
    TextView tv_name;

    @Bind(R.id.iv_sex)
    ImageView iv_sex;

    @Bind(R.id.tv_user_id_num)
    TextView tv_user_id_num;

    @Bind(R.id.tv_attention_num)
    TextView tv_attention_num;

    @Bind(R.id.tv_fans_num)
    TextView tv_fans_num;

    /**头像列表*/
    @Bind(R.id.rv_fans_avatar)
    RecyclerView rv_fans_avatar;

    @Bind(R.id.tv_my_earning)
    TextView tv_my_earning;

    @Bind(R.id.tv_mycount_money)
    TextView tv_mycount_money;

    @Bind(R.id.tv_user_verify)
    TextView tv_user_verify;

    @Bind(R.id.my_level)
    ImageView my_level;

    @Bind(R.id.user_message)
    ImageView user_message;

    private String userVerify;//用户认证的标识

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(TCConstants.IM_C2C_MESSAGE_HINT_GONE);
        filter.addAction(TCConstants.IM_C2C_MESSAGE_HINT_SHOW);
        getActivity().registerReceiver(myReceiver, filter);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("UserFragment");
        getUserInfo();
        getFansAvtar();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("UserFragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_user, null);
        ButterKnife.bind(this, view);
        setDefaultInfo();
        return view;
    }

    private void setDefaultInfo() {
        Glide.with(this)
                .load(SPUtils.getString(SPUtils.USER_AVATAR))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .placeholder(R.mipmap.head)
                .dontAnimate()
                .into(iv_head);
        tv_name.setText(SPUtils.getString(SPUtils.USER_NAME));
        if (SPUtils.getString(SPUtils.USER_GENDER).equals("男")){
            iv_sex.setImageResource(R.mipmap.boy);
        }else if (SPUtils.getString(SPUtils.USER_GENDER).equals("女")){
            iv_sex.setImageResource(R.mipmap.girl);
        }
        tv_user_id_num.setText(SPUtils.getString(SPUtils.USER_IDENTITY));
        tv_attention_num.setText("0");
        tv_fans_num.setText("0");
        //等级(默认都为1)
        String level = SPUtils.getString(SPUtils.USER_LEVEL);
        if (TextUtils.isEmpty(level)){
            level = "1";
        }
        int levelIsOk = 1;
        try {
            levelIsOk = Integer.parseInt(level);
        }catch (Exception e){
            levelIsOk = 1;
        }
        if (levelIsOk >80){
            levelIsOk = 80;
        }
        int drawableId = Tools.getDrawableId(getContext(), "level"+levelIsOk);
        my_level.setImageResource(drawableId);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        userProtocol = new UserProtocol();
        rankProtocol = new RankProtocol();
    }

    /**获取用户信息接口*/
    private void getUserInfo( ){
        LiveRequestParams params = new LiveRequestParams();

        userProtocol.MyHomeInfo(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            int code = Integer.parseInt(map.get("code"));
                            if (code == 0){
                                ArrayList<Map<String, String>> append = JsonUtil.getListMapByJson(map.get("append"));
                                Map<String, String> diamond = append.get(0);
                                tv_mycount_money.setText(diamond.get("Z") + "钻");
                                tv_my_earning.setText(diamond.get("B") + "币");

                                //保存用户账户
                                SPUtils.putString(SPUtils.JIU_ZUAN, diamond.get("Z"));
                                SPUtils.putString(SPUtils.JIU_BI, diamond.get("B"));
                                SPUtils.putString(SPUtils.JIU_RMB, diamond.get("RMB"));
                                SPUtils.putString(SPUtils.ACCOUNT, diamond.get("account"));

                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(map.get("data"));
                                if (dataList.size()>0){
                                    Map<String, String> dataMap = dataList.get(0);
                                    initPage(dataMap);
                                }
                            }else if (code == 20000006) {
                                Toast.makeText(getActivity(), "房间未找到，请刷新列表", Toast.LENGTH_SHORT).show();
//                                onRefresh();
                            } else if (code == 111111110 || code == 11111113) {
                                LoginManager.clearUser();
                                goLoginActivity();
                            } else if (code == 11111122) {
                                UIUtils.showToast("账号在别处登录，请重新登录");
                                LoginManager.clearUser();
                                goLoginActivity();
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    /**
     * 进入登录页
     */
    private void goLoginActivity() {
        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
    }

    /**初始化用户信息*/
    private void initPage(Map<String, String> dataMap) {
        if (null != dataMap){
            Glide.with(this)
                    .load(dataMap.get("avatar"))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .placeholder(R.mipmap.head)
                    .dontAnimate()
                    .into(iv_head);
            tv_name.setText(dataMap.get("nickname"));
            if (dataMap.get("gender").equals("男")){
                iv_sex.setImageResource(R.mipmap.boy);
            }else if (dataMap.get("gender").equals("女")){
                iv_sex.setImageResource(R.mipmap.girl);
            }
            tv_user_id_num.setText(dataMap.get("identity"));
            tv_attention_num.setText(dataMap.get("follow"));
            tv_fans_num.setText(dataMap.get("fans"));
            userVerify = dataMap.get("verify");
            iv_approve_me.setVisibility(View.GONE);
            if ("0".equals(userVerify)){
                tv_user_verify.setText("未认证");
            } else if ("1".equals(userVerify)) {
                tv_user_verify.setText("待审核");
            }else if ("2".equals(userVerify)) {
                tv_user_verify.setText("已认证");
                iv_approve_me.setVisibility(View.VISIBLE);
            }else if ("3".equals(userVerify)){
                tv_user_verify.setText("认证失败");
            }else{
                tv_user_verify.setText("");
            }

            //绑定账号注册
            XGPushManager.registerPush(getActivity(), dataMap.get("ucode"));

            //设置标签
            ArrayList<Map<String, String>> tags = JsonUtil.getListMapByJson(dataMap.get("tags"));

            if (dataMap.get("tags") != null && !"".equals(dataMap.get("tags"))) {
                for (int i = 0;i<tags.size();i++){
                    XGPushManager.setTag(getActivity(), ""+ tags.get(i) );
                }
            }
            //等级(默认都为1)
            String level = dataMap.get("level");
            int levelIsOk = 1;
            try {
                levelIsOk = Integer.parseInt(level);
            }catch (Exception e){
                levelIsOk = 1;
            }
            if (levelIsOk >80){
                levelIsOk = 80;
            }
            int drawableId = Tools.getDrawableId(getContext(), "level"+levelIsOk);
            my_level.setImageResource(drawableId);


            /**保存用户信息*/
            SPUtils.putString(SPUtils.USER_NAME,dataMap.get("nickname"));
            SPUtils.putString(SPUtils.USER_AVATAR,dataMap.get("avatar"));
            SPUtils.putString(SPUtils.USER_GENDER, dataMap.get("gender"));
            SPUtils.putString(SPUtils.USER_ID,dataMap.get("user_id"));
            SPUtils.putString(SPUtils.USER_AVATAR_ID,dataMap.get("avatar_id"));
            SPUtils.putString(SPUtils.USER_IDENTITY,dataMap.get("identity"));
            SPUtils.putString(SPUtils.USER_MOBILE, dataMap.get("mobile"));
            SPUtils.putString(SPUtils.USER_BIRTHDAY, dataMap.get("birthday"));
            SPUtils.putString(SPUtils.USER_REGION, dataMap.get("region"));
            SPUtils.putString(SPUtils.USER_SIGN, dataMap.get("sign"));
            SPUtils.putString(SPUtils.USER_STAR, dataMap.get("star"));
            SPUtils.putString(SPUtils.USER_LEVEL, dataMap.get("level"));
            SPUtils.putString(SPUtils.USER_IS_DEFAULT_AVATAR, dataMap.get("is_default_avatar"));
        }
    }

    private void getFansAvtar(){
        LiveRequestParams params = new LiveRequestParams();
        params.put("page", 1);
        params.put("ucode", SPUtils.getString(SPUtils.USER_CODE));

        rankProtocol.getFansContributionInUser(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0) {
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(map.get("data"));
                                if (dataList.size() > 0){
                                    initFansAvatar(dataList);
                                }
                            }else {

                            }
                        }else {

                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    private void initFansAvatar(ArrayList<Map<String, String>> dataList){
        ArrayList<Map<String,String>> three = new ArrayList<>();
        if (dataList.size()>3){
            for (int i=0;i<3;i++){
                three.add(dataList.get(i));
            }
        }else{
            three.addAll(dataList);
        }
        rv_fans_avatar.setAdapter(new FansAvatarAdapter(getActivity(), three));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_fans_avatar.setLayoutManager(linearLayoutManager);
    }

    @Nullable
    @OnClick({R.id.rl_head, R.id.layout_attention, R.id.layout_fans, R.id.layout_my_live, R.id.layout_earning,
            R.id.layout_account, R.id.layout_set, R.id.layout_approve, R.id.layout_fans_contribute,
            R.id.layout_my_grades,R.id.user_message})
    void onClick(View view){
        switch (view.getId()){
            case R.id.rl_head:
                goUserDataActivity();
                YMClick.onEvent(getContext(),"my_info","1");
                break;
            case R.id.layout_attention:
                goAttentionActivity();
                break;
            case R.id.layout_fans:
                goMyFansActivity();
                break;
            case R.id.layout_my_live:
                goMyLiveActivity();
                YMClick.onEvent(getContext(),"my_live","1");
                break;
            case R.id.layout_earning:
                goEarnActivity();
                YMClick.onEvent(getContext(),"my_profit","1");
                break;
            case R.id.layout_account:
                goAccountActivity();
                YMClick.onEvent(getContext(),"my_account","1");
                break;
            case R.id.layout_set:
                goSetActivity();
                YMClick.onEvent(getContext(),"my_set","1");
                break;
            case R.id.layout_approve:
                if (userVerify!= null && !"".equals(userVerify)){
                    if ("0".equals(userVerify)){
                        Approve.goApprove(getActivity());
                    }else if ("1".equals(userVerify)){
                        ApproveSuccess.goApproveSuccess(getActivity());
                    }else if ("2".equals(userVerify)){
                        ApproveOver.goApproveOver(getActivity());
                    }else if ("3".equals(userVerify)){
                        Approve.goApprove(getActivity());
                    }else{
                        UIUtils.showToast("用户信息错误，重新获取");
                    }
                }else{
                    UIUtils.showToast("用户信息错误，重新获取");
                }
                YMClick.onEvent(getContext(),"my_authentication","1");
                break;
            case R.id.layout_fans_contribute:
                FansContribution.goFansContributionByUser(getActivity(),SPUtils.getString(SPUtils.USER_CODE));
                YMClick.onEvent(getContext(),"my_fans","1");
                break;
            case R.id.layout_my_grades:
                goMyGradesActivity();
                YMClick.onEvent(getContext(),"my_level","1");
                break;
            case R.id.user_message:
                PrivateLetter.goPrivateLetter(getContext());
                break;
        }
    }

    /**前往我的等级页面*/
    private void goMyGradesActivity() {
        startActivity(new Intent(getActivity(), MyGradesActivity.class));
    }

    /**前往我的收益页面*/
    private void goEarnActivity() {
        startActivity(new Intent(getActivity(), EarnActivity.class));
    }

    /**前往我的账户页面*/
    private void goAccountActivity() {
        startActivity(new Intent(getActivity(), AccountActivity.class));
    }
    /**前往我的关注页面*/
    private void goAttentionActivity() {
        startActivity(new Intent(getActivity(), AttentionActivity.class));
    }
    /**前往我的粉丝页面*/
    private void goMyFansActivity() {
        startActivity(new Intent(getActivity(), FansActivity.class));
    }
    /**前往编辑资料页面*/
    private void goUserDataActivity() {
        startActivity(new Intent(getActivity(), UserInfoActivity.class));
    }
    /**前往设置页面*/
    private void goSetActivity() {
//        startActivity(new Intent(getActivity(), SetActivity.class));
        SetActivity.goSetActivity(getActivity());
    }

    /**前往我的直播页面*/
    private void goMyLiveActivity() {
        startActivity(new Intent(getActivity(), MyLiveActivity.class));
    }

    private BroadcastReceiver myReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
//            UIUtils.showToast("show");
            if(TCConstants.IM_C2C_MESSAGE_HINT_SHOW.equals(intent.getAction())){
                setMsgUnread(true);
            }else if (TCConstants.IM_C2C_MESSAGE_HINT_GONE.equals(intent.getAction())){
                setMsgUnread(false);
            }else{
                setMsgUnread(false);
            }
        }
    };

    private void setMsgUnread(boolean isShow){
        if (user_message != null){
            if (isShow){
                user_message.setImageResource(R.drawable.icon_message_unread);
            }else{
                user_message.setImageResource(R.drawable.icon_message_read);
            }
        }
    }

}
