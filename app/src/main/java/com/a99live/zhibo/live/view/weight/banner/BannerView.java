package com.a99live.zhibo.live.view.weight.banner;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.model.BannerEntity;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.ShowWeb;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by fuyk on 2016/12/1.
 * 自定义Banner无限轮播控件
 */

public class BannerView extends RelativeLayout implements BannerAdapter.ViewPagerOnItemClickListener {

    @Bind(R.id.layout_banner_viewpager)
    ViewPager viewPager;

    @Bind(R.id.layout_banner_points_group)
    LinearLayout points;

    private CompositeSubscription compositeSubscription;

    //默认轮播时间，5s
    private int delayTime = 5;

    private List<ImageView> imageViewList;

    private List<BannerEntity> bannerList;

    //选中显示Indicator
    private int selectRes = R.mipmap.point1;

    //非选中显示Indicator
    private int unSelcetRes = R.mipmap.point2;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_custom_banner, this, true);
        ButterKnife.bind(this);

        imageViewList = new ArrayList<>();
    }

    /**
     * 设置轮播间隔时间（单位秒）
     */
    public BannerView delayTime(int time){
        this.delayTime = time;
        return this;
    }

    /**
     * 设置points颜色
     */
    public void setPointsRes(int selectRes, int unSelcetRes){
        this.selectRes = selectRes;
        this.unSelcetRes = unSelcetRes;
    }

    /**
     * 图片轮播需要传入参数
     */
    public void build(List<BannerEntity> list){

        destory();

        if (list.size() == 0){
            this.setVisibility(GONE);
            return;
        }

        bannerList = new ArrayList<>();
        bannerList.addAll(list);
        final int pointSize;
        pointSize = bannerList.size();

        if (pointSize == 2){
            bannerList.addAll(list);
        }

        //判断是否清空 指示器点
        if (points.getChildCount() != 0){
            points.removeAllViewsInLayout();
        }

        //初始化与个数相同的指示器点
        for (int i = 0; i < pointSize; i++){
            View dot = new View(getContext());
            dot.setBackgroundResource(unSelcetRes);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    UIUtils.dp2px(5),UIUtils.dp2px(5));
            params.leftMargin = 10;
            dot.setLayoutParams(params);
            dot.setEnabled(false);
            points.addView(dot);
        }

        points.getChildAt(0).setBackgroundResource(selectRes);

        for (int i = 0; i < bannerList.size(); i++){
            ImageView mImageView = new ImageView(getContext());

            Glide.with(getContext())
                    .load(bannerList.get(i).img)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.mipmap.lunbotu_720)
                    .into(mImageView);
            imageViewList.add(mImageView);
        }

        //监听图片轮播，改变指示器状态
        viewPager.clearOnPageChangeListeners();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                position = position % pointSize;
                for (int i = 0; i < points.getChildCount(); i++){
                    points.getChildAt(i).setBackgroundResource(unSelcetRes);
                }
                points.getChildAt(position).setBackgroundResource(selectRes);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state){
                    case ViewPager.SCROLL_STATE_IDLE:
                        if (isStopScroll){
                            startScroll();
                        }
                        break;
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        stopScroll();
                        compositeSubscription.unsubscribe();
                        break;
                }
            }
        });

        ViewGroup.LayoutParams layoutParams = viewPager.getLayoutParams();
        layoutParams.height = UIUtils.getScreenHeight()/6;
        viewPager.setLayoutParams(layoutParams);

        BannerAdapter bannerAdapter = new BannerAdapter(imageViewList);
        viewPager.setAdapter(bannerAdapter);
        bannerAdapter.notifyDataSetChanged();
        bannerAdapter.setmViewPagerOnItemClickListener(this);

        //图片开始轮播
        startScroll();

    }

    private boolean isStopScroll = false;

    /**
     * 图片开始轮播
     */
    private void startScroll(){
        compositeSubscription = new CompositeSubscription();
        isStopScroll = false;
        Subscription subscription = Observable.timer(delayTime, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {

                        if (isStopScroll){
                            return;
                        }

                        isStopScroll = true;
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

                    }
                });
        compositeSubscription.add(subscription);
    }

    /**
     * 图片停止轮播
     */
    private void stopScroll(){
        isStopScroll = true;
    }

    public void destory(){
        if (compositeSubscription != null){
            compositeSubscription.unsubscribe();
        }
    }

    @Override
    public void onItemClick(int position) {
        if (position == 0){
            position = bannerList.size() - 1;
        }else {
            position -= 1;
        }

//        ShowWeb.goShowWeb((Activity)getContext(),
//                bannerList.get(position).link,
//                bannerList.get(position).title);

    }
}
