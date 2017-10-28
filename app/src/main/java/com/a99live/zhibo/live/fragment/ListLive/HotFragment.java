package com.a99live.zhibo.live.fragment.ListLive;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.a99live.zhibo.live.LiveZhiBoApplication;
import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.LivePlayerActivity;
import com.a99live.zhibo.live.activity.LoginActivity;
import com.a99live.zhibo.live.activity.OtherHomeActivity;
import com.a99live.zhibo.live.activity.VoderActivity;
import com.a99live.zhibo.live.activity.xiaovideo.XiaoVideoViewPage;
import com.a99live.zhibo.live.adapter.FragmentAdapter;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.AppProtocol;
import com.a99live.zhibo.live.protocol.ReserveProtocol;
import com.a99live.zhibo.live.protocol.RoomProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.LoginManager;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.utils.YMClick;
import com.a99live.zhibo.live.view.PageStateLayout;
import com.a99live.zhibo.live.view.ShowWeb;
import com.a99live.zhibo.live.view.list.XListView;
import com.a99live.zhibo.live.view.weight.NoticeView;
import com.a99live.zhibo.live.view.weight.banner.GalleryViewPager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 首页热门界面
 * Created by fuyk on 2016/9/9.
 */
public class HotFragment extends BaseFragment implements XListView.IXListViewListener, AdapterView.OnItemClickListener, View.OnTouchListener, ViewPager.OnPageChangeListener {

    private AppProtocol appProtocol;
    private RoomProtocol roomProtocol;
//    private ImProtocol imProtocol;
    private FragmentAdapter fragmentAdapter;
//    private LoginHelper loginPresenter;

    @Bind(R.id.list_view)
    XListView hotListView;

    private int mOminid = 0;
    private int mHminid = 0;

//    LinearLayout linearLayout;
//    BannerView mBannerNet;
    private LinearLayout vpView;
    private LinearLayout noticeView;
    private LinearLayout linearLayout;
    private NoticeView notice;

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("HotFragment");
//        loginPresenter.setIMLoginCallback(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HotFragment");
//        loginPresenter.removeIMLoginCallback();
    }

    @Override
    protected View initContentView() {
        View view = View.inflate(getActivity(), R.layout.fragment_hot, null);
        ButterKnife.bind(this, view);

        //广告轮播图
        linearLayout = new LinearLayout(getActivity());
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        lp.height = UIUtils.getScreenHeight()/6 + UIUtils.getDimen(R.dimen.dp_10);
        linearLayout.setLayoutParams(lp);
        linearLayout.setPadding(0,0,0, UIUtils.getDimen(R.dimen.dp_8));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setVisibility(View.GONE);
        hotListView.addHeaderView(linearLayout);

        vpView = new LinearLayout(getActivity());
        vpView.setVisibility(View.GONE);
        linearLayout.addView(vpView);

        noticeView = new LinearLayout(getActivity());
        noticeView.setVisibility(View.GONE);
        linearLayout.addView(noticeView);


//        View topView = View.inflate(getActivity(), R.layout.layout_banner, null);
//        linearLayout.addView(topView);
//        mBannerNet = (BannerView) topView.findViewById(R.id.banner);

        hotListView.setPullRefreshEnable(true);
        hotListView.setXListViewListener(this);

        hotListView.setPullLoadEnable(true);
        hotListView.setOnItemClickListener(this);
        isPrepared = true;
        initContentData();
        return view;
    }

    @Override
    protected void initContentData() {
        if (!isPrepared || !isVisible){
            return;
        }
        isPrepared = false;
        appProtocol = new AppProtocol();
        roomProtocol = new RoomProtocol();
//        imProtocol = new ImProtocol();
//        loginPresenter = LoginHelper.getInstance();
        onRefresh();
        getNetBanner();
//        getOrder();
    }

    /**
     * 获取预约数据
     */
    private void getOrder(){
        LiveRequestParams params = new LiveRequestParams();
        ReserveProtocol protocol = new ReserveProtocol();
        protocol.getOrder(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                String data = map.get("data");
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                                if(dataList.size()>0){
                                    setOrder(dataList);
                                    return;
                                }
                            }else{

                            }
                        }else{

                        }
                        if (noticeView != null){
                            noticeView.setVisibility(View.GONE);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (noticeView != null){
                            noticeView.setVisibility(View.GONE);
                        }
                    }
                });
    }

//    int m = 0;
    private void setOrder(ArrayList<Map<String, String>> dataList){
//        if (m%2==0){
//            while (dataList.size()>1){
//                dataList.remove(0);
//            }
//        }
//        m++;
        linearLayout.setVisibility(View.VISIBLE);
        if (notice == null){
            noticeView.removeAllViews();
            notice = new NoticeView(getActivity());
            noticeView.addView(notice,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        notice.setData(dataList);
        noticeView.setVisibility(View.VISIBLE);
    }

    /**获取轮播图图片*/
    private void getNetBanner() {
        LiveRequestParams params = new LiveRequestParams();

        appProtocol.getBanner(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> dataMap = listMapByJson.get(0);
                            if ("0".equals(dataMap.get("code"))){
                                String data = dataMap.get("data");
                                String append = dataMap.get("append");
                                initTop(data,append);
                            }else {

                            }
                        }else {

                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

//    private List<BannerEntity> banners = new ArrayList<>();
    /**加载广告图*/
    private void initTop(String data,String append) {
        ArrayList<Map<String, String>> banner = JsonUtil.getListMapByJson(data);
        if (banner.size() > 0){
            ArrayList<Map<String, String>> listMapByJson1 = JsonUtil.getListMapByJson(append);
            Map<String, String> map = listMapByJson1.get(0);
            String uri = map.get("uri");
            setGallery(banner,uri);
//            banners.clear();
//            for (int i = 0; i< banner.size(); i++){
//                Map<String, String> bannerData = banner.get(i);
//                BannerEntity bannerEntity = new BannerEntity();
//                bannerEntity.img = bannerData.get("full_image_path");
//                bannerEntity.link = bannerData.get("address");
//                banners.add(bannerEntity);
//            }
//
//            mBannerNet.delayTime(5).build(banners);
//            linearLayout.setVisibility(View.VISIBLE);

        }else{
            vpView.setVisibility(View.GONE);
        }
    }

    /**
     * 获取列表数据
     */
    private void getHotList() {
        LiveRequestParams params = new LiveRequestParams();
//        params.put("o_minid", mOminid);
//        params.put("h_minid", mHminid);

        roomProtocol.getLiveListNew(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {

                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            String code = map.get("code");
                            if ("0".equals(code)){
                                String data = map.get("data");
                                initPage(data);
                                return;
                            }else if ("20000006".equals(code)) {
                                Toast.makeText(LiveZhiBoApplication.getApp(), "房间未找到，请刷新列表", Toast.LENGTH_SHORT).show();
                            } else if ("111111110".equals(code)  || "11111113".equals(code) ) {
                                LoginManager.clearUser();
                                goLoginActivity();
                            } else if ("11111122".equals(code)) {
                                UIUtils.showToast("在别处登录，请重新登录");
                                LoginManager.clearUser();
                                goLoginActivity();
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                            hotListView.onRefreshFinish();
                        }else{
                            UIUtils.showToast(s);
                            hotListView.onRefreshFinish();
                        }
                        if (fragmentAdapter != null && fragmentAdapter.getData() != null && fragmentAdapter.getData().size() >0){
                            mOminid = 0;
                            mHminid = 0;
                        }else {
                            showPage(PageStateLayout.STATE_ERROR);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
//                        JsonParser.fromError(throwable, getActivity());
                        if (fragmentAdapter != null && fragmentAdapter.getData() != null && fragmentAdapter.getData().size() >0){
                            UIUtils.showToast(R.string.net_error);
                            hotListView.onRefreshFinish();
                        }else {
                            showPage(PageStateLayout.STATE_ERROR);
                        }
                        mOminid = 0;
                        mHminid = 0;
                    }
                });
    }

    /**
     * 初始化界面
     */
    private void initPage(String data) {
        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(data);
        if (listMapByJson.size() > 0){//有数据
            showPage(PageStateLayout.STATE_SUCCEED);
            if (mHminid == 0 && mOminid == 0 && fragmentAdapter != null) {
                //下拉刷新
                fragmentAdapter.getData().clear();
                hotListView.onRefreshFinish();
                hotListView.updateRefreshTime();
            }
            //加載更多
            if (fragmentAdapter == null) {
                fragmentAdapter = new FragmentAdapter(getActivity(), listMapByJson, YMClick.HOTPAGE_HEAD_IMG);
                hotListView.setAdapter(fragmentAdapter);
            } else {
                fragmentAdapter.getData().addAll(listMapByJson);
                fragmentAdapter.notifyDataSetChanged();
            }

        }else{//没数据
            if (mHminid == 0 && mOminid == 0)
                showPage(PageStateLayout.STATE_EMPTY);

            hotListView.setAutoLoadEnable(false);
            hotListView.setPullLoadEnable(false);

        }
        hotListView.onRefreshFinish();

    }

    /**
     * 更新界面
     */
    @Override
    public void onRefresh() {
        mOminid = 0;
        mHminid = 0;
//        if(fragmentAdapter!=null) {
//            fragmentAdapter.initId();
//        }
        hotListView.setAutoLoadEnable(false);
        hotListView.setPullLoadEnable(false);
        getHotList();
        getOrder();
    }

    @Override
    public void onLoadMore() {
//        if (mHminid == fragmentAdapter.getH_minid() && mOminid == fragmentAdapter.getO_minid()) {
//            return;
//        }
//        mHminid = fragmentAdapter.getH_minid();
//        mOminid = fragmentAdapter.getO_minid();
//        getHotList();
    }

    /**
     * 点击Item
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Map<String ,String> item = fragmentAdapter.getItem(position);
        if (item.get("type").equals("Z")) {
            Intent intent = new Intent(getActivity(), LivePlayerActivity.class);
            intent.putExtra("room_id", item.get("room_id"));
            intent.putExtra("ucode", item.get("ucode"));
//            intent.putExtra(TCConstants.COVER_PIC, item.get("bg_img_url"));
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
        } else if (item.get("type").equals("L")) {
            enterPlayRoom(item.get("id"), item.get("user_head"));
        }
    }

    /**
     * 请求IM签名
     */
//    private void getImSig(){
//        LiveRequestParams params = new LiveRequestParams();
//
//        imProtocol.getIMSign(params)
//                .observeOn(AndroidSchedulers.mainThread())
//                .compose(this.<String>bindToLifecycle())
//                .subscribe(new Action1<String>() {
//                    @Override
//                    public void call(String s) {
//
//                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
//                        if (listMapByJson.size()>0){
//                            Map<String, String> map = listMapByJson.get(0);
//                            if ("0".equals(map.get("code"))){
//                                String data = map.get("data");
//                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
//                                if (dataList.size()>0){
//                                    Map<String, String> dataMap = dataList.get(0);
//                                    goLoginIM(dataMap);
//                                }
//
//                            }else{
//                                Log.d("liveLog",map.get("msg"));
//                            }
//                        }else{
//
//                        }
//
//
//                    }
//                }, new Action1<Throwable>() {
//                    @Override
//                    public void call(Throwable throwable) {
//                        Log.e("livelog","error-");
//                    }
//                });
//    }

//    int number = 0;
//
//    private void goLoginIM(Map<String,String> iMsigInfo) {
//        loginPresenter.imLogin(iMsigInfo.get("uid"), iMsigInfo.get("sig"));
//        number = number++;
//    }

//    @Override
//    public void onIMLoginSuccess() {
//        Log.d("livelog_hot", "IM登录成功");
//    }
//
//    @Override
//    public void onIMLoginError(int code, String msg) {
//        Log.d("livelog_hot", "IM登录失败");
//        if (number <= 2) {
//            getImSig();
//        }
//    }


    /**
     * 进入登录页
     */
    private void goLoginActivity() {
        getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
    }


    /**
     * 进入录播接口
     */
    private void enterPlayRoom(String lu_id, final String avator) {

        goPlayRoom(lu_id,avator);

    }

    /**
     * 进入录播房间
     *
     * @param lu_id
     */
    private void goPlayRoom(String lu_id, String avator) {
        Intent intent = new Intent(getActivity(), VoderActivity.class);
        intent.putExtra("lu_id", lu_id);
        intent.putExtra(TCConstants.COVER_PIC, avator);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

//    @Override
//    public void onItemClick(int position) {
//        if (banner != null && banner.size()>0){
//            Map<String, String> map = banner.get(position);
//            String address = map.get("address");
//            ShowWeb.goShowWeb(getActivity(),address,"");
//        }
//    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }



    /**
     * 设置轮播集
     *
     */
    private void setGallery(final ArrayList<Map<String, String>> sliderList, final String uri) {
        linearLayout.setVisibility(View.VISIBLE);
        vpView.removeAllViews();
        View view = View.inflate(getActivity(),R.layout.bannerbystar,null);
        vpView.addView(view);
        vpView.setVisibility(View.VISIBLE);
        RelativeLayout rr = (RelativeLayout) view.findViewById(R.id.gallery_layout);
        ViewGroup.LayoutParams layoutParams = rr.getLayoutParams();
        layoutParams.height = UIUtils.getScreenHeight()/6;
        rr.setLayoutParams(layoutParams);
        final ImageView[] navImg = new ImageView[sliderList.size()];
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.ll_gallery);
        layout.removeAllViews();
        for (int i = 0; i < navImg.length; i++) {
            navImg[i] = new ImageView(getActivity());
            navImg[i].setPadding(UIUtils.dp2px(2.5f), 0,
                    UIUtils.dp2px(2.5f), 0);
            navImg[i].setBackgroundColor(Color.TRANSPARENT);
            navImg[i].setImageResource(R.mipmap.point2);
            if (navImg.length > 1) {
                layout.addView(navImg[i]);
                navImg[i].getLayoutParams().width = UIUtils.dp2px(10f);
                navImg[i].getLayoutParams().height = UIUtils.dp2px(5f);
            }
        }
        navImg[0].setImageResource(R.mipmap.point1);
        GalleryViewPager gallery = (GalleryViewPager) view.findViewById(R.id.gallery);
        ArrayList<View> views = new ArrayList<View>();
        for (int i = 0; i < sliderList.size(); i++) {
            Map<String, String> map = sliderList.get(i);
            views.add(getGalleryView(map));
        }
        if (sliderList.size() > 1) {
            views.add(0, getGalleryView(sliderList.get(sliderList.size() - 1)));
            views.add(getGalleryView(sliderList.get(0)));
        }
        gallery.init(views, 5000, new GalleryViewPager.Helper() {

            @Override
            public void onChange(int position) {
                for (int i = 0; i < navImg.length; i++) {
                    if (i == position) {
                        navImg[i].setImageResource(R.mipmap.point1);
                    } else
                        navImg[i].setImageResource(R.mipmap.point2);
                }
            }

            @Override
            public void onClick(View view, int position) {
                String type = sliderList.get(position).get("type");
                if ("0".equals(type)){
                    Map<String, String> map = sliderList.get(position);
                    String address = map.get("address");
                    if (!TextUtils.isEmpty(address)) {
                        ShowWeb.goShowWeb(getActivity(), address, map.get("title"), map.get("desc"),map.get("is_login"),uri);
                    }
                }else if ("1".equals(type)){
                    String ucode = sliderList.get(position).get("address");
                    if (!TextUtils.isEmpty(ucode)) {
                        OtherHomeActivity.goOtherHomeActivity(getActivity(),ucode );
                    }
                }else if("3".equals(type)){
                    XiaoVideoViewPage.goXiaoVideoViewPage(getActivity());
                }
            }
        });
    }

    // 获取轮转图中一个view
    private View getGalleryView(Map<String, String> map) {
        View view = new View(getActivity());
        ImageView img = new ImageView(getActivity());
        img.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(getContext())
                .load(map.get("full_image_path"))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.banner_default)
                .into(img);
        view = img;
        return view;
    }
}
