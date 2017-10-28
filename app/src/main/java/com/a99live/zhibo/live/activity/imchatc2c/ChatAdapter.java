package com.a99live.zhibo.live.activity.imchatc2c;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.model.FriendProfile;
import com.a99live.zhibo.live.model.FriendshipInfo;
import com.a99live.zhibo.live.model.Message;
import com.a99live.zhibo.live.utils.SPUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

/**
 * 聊天界面adapter
 */
public class ChatAdapter extends ArrayAdapter<Message> {

    private final String TAG = "ChatAdapter";

    private int resourceId;
    private View view;
    private ViewHolder viewHolder;
    private String avatar;

    public void setAvatar(String avatar){
        if (TextUtils.isEmpty(this.avatar) && !TextUtils.isEmpty(avatar)) {
            this.avatar = avatar;
            notifyDataSetChanged();
        }
    }

    /**
     * Constructor
     *
     * @param context  The current context.
     * @param resource The resource ID for a layout file containing a TextView to use when
     *                 instantiating views.
     * @param objects  The objects to represent in the ListView.
     */
    public ChatAdapter(Context context, int resource, List<Message> objects,String avatar) {
        super(context, resource, objects);
        resourceId = resource;
        this.avatar = avatar;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null){
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }else{
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftMessage = (RelativeLayout) view.findViewById(R.id.leftMessage);
            viewHolder.rightMessage = (RelativeLayout) view.findViewById(R.id.rightMessage);
            viewHolder.leftPanel = (RelativeLayout) view.findViewById(R.id.leftPanel);
            viewHolder.rightPanel = (RelativeLayout) view.findViewById(R.id.rightPanel);
            viewHolder.sending = (ProgressBar) view.findViewById(R.id.sending);
            viewHolder.error = (ImageView) view.findViewById(R.id.sendError);
            viewHolder.sender = (TextView) view.findViewById(R.id.sender);
            viewHolder.rightDesc = (TextView) view.findViewById(R.id.rightDesc);
            viewHolder.systemMessage = (TextView) view.findViewById(R.id.systemMessage);
            viewHolder.leftAvatar = (CircleImageView) view.findViewById(R.id.leftAvatar);
            viewHolder.rightAvatar = (CircleImageView) view.findViewById(R.id.rightAvatar);
            view.setTag(viewHolder);
        }
        if (position < getCount()){
            final Message data = getItem(position);
//            String sender = data.getSender();
//            Log.d("livelog",sender);
//            TIMMessage message = data.getMessage();
//            Log.d("livelog",message.getSenderProfile()== null ? "hehe" : message.getSenderProfile().getFaceUrl());
            data.showMessage(viewHolder, getContext());
            boolean self = data.isSelf();
            if (self){
                //我的头像
                String myAvatar = SPUtils.getString(SPUtils.USER_AVATAR);
                Glide.with(getContext())
                        .load(myAvatar)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .placeholder(R.mipmap.head)
                        .dontAnimate()
//                .crossFade()
//                .transform(new GlideCircleTransform(mContext))
                        .into(viewHolder.rightAvatar);
            }else{
                String id = data.getSender();//getMessage();
                if (TextUtils.isEmpty(avatar)){
                    FriendProfile profile = FriendshipInfo.getInstance().getProfile(id);
                    avatar = profile == null ? "" : profile.getAvatarUrl();
                }
//                TIMMessage message = data.getMessage();
//                TIMUserProfile senderProfile = message.getSenderProfile();
//                String sfsd = senderProfile.getFaceUrl();
//                UIUtils.showToast(sfsd+"__"+senderProfile.getNickName());
                Glide.with(getContext())
                        .load(avatar)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .placeholder(R.mipmap.head)
                        .dontAnimate()
//                .crossFade()
//                .transform(new GlideCircleTransform(mContext))
                        .into(viewHolder.leftAvatar);
            }



        }


        return view;
    }


    public class ViewHolder{
        public RelativeLayout leftMessage;
        public RelativeLayout rightMessage;
        public RelativeLayout leftPanel;
        public RelativeLayout rightPanel;
        public ProgressBar sending;
        public ImageView error;
        public TextView sender;
        public TextView systemMessage;
        public TextView rightDesc;
        public CircleImageView leftAvatar;
        public CircleImageView rightAvatar;
    }
}
