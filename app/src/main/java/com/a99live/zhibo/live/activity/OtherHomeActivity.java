package com.a99live.zhibo.live.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.imchatc2c.ChatActivity;
import com.a99live.zhibo.live.activity.imchatc2c.FriendshipManageView;
import com.a99live.zhibo.live.activity.user.AttentionActivity;
import com.a99live.zhibo.live.activity.user.FansActivity;
import com.a99live.zhibo.live.adapter.OtherInfoAdapter;
import com.a99live.zhibo.live.glide.GlideCircleTransform;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.RoomProtocol;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.UIUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendStatus;

import java.util.ArrayList;
import java.util.Map;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 他人主页
 * Created by fuyk on 2016/9/5.
 */
public class OtherHomeActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener ,FriendshipManageView{

    private UserProtocol userProtocol;
    private RoomProtocol roomProtocol;
    private String uid;

    private OtherInfoAdapter otherInfoAdapter;

    private GridViewWithHeaderAndFooter liveGrid;

    private ImageView iv_head, iv_gender;
    private TextView tv_title, tv_nick_name, tv_identity, tv_identity_num, tv_sign, tv_attention, tv_attention_num, tv_fans, tv_fans_num, tv_is_attention, tv_person_letter;
    private RelativeLayout layout_back, layout_attention, layout_fans;
    private Boolean is_attention = false;
    private View headerView;
    private Map<String, String> userMap;
    private ArrayList<Map<String, String>> allData;
    private String identity;
    private String mobie;
    private String avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_home);
        liveGrid = (GridViewWithHeaderAndFooter) findViewById(R.id.live_grid);
        headerView = LayoutInflater.from(this).inflate(R.layout.other_home_head, null);
        liveGrid.addHeaderView(headerView);


        initData();
        initView();
    }

    public static void goOtherHomeActivity(Context context,String code){
        Intent intent = new Intent(context, OtherHomeActivity.class);
        intent.putExtra(TCConstants.UCODE, code);
        context.startActivity(intent);
    }

    private void initData() {
        uid = getIntent().getStringExtra(TCConstants.UCODE);
        userProtocol = new UserProtocol();
        roomProtocol = new RoomProtocol();
        allData = new ArrayList<>();
        otherInfoAdapter = new OtherInfoAdapter(this, allData);
        liveGrid.setAdapter(otherInfoAdapter);
        getOtherInfo(uid);
    }

    /**初始化控件*/
    private void initView() {

        layout_back = (RelativeLayout) findViewById(R.id.layout_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(R.string.other_home);

        iv_head = (ImageView) headerView.findViewById(R.id.iv_head);
        tv_nick_name = (TextView) headerView.findViewById(R.id.tv_nick_name);
        iv_gender = (ImageView) headerView.findViewById(R.id.iv_gender);
        tv_identity = (TextView) headerView.findViewById(R.id.tv_identity);
        tv_identity_num = (TextView) headerView.findViewById(R.id.tv_identity_num);
        tv_sign = (TextView) headerView.findViewById(R.id.tv_sign);
        tv_attention_num = (TextView) headerView.findViewById(R.id.tv_attention_num);
        tv_fans_num = (TextView) headerView.findViewById(R.id.tv_fans_num);
        tv_is_attention = (TextView) headerView.findViewById(R.id.tv_is_attention);
        tv_person_letter = (TextView) headerView.findViewById(R.id.tv_person_letter);

        layout_attention = (RelativeLayout) headerView.findViewById(R.id.layout_attention);
        layout_fans = (RelativeLayout) headerView.findViewById(R.id.layout_fans);

        layout_back.setOnClickListener(this);
        layout_attention.setOnClickListener(this);
        layout_fans.setOnClickListener(this);
        tv_is_attention.setOnClickListener(this);
        tv_person_letter.setOnClickListener(this);

        liveGrid.setOnItemClickListener(this);

    }

    private int getLastVisiblePosition = 0, lastVisiblePositionY = 0;
    private void setGridViewScrollListener(){
        if (liveGrid != null){
            liveGrid.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                        //滚动到底部
                        if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                            View v = (View) view.getChildAt(view.getChildCount() - 1);
                            int[] location = new int[2];
                            v.getLocationOnScreen(location);//获取在整个屏幕内的绝对坐标
                            int y = location[1];

                            if (view.getLastVisiblePosition() != getLastVisiblePosition && lastVisiblePositionY != y)//第一次拖至底部
                            {
                                Toast.makeText(view.getContext(), "已经拖动至底部，再次拖动即可翻页", Toast.LENGTH_SHORT).show();
                                getLastVisiblePosition = view.getLastVisiblePosition();
                                lastVisiblePositionY = y;
                                return;
                            } else if (view.getLastVisiblePosition() == getLastVisiblePosition && lastVisiblePositionY == y)//第二次拖至底部
                            {
//                                mCallback.execute();
                            }
                        }

                        //未滚动到底部，第二次拖至底部都初始化
                        getLastVisiblePosition = 0;
                        lastVisiblePositionY = 0;
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                }
            });
        }
    }

    /**获取他人主页信息*/
    private void getOtherInfo(String uid){
        LiveRequestParams params = new LiveRequestParams();
        params.put("uid", uid);

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
                                    initTopPage(dataMap);
                                    initBottom(dataMap);
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
                        UIUtils.showToast(R.string.net_error);
                    }
                });
    }

    /**加载底布局*/
    private void initBottom(Map<String ,String > dataMap) {
        if (otherInfoAdapter != null){
            String videoList = dataMap.get("videoList");
            ArrayList<Map<String, String>> videoListList = JsonUtil.getListMapByJson(videoList);
            allData.addAll(videoListList);
//            otherInfoAdapter = new OtherInfoAdapter(this,videoListList);
            otherInfoAdapter.notifyDataSetChanged();
//            liveGrid.setAdapter(otherInfoAdapter);
        }
    }

    /**加载头布局*/
    private void initTopPage(Map<String, String> dataMap) {
        if (dataMap != null){
            String user = dataMap.get("user");
            String follow = dataMap.get("isFollow");
            ArrayList<Map<String, String>> userList = JsonUtil.getListMapByJson(user);
            userMap = userList.get(0);
            avatar = userMap.get("avatar");
            //头像
            Glide.with(this)
                    .load(avatar)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .centerCrop()
                    .placeholder(R.mipmap.head)
                    .crossFade()
                    .transform(new GlideCircleTransform(this))
                    .into(iv_head);
            //昵称
            tv_nick_name.setText(userMap.get("nickname"));
            //性别
//            tv_gender.setText(userMap.get("gender"));
            if (userMap.get("gender").equals("男")){
                iv_gender.setImageResource(R.mipmap.boy);
            } else if (userMap.get("gender").equals("女")){
                iv_gender.setImageResource(R.mipmap.girl);
            }
            //99号
            identity = userMap.get("identity");
            mobie = userMap.get("mobie");
            tv_identity_num.setText(identity);
            //个人签名
            if (TextUtils.isEmpty(userMap.get("sign"))){
                tv_sign.setVisibility(View.GONE);
            }else {
                tv_sign.setText(userMap.get("sign"));
            }
            //关注
            tv_attention_num.setText(userMap.get("follow"));
            //粉丝
            tv_fans_num.setText(userMap.get("fans"));
            //关注
            if ("0".equals(follow)){
                //没有关注
                tv_is_attention.setText(R.string.unattention);
                tv_is_attention.setBackgroundResource(R.drawable.shape_get_code);
                tv_is_attention.setTextColor(getResources().getColor(R.color.text_5a5b));
            }else {
                //已关注
                tv_is_attention.setText(R.string.attentioned);
                tv_is_attention.setBackgroundResource(R.drawable.shape_btn_press);
                tv_is_attention.setTextColor(getResources().getColor(R.color.text_999));
                is_attention = true;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_back:
                back();
                break;
            case R.id.layout_attention:
                goAttentionActivity();
                break;
            case R.id.layout_fans:
                goMyFansActivity();
                break;
            case R.id.tv_is_attention:
                if (!is_attention){

                    //关注
                    follow(userMap.get("user_id"));
                }else {

                    //取消关注
                    unFollow(userMap.get("user_id"));
                }
                break;
            case R.id.tv_person_letter:
                if (!TextUtils.isEmpty(identity)){
                    if (identity.equals(SPUtils.getString(SPUtils.USER_IDENTITY))){
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

//                    if (FriendshipInfo.getInstance().isFriend(identity)){
//                        ProfileActivity.navToProfile(this, identity);
                        ChatActivity.navToChat(OtherHomeActivity.this,identity,tv_nick_name.getText().toString(),avatar, TIMConversationType.C2C);
//                    }else{
//                        Intent person = new Intent(this,AddFriendActivity.class);
//                        person.putExtra("id",identity);
//                        person.putExtra("name",tv_nick_name.getText().toString());
//                        startActivity(person);
//                    }
//                    ChatActivity.navToChat(OtherHomeActivity.this,identity, TIMConversationType.C2C);
                }else{
                    UIUtils.showToast("用户数据未加载");
                }

//                UIUtils.showToast("该功能暂时未开通");
                break;
        }
    }

    /**前往我的关注页面*/
    private void goAttentionActivity() {
        Intent intent = new Intent(this, AttentionActivity.class);
        intent.putExtra("uid", uid);
        startActivity(intent);
    }

    /**前往我的粉丝页面*/
    private void goMyFansActivity() {
        Intent intent = new Intent(this, FansActivity.class);
        intent.putExtra("uid", uid);
        startActivity(intent);
    }

    /**
     * 关注接口
     */
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
                                tv_is_attention.setText(R.string.attentioned);
                                tv_is_attention.setBackgroundResource(R.drawable.shape_btn_press);
                                tv_is_attention.setTextColor(getResources().getColor(R.color.text_999));
                                is_attention = true;
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
                                tv_is_attention.setText(R.string.unattention);
                                tv_is_attention.setBackgroundResource(R.drawable.shape_get_code);
                                tv_is_attention.setTextColor(getResources().getColor(R.color.text_5a5b));
                                is_attention = false;
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
                        Log.d("livelog", "取消关注失败");
                    }
                });
    }

    private void back() {
        overridePendingTransition(R.anim.scale_big_in, R.anim.tran_right_out);
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Map<String,String> item = otherInfoAdapter.getItem(position);
        if (!"0".equals(item.get("is_live")) ){
            Intent intent = new Intent(this, LivePlayerActivity.class);
            intent.putExtra("room_id", item.get("room_id"));
            intent.putExtra("ucode", item.get("ucode"));
//            intent.putExtra(TCConstants.COVER_PIC, avator);
            this.startActivity(intent);
            this.overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
        }else {
            enterPlayRoom(item.get("id"), item.get("bg_img_url"));
        }
    }



    /**
     * 进入录播接口
     */
    private void enterPlayRoom(String lu_id, final String avator) {

        goPlayRoom(lu_id, avator);

    }

    /**
     * 进入录播房间
     *
     * @param lu_id
     */
    private void goPlayRoom(String lu_id, String avator) {
        Intent intent = new Intent(this, VoderActivity.class);
        intent.putExtra("lu_id", lu_id);
        intent.putExtra(TCConstants.COVER_PIC, avator);
        startActivity(intent);
        this.overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
    }

    @Override
    public void onAddFriend(TIMFriendStatus status) {

    }

    @Override
    public void onDelFriend(TIMFriendStatus status) {

    }

    @Override
    public void onChangeGroup(TIMFriendStatus status, String groupName) {

    }
}
