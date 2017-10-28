package com.a99live.zhibo.live.activity.xiaovideo.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.xiaovideo.adapter.VideoCommentListAdapter;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.VideoProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.list.XListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by JJGCW on 2017/4/12.
 */

public class CommentList extends RelativeLayout implements XListView.IXListViewListener, AdapterView.OnItemClickListener {
    private Context mContext;
    private XListView list_view;
    private int MAXID = 0;
    private int MINID = Integer.MAX_VALUE;
    private int LISTNUM = 10;
    private String id = "";
    private List<Map<String, String>> data;
    private VideoCommentListAdapter adapter;

    public CommentList(Context context) {
        this(context,null);
    }

    public CommentList(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CommentList(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View view = LayoutInflater.from(mContext).inflate(R.layout.xvideo_comment_list,this,true);
        list_view = (XListView) findViewById(R.id.list_view);
    }

    public void setData(String id){
        this.id = id;
        list_view.setPullRefreshEnable(true);
        list_view.setXListViewListener(this);

        list_view.setPullLoadEnable(true);
        list_view.setOnItemClickListener(this);
        MAXID = 0;
        MINID = Integer.MAX_VALUE;
        data = new ArrayList<>();
        adapter = new VideoCommentListAdapter(mContext,data);
        list_view.setAdapter(adapter);
        getCList(true);
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
     * @param s
     * @param video_id
     */
    public void sendOk(String s, String video_id){
//        Map<String,String> map = new HashMap<>();
//        map.put("nick_name", SPUtils.getString(SPUtils.USER_NAME));
//        map.put("user_head",SPUtils.getString(SPUtils.USER_AVATAR));
//        map.put("content",s);
//        map.put("create_time","刚刚");
//        map.put("video_id",video_id);
//        if (adapter != null){
//            adapter.getData().add(0,map);
//            adapter.notifyDataSetChanged();
//            list_view.setSelection(0);
//        }
        getCList(true);
    }

    private void getCList(final boolean isRefresh){
        int maxId = 0;
        int minId = 0;
        if (isRefresh){
            minId = 0;
            maxId = MAXID;
        }else{
            minId = MINID;
            maxId = 0;
        }
        VideoProtocol videoProtocol = new VideoProtocol();
        LiveRequestParams params = new LiveRequestParams();
        params.put("o_maxid",maxId);
        params.put("o_minid",minId);
        params.put("o_number",LISTNUM);
        params.put("vid",id);
        videoProtocol.getCList(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        initData(s,isRefresh);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        list_view.onRefreshFinish();
                    }
                });
    }

    private void initData(String data,boolean isRefresh){
        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(data);
        if (listMapByJson.size()>0){
            Map<String, String> map = listMapByJson.get(0);
            if ("0".equals(map.get("code"))){
                String dd = map.get("data");
                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(dd);
                if (dataList.size()>0){//有数据
                    if (isRefresh){
                        if (MAXID == 0 && MINID == Integer.MAX_VALUE){//第一次加载

                        } else {
//                            adapter.getData().clear();
//                            MINID = Integer.MAX_VALUE;
//                            MAXID = 0;
                            list_view.onRefreshFinish();
                            list_view.updateRefreshTime();
                        }
                    }else{

                    }

                    for (int i=0;i<dataList.size();i++){
                        Map<String, String> map1 = dataList.get(i);
                        String id = map1.get("id");
                        int idd = Integer.parseInt(id);
                        MAXID = MAXID < idd ? idd: MAXID;
                        MINID = MINID > idd ? idd : MINID;
                    }
                    if (isRefresh) {
                        adapter.getData().addAll(0,dataList);
                    }else{
                        adapter.getData().addAll(dataList);
                    }
                    adapter.notifyDataSetChanged();

                }else{//无数据

                    list_view.setAutoLoadEnable(false);
                    list_view.setPullLoadEnable(false);

                }
                list_view.onRefreshFinish();
            }else{
                list_view.onRefreshFinish();
            }
        }else{
            UIUtils.showToast(data);
            list_view.onRefreshFinish();
        }

    }


    @Override
    public void onRefresh() {
        getCList(true);
    }

    @Override
    public void onLoadMore() {
        getCList(false);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
