package com.a99live.zhibo.live.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.a99live.zhibo.live.LiveZhiBoApplication;
import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.model.UMengInfo;
import com.a99live.zhibo.live.net.LiveHttpClient;
import com.a99live.zhibo.live.net.LiveHttpUrl;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.WebProtocol;
import com.a99live.zhibo.live.token.PhoneManager;
import com.a99live.zhibo.live.utils.EncodeUtils;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.CircleProgressView;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by JJGCW on 2016/11/28.
 */
@SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
public class ShowWeb extends BaseActivity {

    @Bind(R.id.tv_title)
    TextView textView;

    @Bind(R.id.tv_finish)
    TextView tv_finish;

    @Bind(R.id.loadingImageView)
    CircleProgressView loadingImageView;

    private WebView webView;

    String til;
    String url;
    String titleStr;
    String desc;
    private String is_login = "1";//0可以分享 1不可以分享
    public static String uri = "";//算hamc的时候用的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_webview);
        ButterKnife.bind(this);
        loadingImageView.setVisibility(View.VISIBLE);
        loadingImageView.spin();
        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("title"))
                titleStr = bundle.getString("title");
            if (bundle.containsKey("desc"))
                desc = bundle.getString("desc");
            if (bundle.containsKey("url"))
                url = bundle.getString("url");
            if (bundle.containsKey("is_login"))
                is_login = bundle.getString("is_login");
            if (bundle.containsKey("uri")) {
                String uuu = bundle.getString("uri");
                this.uri = uuu;
            }
        }

        if(titleStr != null && titleStr != ""){
            til = titleStr;
        }else{
            til = "";
        }
        textView.setText(til);
        if ("0".equals(is_login)){
            tv_finish.setVisibility(View.VISIBLE);
            tv_finish.setText(R.string.h5_share);
        }else{
            tv_finish.setVisibility(View.GONE);
        }
//        url = "http://www.i999d.cn/app/h5app.html";
        if (url.startsWith("http")){
//            tv_finish.setVisibility(View.VISIBLE);
            if (url.indexOf(LiveHttpUrl.getIsOurUrl()) != -1){
//                path = path.replace(LiveHttpUrl.getIsOurUrl(),"");
//                hmac = getHmac(path, bizStr, token, t, acode, ucode);
                LoadWebView(url);
            }else if (url.indexOf(LiveHttpUrl.getIsOurUrlWithHttp()) != -1){
                LoadWebView(url);
//                path = path.replace(LiveHttpUrl.getIsOurUrlWithHttp(),"");
//                hmac = getHmac(path, bizStr, token, t, acode, ucode);
//                path = LiveHttpUrl.getIsOurUrlWithHttp() + path;
            }else{
                LoadWeb(url);
            }

        }else{
//            tv_finish.setVisibility(View.GONE);
            LoadWebView(url);
        }

    }

    private void LoadWeb(String url) {
        webView = (WebView) this.findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();
//		settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
//		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//		settings.setAppCacheEnabled(false);
//		settings.setSupportZoom(true);
//		settings.setJavaScriptEnabled(true);
//		settings.setBuiltInZoomControls(true);
//		settings.setSavePassword(false);
//		settings.setDefaultTextEncodingName("utf-8");
        webView.addJavascriptInterface(new JsAppCommon(this),
                "gcw");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                stopLoading();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                stopLoading();
                UIUtils.showToast("加载网站失败");
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                //handler.cancel(); // Android默认的处理方式
                handler.proceed();  // 接受所有网站的证书
                //handleMessage(Message msg); // 进行其他处理
            }
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
////                view.loadUrl(urll);
//                return true;
////                return super.shouldOverrideUrlLoading(view, request);
//            }
        });
        //解决https图片加载问题
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//MIXED_CONTENT_ALWAYS_ALLAW

        settings.setUseWideViewPort(true);//适应分辨率
        settings.setLoadWithOverviewMode(true);
        settings.setLoadsImagesAutomatically(true); // 支持自动加载图片

        settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(false);
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setSavePassword(false);
        settings.setDefaultTextEncodingName("utf-8");

        Map<String,String> map = new HashMap<>();
        map.put("cookie",getCookie());
        synCookies(this,url,getCookie());
        webView.loadUrl(getData(url),map);

    }

    private String getData(String path){
        //设备码
        String acode = PhoneManager.getMyUUID(LiveZhiBoApplication.getApp());
        //时间戳
        long t = System.currentTimeMillis();
        //用户ucode
        String ucodeInt = SPUtils.getString(SPUtils.USER_CODE);
        Log.i("livelog", "ucodeInt=" +ucodeInt);
        String ucode = ucodeInt == "0" ? null : ucodeInt;

        String cookieString = "";
        if (TextUtils.isEmpty(String.valueOf(ucodeInt))){
            cookieString = PhoneManager.getCookieInfo();
        }else {
            cookieString = PhoneManager.getCookieInfo()+"ucode="+SPUtils.getString(SPUtils.USER_CODE)+";uauth="+SPUtils.getString(SPUtils.USER_UAUTH);
        }
        Log.e("livelog-cookieString:", cookieString);
        String token = SPUtils.getString(SPUtils.TAG_TOKEN);

        String hmac;
        if (path.startsWith("http")){
            if (path.indexOf(LiveHttpUrl.getIsOurUrl()) != -1){
                path = path.replace(LiveHttpUrl.getIsOurUrl(),"");
                hmac = getHmac(path, "", token, t, acode, ucode);
            }else if (path.indexOf(LiveHttpUrl.getIsOurUrlWithHttp()) != -1){
                path = path.replace(LiveHttpUrl.getIsOurUrlWithHttp(),"");
                hmac = getHmac(path, "", token, t, acode, ucode);
                path = LiveHttpUrl.getIsOurUrlWithHttp() + path;
            }else{
                if (!TextUtils.isEmpty(ShowWeb.uri)){
                    hmac = getHmac(ShowWeb.uri, "", token, t, acode, ucode);
                }else{
                    hmac = getHmac("/V1/client/verify", "", token, t, acode, ucode);
                }
            }
        }else{
            hmac = getHmac(path, "", token, t, acode, ucode);
        }

        if (path.contains("?")){
            path = path +  "&t="+t + "&sha1="+hmac;
        }else{
            path = path +  "?t="+t + "&sha1="+hmac;
        }

        return path;
    }

    private String getHmac(String path, String bizStr, String token, long t, String acode, String ucode) {

        long ll = t% LiveHttpClient.METHOD_COUNT;
        int methodIndex = Integer.parseInt(ll+"");
        String hmac = "";
        switch (methodIndex) {
            case 0:
                hmac = EncodeUtils.md5(EncodeUtils.md5(bizStr + token).toLowerCase() + path + t + acode).toLowerCase();
                Log.e("livelog","hmc0::"+hmac);
                break;
            case 1:
                hmac = EncodeUtils.md5(path + EncodeUtils.md5(bizStr + token).toLowerCase() + t + acode).toLowerCase();
                Log.e("livelog","hmc1::"+hmac);
                break;
            case 2:
                hmac = EncodeUtils.md5(path + t + EncodeUtils.md5(bizStr + token).toLowerCase() + acode).toLowerCase();
                Log.e("livelog","hmc2::"+hmac);
                break;
            case 3:
                hmac = EncodeUtils.md5(path + t + acode + EncodeUtils.md5(bizStr + token).toLowerCase()).toLowerCase();
                Log.e("livelog","hmc3::"+hmac);
                break;
            default:
                return hmac;
        }

        if (TextUtils.isEmpty(ucode)) {
            return hmac;
        } else {
            hmac = EncodeUtils.md5(hmac + ucode).toLowerCase();
            Log.e("livelog","hmc&ucode::"+hmac);
            return hmac;
        }
    }

    public static void goShowWeb(Context context, String url, String title, String desc,String is_login,String uri){
        Intent intent = new Intent(context, ShowWeb.class);
        intent.putExtra("title",title);
        intent.putExtra("desc", desc);
        intent.putExtra("url",url);
        intent.putExtra("is_login",is_login);
        intent.putExtra("uri",uri);
        context.startActivity(intent);
    }

    @OnClick({R.id.layout_back, R.id.tv_finish})
    void onClick(View view){
        switch (view.getId()){
            case R.id.layout_back:
                onBackPressed();
                break;
            case R.id.tv_finish:
                if (TextUtils.isEmpty(titleStr) && TextUtils.isEmpty(desc)) {
                    UMengInfo.getH5ShareTool(ShowWeb.this, getString(R.string.app_name),"",
                            umShareListener, url);
                }else if (TextUtils.isEmpty(titleStr)){
                    UMengInfo.getH5ShareTool(ShowWeb.this, getString(R.string.app_name), desc,
                            umShareListener, url);
                }else if (TextUtils.isEmpty(desc)){
                    UMengInfo.getH5ShareTool(ShowWeb.this, titleStr,"",
                            umShareListener, url);
                }else {
                    UMengInfo.getH5ShareTool(ShowWeb.this, titleStr, desc,
                            umShareListener, url);
                }
                break;
        }
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable throwable) {
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    };


    private void LoadWebView(final String url){
//        String mm = getRootUrl() + url;
//        String cookieString = PhoneManager.getCookieInfo()+"ucode="+ SPUtils.getString(SPUtils.USER_CODE)+";uauth="+SPUtils.getString(SPUtils.USER_UAUTH);
//        synCookies(this ,mm , cookieString);
        WebProtocol webProtocol = new WebProtocol();
        LiveRequestParams params = new LiveRequestParams();
        webProtocol.getWebView(url,params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        showWeb(s,url);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast(R.string.net_error);
                    }
                });
    }

    public interface ShowWebCallBack{
        void showProgress(boolean isShow);
    }

    private void showWeb(String data,String url){
//        String mm = getRootUrl() + url;
//        PhoneManager.getCookieInfo()
        webView = (WebView) this.findViewById(R.id.webview);
        webView.addJavascriptInterface(new JsAppCommon(this),
                "alive");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                view.loadUrl(request.toString());
                return false;
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                //handler.cancel(); // Android默认的处理方式
                handler.proceed();  // 接受所有网站的证书
                //handleMessage(Message msg); // 进行其他处理
            }
        });
        WebSettings settings = webView.getSettings();

        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setDisplayZoomControls(false);

        settings.setLoadsImagesAutomatically(true); // 支持自动加载图片

        settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(false);
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setSavePassword(false);
        settings.setDefaultTextEncodingName("utf-8");

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//MIXED_CONTENT_ALWAYS_ALLAW

        //让网页自适应屏幕宽度
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
//        webView.loadUrl(mm);
//        String mm = getRootUrl() + url;
//        String cookieString = PhoneManager.getCookieInfo()+"ucode="+ SPUtils.getString(SPUtils.USER_CODE)+";uauth="+SPUtils.getString(SPUtils.USER_UAUTH);
//        synCookies(this ,mm , cookieString);

//        StringBuilder sb = new StringBuilder();
//        sb.append("<HTML><HEAD><LINK href=\"styles.css\" type=\"text/css\" rel=\"stylesheet\"/></HEAD><body>");
//        sb.append(data.toString());
//        sb.append("</body></HTML>");
        webView.loadDataWithBaseURL(url, data, "text/html", "utf-8", null);
//        webView.loadUrl(url);

//        webView.loadDataWithBaseURL(null, data, "text/html", "utf-8", null);

        stopLoading();
    }


    private void initWeb(String urll) {
        webView = (WebView) this.findViewById(R.id.webview);
        WebSettings settings = webView.getSettings();
//		settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
//		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//		settings.setAppCacheEnabled(false);
//		settings.setSupportZoom(true);
//		settings.setJavaScriptEnabled(true);
//		settings.setBuiltInZoomControls(true);
//		settings.setSavePassword(false);
//		settings.setDefaultTextEncodingName("utf-8");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                stopLoading();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                stopLoading();
                UIUtils.showToast("加载网站失败");
            }

            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
                                 //handler.cancel(); // Android默认的处理方式
                                 handler.proceed();  // 接受所有网站的证书
                                 //handleMessage(Message msg); // 进行其他处理
            }
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
////                view.loadUrl(urll);
//                return true;
////                return super.shouldOverrideUrlLoading(view, request);
//            }
        });
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);//MIXED_CONTENT_ALWAYS_ALLAW

        settings.setUseWideViewPort(true);//适应分辨率
        settings.setLoadWithOverviewMode(true);
        settings.setLoadsImagesAutomatically(true); // 支持自动加载图片

        settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(false);
        settings.setSupportZoom(true);
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setSavePassword(false);
        settings.setDefaultTextEncodingName("utf-8");
        webView.loadUrl(urll);
        //webView.loadUrl("http://nce.shexl.sooker.3322.org/edm/wap0817");
    }

    private String getCookie(){
        String cookieString = PhoneManager.getCookieInfo()+"ucode="+ SPUtils.getString(SPUtils.USER_CODE)+";uauth="+SPUtils.getString(SPUtils.USER_UAUTH);
        Log.d("livelog",cookieString);
        return cookieString;
    }

    private void postWebView(String url){
//        StringBuilder builder1 = new StringBuilder();
        String cookieString = PhoneManager.getCookieInfo()+"ucode="+ SPUtils.getString(SPUtils.USER_CODE)+";uauth="+SPUtils.getString(SPUtils.USER_UAUTH);
//
//        try {//拼接post提交参数
//            builder1.append("interfaceName=").append(params.get("interfaceName")).append("&")
//                    .append("interfaceVersion=").append(params.get("interfaceVersion")).append("&")
//                    .append("tranData=").append(URLEncoder.encode(params.get("tranData"), "UTF-8")).append("&")
//                    .append("merSignMsg=").append(URLEncoder.encode(params.get("merSignMsg"), "UTF-8")).append("&")
//                    .append("appId=").append(params.get("appId")).append("&")
//                    .append("transType=").append(params.get("transType"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        String postData = builder1.toString();
        String encode = EncodeUtils.encode(cookieString, "UTF-8");
        byte[] bytes = encode.getBytes();
        webView.postUrl(url,bytes);
    }

    private void stopLoading(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (loadingImageView != null){
                    loadingImageView.stopSpinning();
                    loadingImageView.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView != null &&  webView.canGoBack()) {
            webView.goBack();
        }else {
            super.onBackPressed();
        }
    }

    /**
     *同步cookie
     * @param context
     * @param url
     */
//    public static void synCookies(Context context, String url,String cookieValue) {
//        CookieSyncManager.createInstance(context);
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setCookie(url,cookieValue);
//        CookieSyncManager.getInstance().sync();
//    }

    /**
     * 同步一下cookie
     */
    public static void synCookies(Context context, String url,String cookies) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
//        cookieManager.removeSessionCookie();//移除
        String[] split = cookies.split(";");
        for (int i=0;i<split.length;i++) {
            cookieManager.setCookie(url, split[i]);//cookies是在HttpClient中获得的cookie
        }
        CookieSyncManager.getInstance().sync();
    }

    /**
     * 移除cookie
     * @param context
     */
    private void removeCookie(Context context) {
        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        CookieSyncManager.getInstance().sync();
    }

    /**
     * Sync Cookie
     */
    private void syncCookie(Context context, String url){
        try{
//            Log.d("Nat: webView.syncCookie.url", url);

            CookieSyncManager.createInstance(context);

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.removeSessionCookie();// 移除
            cookieManager.removeAllCookie();
            String oldCookie = cookieManager.getCookie(url);
            if(oldCookie != null){
//                Log.d("Nat: webView.syncCookieOutter.oldCookie", oldCookie);
            }

            StringBuilder sbCookie = new StringBuilder();
            sbCookie.append(String.format("JSESSIONID=%s","INPUT YOUR JSESSIONID STRING"));
            sbCookie.append(String.format(";domain=%s", "INPUT YOUR DOMAIN STRING"));
            sbCookie.append(String.format(";path=%s","INPUT YOUR PATH STRING"));

            String cookieValue = sbCookie.toString();
            cookieManager.setCookie(url, cookieValue);
            CookieSyncManager.getInstance().sync();

            String newCookie = cookieManager.getCookie(url);
            if(newCookie != null){
//                Log.d("Nat: webView.syncCookie.newCookie", newCookie);
            }
        }catch(Exception e){
//            Log.e("Nat: webView.syncCookie failed", e.toString());
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null){
            webView.clearHistory();
            ((ViewGroup)webView.getParent()).removeView(webView);
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;


        }
        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptCookie(false);
        cookieManager.removeSessionCookie();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.removeAllCookies(null);
        } else {
            cookieManager.removeAllCookie();
        }
    }

    @Override
    public void finish() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        super.finish();
        overridePendingTransition(0,R.anim.tran_right_out);
    }
}
