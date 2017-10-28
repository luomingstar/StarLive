package com.a99live.zhibo.live.activity;

import android.os.Bundle;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.adapter.GridViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分享界面
 * Created by fuyk on 2016/9/5.
 */
public class ShareActivity extends BaseActivity {

//    @Bind(R.id.gridView)
//    GridView gridView;

    private GridViewAdapter gridViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        // 设置适配器的图片资源
        int[] imageId = new int[] { R.mipmap.share_weichat, R.mipmap.share_weichat_qzone, R.mipmap.share_weibo, R.mipmap.share_qq, R.mipmap.share_qq_qzone};

        // 设置标题
        String[] title = new String[] { "微信好友", "朋友圈", "新浪微博", "QQ好友", "QQ空间"};
        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();

        // 将上述资源转化为list集合
        for (int i = 0; i < title.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", imageId[i]);
            map.put("title", title[i]);

            listitem.add(map);
        }
        gridViewAdapter = new GridViewAdapter(this, listitem);
//
//        gridView = (GridView) this.findViewById(R.id.gridView);
//        gridView.setAdapter(gridViewAdapter);

    }
}
