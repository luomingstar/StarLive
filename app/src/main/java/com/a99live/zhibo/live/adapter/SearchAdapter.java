package com.a99live.zhibo.live.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.OtherHomeActivity;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.NewCircleImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/12/7.
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private List<Map<String,String>> mSearchList;

    UserProtocol userProtocol = new UserProtocol();

    public SearchAdapter(Context context, List<Map<String,String>> data) {
        this.context = context;
        this.mSearchList = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
        final SearchAdapter.SearchViewHolder searchViewHolder = new SearchAdapter.SearchViewHolder(view);
        return searchViewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Map<String, String> searchMap = mSearchList.get(position);

        //点击事件
        ((SearchViewHolder)holder).rl_layout_search_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OtherHomeActivity.goOtherHomeActivity(context, searchMap.get("ucode"));
            }
        });

        //头像
        Glide.with(context)
                .load(searchMap.get("avatar"))
                .centerCrop()
                .placeholder(R.mipmap.head)
                .dontAnimate()
                .into(((SearchViewHolder) holder).iv_search_head);
        //昵称
        ((SearchViewHolder)holder).tv_search_name.setText(searchMap.get("nickname"));
        //性别
        if ("1".equals(searchMap.get("gender"))){
            ((SearchViewHolder)holder).iv_search_sex.setImageResource(R.drawable.card_boy);
        }else {
            ((SearchViewHolder)holder).iv_search_sex.setImageResource(R.drawable.card_girl);
        }
        //签名
        if (TextUtils.isEmpty(searchMap.get("sign"))){
            ((SearchViewHolder)holder).tv_search_sigle.setText(R.string.sign_normal);
        }else {
            ((SearchViewHolder)holder).tv_search_sigle.setText(searchMap.get("sign"));
        }
//        ((SearchViewHolder)holder).tv_search_sigle.setText(searchMap.get("sign"));
        //是否关注
        if ("Y".equals(searchMap.get("is_follow"))){
            ((SearchViewHolder)holder).iv_search_attention.setImageResource(R.drawable.search_follow);
            ((SearchViewHolder)holder).iv_search_attention.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unFollow(position, searchMap.get("user_id"));
                }
            });

        }else if ("N".equals(searchMap.get("is_follow"))){
            ((SearchViewHolder)holder).iv_search_attention.setImageResource(R.drawable.search_unfollow);
            ((SearchViewHolder)holder).iv_search_attention.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    follow(position, searchMap.get("user_id"));
                }
            });
        }
    }

    /**关注接口*/
    private void follow(final int position, String user_id){
        LiveRequestParams params = new LiveRequestParams();
        params.put("fid", user_id);

        userProtocol.getFollow(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                Log.d("livelog", "关注成功");
//                                iv_search_attention.setImageResource(R.drawable.search_follow);
                                mSearchList.get(position).put("is_follow","Y");
                                notifyDataSetChanged();
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
    private void unFollow(final int position, String user_id){
        LiveRequestParams params = new LiveRequestParams();
        params.put("fid", user_id);

        userProtocol.getUnFollow(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                Log.d("livelog", "取消关注成功");
//                                iv_search_attention.setImageResource(R.drawable.search_unfollow);
                                mSearchList.get(position).put("is_follow","N");
                                notifyDataSetChanged();
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

    @Override
    public int getItemCount() {
        return mSearchList != null? mSearchList.size() : 0;
    }

    public class SearchViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.rl_layout_search_item)
        RelativeLayout rl_layout_search_item;

        @Bind(R.id.iv_search_head)
        NewCircleImageView iv_search_head;

        @Bind(R.id.tv_search_name)
        TextView tv_search_name;

        @Bind(R.id.iv_search_sex)
        ImageView iv_search_sex;

        @Bind(R.id.tv_search_sigle)
        TextView tv_search_sigle;

        @Bind(R.id.iv_search_attention)
        ImageView iv_search_attention;


        public SearchViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
