package com.a99live.zhibo.live.fragment.ListLive;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.SearchActivity;
import com.a99live.zhibo.live.activity.imchatc2c.ConversationPresenter;
import com.a99live.zhibo.live.activity.imchatc2c.ConversationView;
import com.a99live.zhibo.live.activity.imchatc2c.MessageFactory;
import com.a99live.zhibo.live.activity.imchatc2c.PrivateLetter;
import com.a99live.zhibo.live.adapter.MyPagerAdapter;
import com.a99live.zhibo.live.event.EmptyClickEvent;
import com.a99live.zhibo.live.model.Conversation;
import com.a99live.zhibo.live.model.CustomMessage;
import com.a99live.zhibo.live.model.NomalConversation;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.UIUtils;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMMessage;
import com.trello.rxlifecycle.components.support.RxFragment;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by fuyk on 2016/8/24.
 */
public class LiveListFragment extends RxFragment implements ConversationView {

    @Bind(R.id.tabs)
    TabLayout mTabLayout;

    @Bind(R.id.vp_view)
    ViewPager mViewPager;

    @Bind(R.id.line)
    View vline;

    @Bind(R.id.message_hint)
    ImageView message_hint;

    /**
     * 指示器偏移宽度
     */
    private int offsetWidth = 0;

//    private LayoutInflater mInflater;
    private List<String> mTitleList = new ArrayList<>();//页卡标题集合
    private List<Fragment> mFragment = new ArrayList<>();

    private ConversationPresenter presenter;
    private List<Conversation> conversationList = new LinkedList<>();
    private List<String> groupList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(TCConstants.IM_C2C_MESSAGE_HINT_GONE);
        filter.addAction(TCConstants.IM_C2C_MESSAGE_HINT_SHOW);
        getActivity().registerReceiver(myReceiver, filter);
    }

    public void setSome(){
//        presenter = new ConversationPresenter(LiveListFragment.this);
        if (presenter != null) {
            presenter.getConversation();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        getActivity().unregisterReceiver(myReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("LiveListFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("LiveListFragment");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live_list, null);
        ButterKnife.bind(this, view);
        initView();
        presenter = new ConversationPresenter(LiveListFragment.this);
        presenter.getConversation();
        return view;
    }

    private void initView() {
//        mInflater = LayoutInflater.from(getActivity());

        //添加页卡标题
        mTitleList.add(TCConstants.isTest ? "注意" : "广场");
        mTitleList.add(TCConstants.isTest ? "这是" : "热门");
        mTitleList.add(TCConstants.isTest ? "测试" : "关注");
        mTitleList.add(TCConstants.isTest ? "注意" : "频道");

        mFragment.add(new NewFragment());
        mFragment.add(new HotFragment());
        mFragment.add(new FocusFragment());
        mFragment.add(new ChannelFragment());

        mTabLayout.setTabMode(TabLayout.MODE_FIXED);//设置tab模式，当前为系统默认模式
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(0)));//添加tab选项卡
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(2)));
        mTabLayout.addTab(mTabLayout.newTab().setText(mTitleList.get(3)));

        MyPagerAdapter mAdapter = new MyPagerAdapter(getChildFragmentManager(), mFragment, mTitleList);
        mViewPager.setAdapter(mAdapter);//给ViewPager设置适配器
        mTabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        mTabLayout.setTabsFromPagerAdapter(mAdapter);//给Tabs设置适配器
        int screenWidth = UIUtils.getScreenWidth();
        offsetWidth = screenWidth/6;
        mViewPager.addOnPageChangeListener(pageChangedListener);
        mViewPager.setCurrentItem(1);


//        mTabLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                setIndicator(mTabLayout,11,11);
//            }
//        });
    }

    @Subscribe
    public void onEmptyClickEvent(EmptyClickEvent event) {
        mViewPager.setCurrentItem(1);
    }

    @OnClick({R.id.iv_home_search,R.id.message})
    void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_home_search:
                SearchActivity.goSearchActivty(getActivity());
//                XiaoVideoPlayerActivity.goXiaoVideoPlayerActivity(getActivity());
//                XiaoVideoList.goXiaoVideoList(getActivity());
//                XiaoVideoViewPage.goXiaoVideoViewPage(getActivity());
                break;

            case R.id.message:
                PrivateLetter.goPrivateLetter(getContext());
//                setMsgUnread(false);
                break;
        }

    }

    public void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
            tabStrip.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return;
        }

        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
        }

    }

    private ViewPager.OnPageChangeListener pageChangedListener = new ViewPager.OnPageChangeListener() {

        private boolean isAnim = false;
        private int pos = 0;

        @Override
        public void onPageSelected(int position)
        {
            Log.e("ViewPager", "position===>"+position);
            vline.setTranslationX(position*offsetWidth);
            pos = position;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            Log.d("ViewPager", "arg0="+arg0+"  arg1="+arg1+"   arg2="+arg2);
//            if(isAnim && arg1!=0)
//            {
                vline.setTranslationX(arg0*offsetWidth + arg1*offsetWidth);
//            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0)
        {
            Log.i("ViewPager", "=====>arg0="+arg0);
            //下面这个写法简直就是傻逼
//            if(arg0==1) //开始状态
//            {
//                isAnim  = true;
//            }
//            else if(arg0==2) //分界状态
//            {
//                isAnim = false;
//                vline.setTranslationX(pos*offsetWidth);
//            }
//            else if(arg0==0) //结束状态
//            {
//                vline.setTranslationX(pos*offsetWidth);
//            }
        }

    };

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


    /**
     * 设置未读tab显示
     */
    public void setMsgUnread(boolean noUnread){
        message_hint.setVisibility(noUnread?View.VISIBLE:View.GONE);
    }

    @Override
    public void initView(List<TIMConversation> conversationList) {
//        UIUtils.showToast("fragment_initview");
        this.conversationList.clear();
        groupList = new ArrayList<>();
        for (TIMConversation item:conversationList){
            switch (item.getType()){
                case C2C:
                    this.conversationList.add(new NomalConversation(item));
                    groupList.add(item.getPeer());
                    break;
                case Group:
                    break;
            }
        }
    }

    @Override
    public void updateMessage(TIMMessage message) {
//        UIUtils.showToast("fragment_updatemessage");
        if (message == null){
//            adapter.notifyDataSetChanged();
            return;
        }
        if (message.getConversation().getType() == TIMConversationType.System){
//            groupManagerPresenter.getGroupManageLastMessage();
            return;
        }
        if (message.getConversation().getType() == TIMConversationType.Group){
            return;
        }
        if (MessageFactory.getMessage(message) instanceof CustomMessage) return;
        NomalConversation conversation = new NomalConversation(message.getConversation());
//        Iterator<Conversation> iterator =conversationList.iterator();
//        while (iterator.hasNext()){
//            Conversation c = iterator.next();
//            if (conversation.equals(c)){
//                conversation = (NomalConversation) c;
//                iterator.remove();
//                break;
//            }
//        }
        conversation.setLastMessage(MessageFactory.getMessage(message));
        conversationList.add(conversation);
        Collections.sort(conversationList);
        refresh();
    }

    @Override
    public void updateFriendshipMessage() {

    }

    @Override
    public void removeConversation(String identify) {

    }

    @Override
    public void updateGroupInfo(TIMGroupCacheInfo info) {

    }

    @Override
    public void refresh() {
        Collections.sort(conversationList);
//        adapter.notifyDataSetChanged();
//        UIUtils.showToast("fragment_refresh");
//        if (getActivity() instanceof HomeActivity)
//            ((HomeActivity) getActivity()).setMsgUnread(getTotalUnreadNum() == 0);
        if(getTotalUnreadNum() != 0){
            Intent intent = new Intent();
            intent.setAction(TCConstants.IM_C2C_MESSAGE_HINT_SHOW);
            if (getContext()!=null){
                getContext().sendBroadcast(intent);
            }
//            UIUtils.showToast("fragment_show + " + getTotalUnreadNum() );
            setMsgUnread(true);
        }else{
//            UIUtils.showToast("fragment_gone");
            Intent intent = new Intent();
            intent.setAction(TCConstants.IM_C2C_MESSAGE_HINT_GONE);
            if (getContext()!=null){
                getContext().sendBroadcast(intent);
            }
            setMsgUnread(false);
        }
    }

    private long getTotalUnreadNum(){
        long num = 0;
        for (Conversation conversation : conversationList){
            num += conversation.getUnreadNum();
        }
        return num;
    }
}

