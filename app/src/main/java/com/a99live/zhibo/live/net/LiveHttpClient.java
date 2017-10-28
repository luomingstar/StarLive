package com.a99live.zhibo.live.net;

import android.text.TextUtils;
import android.util.Log;

import com.a99live.zhibo.live.LiveZhiBoApplication;
import com.a99live.zhibo.live.net.interceptor.XgoLogInterceptor;
import com.a99live.zhibo.live.token.PhoneManager;
import com.a99live.zhibo.live.utils.EncodeUtils;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.ShowWeb;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.a99live.zhibo.live.net.LiveHttpUrl.getRootUrl;

/**
 * Created by fuyk on 2016/9/1.
 */
public class LiveHttpClient {

    public static final String TAG_LOG = "livelog";

    public static final int METHOD_COUNT = 4;
    public static String METHOD_GET = "GET";
    public static String METHOD_POST = "POST";

    private static final LiveHttpClient mClient = new LiveHttpClient();
    private static final OkHttpClient mOkHttpClient = new OkHttpClient();

    static {
        //添加日志过滤器
        XgoLogInterceptor logInterceptor = new XgoLogInterceptor(new XgoLogInterceptor.Logger() {

            @Override
            public void log(String message) {
                Log.d(TAG_LOG ,message);
            }
        });
        logInterceptor.setLevel(XgoLogInterceptor.Level.BODY);
        mOkHttpClient.interceptors().add(logInterceptor);
    }


    public static LiveHttpClient getClient() {
        return mClient;
    }

    /**
     * 同步模式
     */
    public Response execute(Request request) throws IOException {
        return mOkHttpClient.newCall(request).execute();
    }

    /**
     * 异步callBack模式
     */
    public void enqueue(Request request, Callback responseCallback) {
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 通过http请求的基本信息，创建一个Request对象
     */
    public Request getRequest(String path, String method, LiveRequestParams params) {
        if (params == null) {
            params = new LiveRequestParams();
        }

        String bizStr = getGetParams(params, true);
        Log.i("livelog", "bizStr="+bizStr);
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

        if ("".equals(token)){
            try {
                Response response = getClient().execute(LiveHttpClient.getClient().getTokenRequest());
                if(response.code()>=200 && response.code()<300) {
                    String result = response.body().string();
                    Log.d("livelog_result","result" + result);
                    ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(result);
                    if (listMapByJson.size()>0){
                        Map<String, String> map = listMapByJson.get(0);
                        if ("0".equals(map.get("code"))){
                            String data = map.get("data");
                            ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(data);
                            if (dataList.size() > 0){
                                Map<String, String> dataMap = dataList.get(0);
                                String key = dataMap.get("key");
                                String global = dataMap.get("global");
                                String msg_exp_time = "";
                                ArrayList<Map<String, String>> globalList = JsonUtil.getListMapByJson(global);
                                if (globalList.size()>0){
                                    Map<String, String> globaMap = globalList.get(0);
                                    msg_exp_time = globaMap.get("msg_exp_time");
                                }
                                SPUtils.putString(SPUtils.TAG_TOKEN, key);
                                SPUtils.putString(SPUtils.TAG_TIME, msg_exp_time);
                            }
                        }
                    }
//                    GetTokenInfo tokena = JsonParser.fromJson(result, GetTokenInfo.class);
//                    token = tokena.getData().getKey();
//                    SPUtils.putString(SPUtils.TAG_TOKEN, tokena.getData().getKey());
//                    SPUtils.putString(SPUtils.TAG_TIME, tokena.getData().getGlobal().getMsg_exp_time());
                }else{
                    Log.d("livelog" , "获取Token失败");
                }
            }catch (Exception e){

            }
        }
        Log.d("livelog", token);
//        String  hmac= getHmac(path, bizStr, token, t, acode, hasUcode?ucode:null);
        String hmac;
        if (path.startsWith("http")){
            if (path.indexOf(LiveHttpUrl.getIsOurUrl()) != -1){
                path = path.replace(LiveHttpUrl.getIsOurUrl(),"");
                hmac = getHmac(path, bizStr, token, t, acode, ucode);
            }else if (path.indexOf(LiveHttpUrl.getIsOurUrlWithHttp()) != -1){
                path = path.replace(LiveHttpUrl.getIsOurUrlWithHttp(),"");
                hmac = getHmac(path, bizStr, token, t, acode, ucode);
                path = LiveHttpUrl.getIsOurUrlWithHttp() + path;
            }else{
                if (!TextUtils.isEmpty(ShowWeb.uri)){
                    hmac = getHmac(ShowWeb.uri, bizStr, token, t, acode, ucode);
                }else{
                    hmac = getHmac("/V1/client/verify", bizStr, token, t, acode, ucode);
                }
            }
        }else{
            hmac = getHmac(path, bizStr, token, t, acode, ucode);
        }


        params.put("hmac", hmac);
        params.put("t", String.valueOf(t));
        String url = path;
        if (!path.startsWith("http")){
            url = getRootUrl() + path;
        }
        Request.Builder builder = new Request.Builder();

        if (LiveHttpClient.METHOD_GET.equalsIgnoreCase(method)) {
            builder.addHeader("cookie", cookieString);
            builder.url(initGetRequest(url, params)).get();
        } else if (LiveHttpClient.METHOD_POST.equalsIgnoreCase(method)) {
            builder.addHeader("cookie", cookieString);
            builder.url(url).post(initRequestBody(params));
        }

        return builder.build();
    }

    private String getHmac(String path, String bizStr, String token, long t, String acode, String ucode) {

        long ll = t%METHOD_COUNT;
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

    /**
     * 获取Token
     *
     * @return
     */
    public Request getTokenRequest() {
        Request.Builder builder = new Request.Builder();
        Log.i("infoCookie",PhoneManager.getCookieInfo() );
        builder.url(LiveHttpUrl.getRootUrl() + "/v1/client/get").get()
//                .addHeader("Content-Type","multipart/form-data")
                .addHeader("Cookie",PhoneManager.getCookieInfo());
        return builder.build();
    }

    /**
     * 初始化Body类型请求参数
     * init Body type params
     */
    private RequestBody initRequestBody(LiveRequestParams params) {
        MultipartBuilder bodyBuilder = new MultipartBuilder().type(MultipartBuilder.FORM);
        Set<Map.Entry<String, Object>> entries = params.getParamsMap().entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            Object value = entry.getValue();

            Log.i("livelog" , key+ "="+ value);

            if (value instanceof File) {
                File file = (File) value;
                try {
                    FileNameMap fileNameMap = URLConnection.getFileNameMap();
                    String mimeType = fileNameMap.getContentTypeFor(file.getAbsolutePath());
                    bodyBuilder.addFormDataPart(key, file.getName(), RequestBody.create(MediaType.parse(mimeType), file));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                bodyBuilder.addFormDataPart(key, value.toString());
            }
        }
        return bodyBuilder.build();
    }

    /**
     * 初始化Get请求参数
     */
    private String initGetRequest(String url, LiveRequestParams params) {
        StringBuilder sb = new StringBuilder(url);
        //has params ?
        if (params.size() > 0) {
            sb.append('?').append(getGetParams(params, false));
            url = new String(sb);
        }

        return url;
    }

    private String getGetParams(LiveRequestParams params, boolean flag) {

        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, Object>> entries = params.getParamsMap().entrySet();
        for (Map.Entry entry : entries) {
            String keyStr = entry.getKey().toString();
            Object value = entry.getValue();

            if (flag) {
                if ((keyStr.contains("[") && keyStr.contains("]"))
                        || value == null || value instanceof File
                        || TextUtils.isEmpty(value.toString())) {
                    continue;
                }
            }

            String valueStr = keyStr + "=" + value.toString();
            Log.i("valueStr", valueStr);
            sb.append(valueStr);
//            if (count == params.size()) {
//                break;
//            }
            sb.append("&");
        }
        if (!TextUtils.isEmpty(sb.toString())){
            sb.deleteCharAt(sb.length()-1);
        }
        Log.d("livelog","sb=" + sb.toString());


//        String  sbt = Uri.encode(sb.toString());
//        String sbt = "";
//        try {
//            sbt = URLEncoder.encode(sb.toString(),"GB2312");
        String  sbt = UIUtils.encode(sb.toString(),"UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
        return sbt;
    }

    /**
     * set timeout
     */
    public void setConnectTimeout(long time) {
        if (mOkHttpClient != null) {
            mOkHttpClient.setConnectTimeout(time, TimeUnit.MILLISECONDS);
        }
    }


}
