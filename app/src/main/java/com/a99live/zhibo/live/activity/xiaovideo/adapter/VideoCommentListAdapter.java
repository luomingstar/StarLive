package com.a99live.zhibo.live.activity.xiaovideo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.adapter.base.BaseListAdapter;
import com.a99live.zhibo.live.view.weight.NewCircleImageView;
import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

/**
 * Created by JJGCW on 2017/4/12.
 */

public class VideoCommentListAdapter extends BaseListAdapter {


    public VideoCommentListAdapter(Context c, List<Map<String, String>> data) {
        super(c, data);
    }

    /**
     * content = "\U963f\U65af\U8fbe";
     guan = 1;
     id = 5;
     "nick_name" = "\U7a7f\U8fc7\U4f60\U7684\U9ed1\U53d1\U6211\U7684\U624b";
     number99 = 10000;
     ucode = 936;
     "user_head" = "http://99live-10063116.image.myqcloud.com/c33861e0e972d59891febca1d4c5e19b?imageMogr2/thumbnail/x200";
     "user_id" = 1;
     verify = 0;
     "video_id" = 4;
     create_time
     * @param i
     * @param view
     * @param viewGroup
     * @return
     */
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null){
            view = View.inflate(mContext, R.layout.xvideo_comment_list_item,null);
            viewHolder = new ViewHolder(view);
            view.setTag(R.id.tag_holder, viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag(R.id.tag_holder);
        }
        Map<String, String> item = getItem(i);
        String nick_name = item.get("nick_name");
        String user_head = item.get("user_head");
        String content = item.get("content");
        String create_time = item.get("create_time");

        viewHolder.nick_name.setText(nick_name);
        viewHolder.centent.setText(content);
        viewHolder.time.setText(create_time);
        Glide.with(mContext).load(user_head).dontAnimate().into(viewHolder.head);

        return view;
    }


    static class ViewHolder{

        private final NewCircleImageView head;
        private final TextView nick_name;
        private final TextView time;
        private final TextView centent;

        public ViewHolder(View view){
            head = (NewCircleImageView) view.findViewById(R.id.head);
            nick_name = (TextView) view.findViewById(R.id.nick_name);
            time = (TextView) view.findViewById(R.id.time);
            centent = (TextView) view.findViewById(R.id.centent);
        }
    }
}
