package com.a99live.zhibo.live.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.adapter.base.BaseListAdapter;
import com.a99live.zhibo.live.model.UMengInfo;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.ShareProtocol;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.view.MyDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 我的直播adapter
 * Created by fuyk on 2016/8/31.
 */
public class MyLiveAdapter extends BaseListAdapter {

    private DelMyLiveClickListener mListener;
    public MyLiveAdapter(Context context, List<Map<String,String>> data) {
        super(context, data);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null){
            convertView = View.inflate(mContext, R.layout.item_my_live, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(R.id.tag_holder, holder);
        }else {
            convertView.setPadding(0, 0, 0, 0);
            holder = (ViewHolder) convertView.getTag(R.id.tag_holder);
        }

        final Map<String ,String> item = getItem(position);

        //标题
        holder.tv_title.setText(item.get("room_title"));
        //时间
        holder.tv_time.setText(item.get("format_time"));

        final String title = item.get("room_title");

        final String bg_img_url = item.get("bg_img_url");
        //封面
        Glide.with(mContext)
                .load(bg_img_url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .placeholder(R.mipmap.list_cover)
                .crossFade()
                .into(holder.iv_cover);
        //类型 直播或录播
        if ("0".equals(item.get("is_live"))){
            holder.iv_type.setImageResource(R.mipmap.type_state_huifang);
        }else {
            holder.iv_type.setImageResource(R.mipmap.type_state_live);
        }

        /**分享*/
        holder.iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UMengInfo.getUrlTool((Activity)mContext,SPUtils.getString(SPUtils.USER_NAME),
                        umShareListener,SPUtils.getString(SPUtils.USER_ID),false,title,bg_img_url,null);
//                new ShareAction((Activity)mContext).setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.SINA)
//                        .withTitle(UMengInfo.title)
//                        .withText(UMengInfo.getText(SPUtils.getString(SPUtils.USER_NAME)))
//                        .withMedia(new UMImage(LiveZhiBoApplication.getApp(), R.mipmap.ic_launcher))
//                        .withTargetUrl(UMengInfo.getH5Center(SPUtils.getString(SPUtils.USER_ID)))
//                        .setCallback(umShareListener)
//                        .open();
            }
        });

        /**删除*/
        holder.iv_remove.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(mListener !=null){
                    if(mContext instanceof Activity) {
                        new MyDialog((Activity) mContext)
                                .setMessage(R.string.if_del)
                                .setLeftButtonState(R.string.cancel, null)
                                .setRightButtonState(R.string.enter, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mListener.onDelMyLiveClick(item.get("id"), position);
                                    }
                                }).show();
                    }else {
                        mListener.onDelMyLiveClick(item.get("id"), position);
                    }
                }
            }
        });
        return convertView;
    }


    public void setListener(DelMyLiveClickListener mListener) {
        this.mListener = mListener;
    }

    public interface DelMyLiveClickListener {
            void onDelMyLiveClick(String delId , int position);
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            Toast.makeText(mContext, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
            shareCallBack(SPUtils.getString(SPUtils.USER_ID));
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable throwable) {
//            Toast.makeText(mContext, platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            Toast.makeText(mContext, platform + " 分享取消啦", Toast.LENGTH_SHORT).show();
        }
    };

    /**分享回调接口*/
    private void shareCallBack(String uid){
        LiveRequestParams params = new LiveRequestParams();
        params.put("uid", uid);

        ShareProtocol shareProtocol = new ShareProtocol();
        shareProtocol.ShareCallback(params)
                .observeOn(AndroidSchedulers.mainThread())
//                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("livelog","sharecallback");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    /**
     * 列表数据
     */
    static class ViewHolder {
        @Bind(R.id.tv_title)
        TextView tv_title;

        @Bind(R.id.tv_time)
        TextView tv_time;

        @Bind(R.id.iv_cover)
        ImageView iv_cover;

        @Bind(R.id.iv_type)
        ImageView iv_type;

        @Bind(R.id.iv_share)
        ImageView iv_share;

        @Bind(R.id.iv_remove)
        ImageView iv_remove;

        public ViewHolder(View v) {
            ButterKnife.bind(this, v);
        }
    }

}
