package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.model.TCSimpleUserInfo;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.TCUtils;

import java.util.LinkedList;

/**
 * Created by teckjiang on 2016/8/21.
 * 直播头像列表Adapter
 */
public class TCUserAvatarListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    LinkedList<TCSimpleUserInfo> mUserAvatarList;
    Context mContext;
    private Handler mHandler;
    //主播id
    private String mPusherId;
    //最大容纳量
    private final static int TOP_STORGE_MEMBER = 50;


    public TCUserAvatarListAdapter(Context context, String pusherId, Handler handler) {
        this.mContext = context;
        this.mPusherId = pusherId;
        this.mUserAvatarList = new LinkedList<>();
        this.mHandler = handler;
    }

    /**
     * 添加用户信息
     * @param userInfo 用户基本信息
     * @return 存在重复或头像为主播则返回false
     */
    public boolean addItem(TCSimpleUserInfo userInfo) {

        //去除主播头像
        if (userInfo.userid == null)
            return false;
        if(userInfo.userid.equals(mPusherId))
            return false;

        //去重操作
        for (TCSimpleUserInfo tcSimpleUserInfo : mUserAvatarList) {
            if(tcSimpleUserInfo.userid.equals(userInfo.userid))
                return false;
        }

        //超出时删除末尾项
        if(mUserAvatarList.size() > TOP_STORGE_MEMBER) {
            mUserAvatarList.remove(mUserAvatarList.size()-1);
            notifyItemRemoved(mUserAvatarList.size()-1);
//            notifyDataSetChanged();
        }

        //始终显示新加入item为第一位
        mUserAvatarList.add(0,userInfo);
        notifyItemInserted(0);

        return true;
    }

    public void removeItem(String userId) {
        TCSimpleUserInfo tempUserInfo = null;

        for(TCSimpleUserInfo userInfo : mUserAvatarList)
            if(userInfo.userid.equals(userId))
                tempUserInfo = userInfo;


        if(null != tempUserInfo) {
            mUserAvatarList.remove(tempUserInfo);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.item_user_avatar, parent, false);

        final AvatarViewHolder avatarViewHolder = new AvatarViewHolder(view);
        avatarViewHolder.ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mHandler != null){
                    String id = mUserAvatarList.get(avatarViewHolder.getAdapterPosition()).userid;
                    if (id != null && !TextUtils.isEmpty(id)) {
                        Message message = mHandler.obtainMessage(TCConstants.OPEN_USER_BY_IDENTITY);
                        message.obj = id;
                        message.sendToTarget();
                    }
                }
//                TCSimpleUserInfo userInfo = mUserAvatarList.get(avatarViewHolder.getAdapterPosition());
//                Toast.makeText(mContext.getApplicationContext(),"当前点击用户： " + userInfo.userid, Toast.LENGTH_SHORT).show();
            }
        });

        return avatarViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (mUserAvatarList.size()>position) {
            TCUtils.showPicWithUrl(mContext, ((AvatarViewHolder) holder).ivAvatar, mUserAvatarList.get(position).headpic,
                    R.mipmap.head);
//            ((AvatarViewHolder) holder).ivAvatar.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (mHandler != null){
//                        String id = mUserAvatarList.get(position).userid;
//                        if (id != null && !TextUtils.isEmpty(id)) {
//                            Message message = mHandler.obtainMessage(TCConstants.OPEN_USER_BY_IDENTITY);
//                            message.obj = id;
//                            message.sendToTarget();
//                        }
//                    }
//                }
//            });
        }

    }

    @Override
    public int getItemCount() {
        return mUserAvatarList != null? mUserAvatarList.size(): 0;
    }

    private class AvatarViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAvatar;

        public AvatarViewHolder(View itemView) {
            super(itemView);

            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
        }
    }
}
