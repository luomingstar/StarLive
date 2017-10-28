package com.a99live.zhibo.live.activity.imchatc2c;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.model.Conversation;
import com.a99live.zhibo.live.model.CustomMessage;
import com.a99live.zhibo.live.model.NomalConversation;
import com.a99live.zhibo.live.utils.TCConstants;
import com.tencent.TIMConversation;
import com.tencent.TIMConversationType;
import com.tencent.TIMFriendFutureItem;
import com.tencent.TIMGroupCacheInfo;
import com.tencent.TIMMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by JJGCW on 2017/3/10.
 */

public class PrivateLetter extends BaseActivity implements ConversationView,FriendshipMessageView, View.OnClickListener {

    private final String TAG = "ConversationFragment";

//    private View view;
    private List<Conversation> conversationList = new LinkedList<>();
    private ConversationAdapter adapter;
    private ListView listView;
    private ConversationPresenter presenter;
//    private FriendshipManagerPresenter friendshipManagerPresenter;
//    private GroupManagerPresenter groupManagerPresenter;
    private List<String> groupList;
//    private FriendshipConversation friendshipConversation;
//    private GroupManageConversation groupManageConversation;

    public static void goPrivateLetter(Context context){
        Intent intent = new Intent(context,PrivateLetter.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_letter);

        listView = (ListView) findViewById(R.id.list);
        adapter = new ConversationAdapter(this, R.layout.item_conversation, conversationList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                conversationList.get(position).navToDetail(PrivateLetter.this);
//                if (conversationList.get(position) instanceof GroupManageConversation) {
//                    groupManagerPresenter.getGroupManageLastMessage();
//                }

            }
        });
//        friendshipManagerPresenter = new FriendshipManagerPresenter(this);
//        groupManagerPresenter = new GroupManagerPresenter(this);
        presenter = new ConversationPresenter(this);
        presenter.getConversation();
        registerForContextMenu(listView);

        RelativeLayout layoutBack = (RelativeLayout) findViewById(R.id.layout_back);
        layoutBack.setOnClickListener(this);
        TextView tvtitlte = (TextView) findViewById(R.id.tv_title);
        tvtitlte.setText(getResources().getString(R.string.private_letter));
    }

    @Override
    public void onResume(){
        super.onResume();
        refresh();
//        PushUtil.getInstance().reset();
    }

    /**
     * 初始化界面或刷新界面
     *
     * @param conversationList
     */
    @Override
    public void initView(List<TIMConversation> conversationList) {
//        UIUtils.showToast("privateletter_initview");
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
//        friendshipManagerPresenter.getFriendshipLastMessage();
//        groupManagerPresenter.getGroupManageLastMessage();
    }

    /**
     * 更新最新消息显示
     *
     * @param message 最后一条消息
     */
    @Override
    public void updateMessage(TIMMessage message) {
//        UIUtils.showToast("private_updatemessage");
        if (message == null){
            adapter.notifyDataSetChanged();
            return;
        }
        if (message.getConversation().getType() == TIMConversationType.System){
//            groupManagerPresenter.getGroupManageLastMessage();
            return;
        }
        if (message.getConversation().getType() == TIMConversationType.Group){
            return;
        }
        if (null == MessageFactory.getMessage(message) ){
            return;
        }else{
            if (MessageFactory.getMessage(message)instanceof CustomMessage) return;
        }
        NomalConversation conversation = new NomalConversation(message.getConversation());
        Iterator<Conversation> iterator =conversationList.iterator();
        while (iterator.hasNext()){
            Conversation c = iterator.next();
            if (conversation.equals(c)){
                conversation = (NomalConversation) c;
                iterator.remove();
                break;
            }
        }

        conversation.setLastMessage(MessageFactory.getMessage(message));
        conversationList.add(conversation);
        Collections.sort(conversationList);
        refresh();
    }

    /**
     * 更新好友关系链消息
     */
    @Override
    public void updateFriendshipMessage() {
//        friendshipManagerPresenter.getFriendshipLastMessage();
    }

    /**
     * 删除会话
     *
     * @param identify
     */
    @Override
    public void removeConversation(String identify) {
        Iterator<Conversation> iterator = conversationList.iterator();
        while(iterator.hasNext()){
            Conversation conversation = iterator.next();
            if (conversation.getIdentify()!=null&&conversation.getIdentify().equals(identify)){
                iterator.remove();
                adapter.notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * 更新群信息
     *
     * @param info
     */
    @Override
    public void updateGroupInfo(TIMGroupCacheInfo info) {
//        for (Conversation conversation : conversationList){
//            if (conversation.getIdentify()!=null && conversation.getIdentify().equals(info.getGroupInfo().getGroupId())){
//                adapter.notifyDataSetChanged();
//                return;
//            }
//        }
    }

    /**
     * 刷新
     */
    @Override
    public void refresh() {
        Collections.sort(conversationList);
        adapter.notifyDataSetChanged();
//        UIUtils.showToast("private_refresh");
//        if (getActivity() instanceof HomeActivity)
//            ((HomeActivity) getActivity()).setMsgUnread(getTotalUnreadNum() == 0);
        if(getTotalUnreadNum() != 0){
//            UIUtils.showToast("privateletter_show");
            Intent intent = new Intent();
            intent.setAction(TCConstants.IM_C2C_MESSAGE_HINT_SHOW);
            sendBroadcast(intent);
        }else{
//            UIUtils.showToast("privateletter_gone");
            Intent intent = new Intent();
            intent.setAction(TCConstants.IM_C2C_MESSAGE_HINT_GONE);
            sendBroadcast(intent);
        }
    }

    /**
     * 获取好友关系链管理系统最后一条消息的回调
     *
     * @param message 最后一条消息
     * @param unreadCount 未读数
     */
    @Override
    public void onGetFriendshipLastMessage(TIMFriendFutureItem message, long unreadCount) {
//        if (friendshipConversation == null){
//            friendshipConversation = new FriendshipConversation(message);
//            conversationList.add(friendshipConversation);
//        }else{
//            friendshipConversation.setLastMessage(message);
//        }
//        friendshipConversation.setUnreadCount(unreadCount);
//        Collections.sort(conversationList);
//        refresh();
    }

    /**
     * 获取好友关系链管理最后一条系统消息的回调
     *
     * @param message 消息列表
     */
    @Override
    public void onGetFriendshipMessage(List<TIMFriendFutureItem> message) {
//        friendshipManagerPresenter.getFriendshipLastMessage();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Conversation conversation = conversationList.get(info.position);
        if (conversation instanceof NomalConversation){
            menu.add(0, 1, Menu.NONE, getString(R.string.conversation_del));
        }
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        NomalConversation conversation = (NomalConversation) conversationList.get(info.position);
        switch (item.getItemId()) {
            case 1:
                if (conversation != null){
                    if (presenter.delConversation(conversation.getType(), conversation.getIdentify())){
                        conversationList.remove(conversation);
                        adapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    private long getTotalUnreadNum(){
        long num = 0;
        for (Conversation conversation : conversationList){
            num += conversation.getUnreadNum();
        }
        return num;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_back:
                finish();
                break;
        }
    }
}
