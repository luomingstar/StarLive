package com.a99live.zhibo.live.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.AppProtocol;
import com.a99live.zhibo.live.token.PhoneManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 强制更新
 * Created by fuyk on 2016/9/29.
 */
public class UpdateManager {

    private Context mContext; //上下文
    private static UpdateManager updateManager = null;
    private boolean isShowToast;

    private String apkUrl = ""; //apk下载地址
    private static final String savePath = FileUtils.getSDDir(); //apk保存到SD卡的路径
    private static final String saveFileName = savePath + "99live.apk"; //完整路径名

    private ProgressBar mProgress; //下载进度条控件
    private static final int DOWNLOADING = 1; //表示正在下载
    private static final int DOWNLOADED = 2; //下载完毕
    private static final int DOWNLOAD_FAILED = 3; //下载失败
    private int progress; //下载进度
    private boolean cancelFlag = false; //取消下载标志位

    private String clientVersion = ""; //客户端当前的版本号
    private boolean mForceUpdate = true; //是否强制更新

    private AlertDialog alertDialog1, alertDialog2; //表示提示对话框、进度条对话框

    private UpdateManager(){

    }
    /** 构造函数 */
    public static UpdateManager getInstance(){
        if (updateManager == null){
            updateManager = new UpdateManager();
        }
        return updateManager;
    }

    /**APP升级接口*/
    public void getUpdate(Context context,boolean isShowToast){
        this.mContext = context;
        this.isShowToast = isShowToast;
        LiveRequestParams params = new LiveRequestParams();
        AppProtocol versionProtocol = new AppProtocol();
        versionProtocol.getAppVersion(params)
                .observeOn(AndroidSchedulers.mainThread())
//                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("livelog","更新内容" + s);
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                String data = map.get("data");
                                ArrayList<Map<String, String>> listMapByJson1 = JsonUtil.getListMapByJson(data);
                                if (listMapByJson1.size()>0){
                                    Map<String, String> dataMap = listMapByJson1.get(0);

                                    initUpdate(dataMap);

                                }
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }

    private void initUpdate(Map<String, String> map) {
        String apkUrl = map.get("and_url");
        String desc = map.get("desc");
        String serverVersion = map.get("vnum");
        String forceUpdate = map.get("force_update");

        showNoticeDialog(apkUrl,serverVersion, desc, forceUpdate);
    }

    /** 显示更新对话框
     * 1.下载地址
     * 2.服务器版本号
     * 3.更新详情
     * 4.是否强制升级*/
    private void showNoticeDialog(String apkUrl, String serverVersion, String updateDescription, String forceUpdate) {
        this.apkUrl = apkUrl;
        String s = serverVersion.replace(".", "");
        clientVersion = PhoneManager.getVersionName(mContext);
        String sb = clientVersion.replace(".","");
        //如果版本最新，则不需要更新
        if (Integer.parseInt(s) <= Integer.parseInt(sb)){
            if (isShowToast)
                UIUtils.showToast("已是最新版本");
            return;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        //服务器获取的版本
        dialog.setTitle("发现新版本 ：" + serverVersion);
        //更新内容描述
        dialog.setMessage(updateDescription);
        dialog.setPositiveButton("现在更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                // TODO Auto-generated method stub
                arg0.dismiss();
                if (Build.VERSION.SDK_INT >= 23) {
                    if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            ) {
                        ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                1);
                    } else {
                        showDownloadDialog();
                    }
                }else{
                    showDownloadDialog();
                }

            }
        });
        //是否强制更新
        if ("false".equals(forceUpdate)) {
            dialog.setNegativeButton("待会更新", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // TODO Auto-generated method stub
                    arg0.dismiss();
                }
            });
            mForceUpdate = false;
        }else{
            mForceUpdate = true;
        }
        alertDialog1  = dialog.create();
        alertDialog1.setCancelable(false);
        alertDialog1.show();
    }

    /** 显示进度条对话框 */
    public void showDownloadDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setTitle("正在更新");
        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.softupdate_progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
        dialog.setView(v);
        //如果是强制更新，则不显示取消按钮
//        if (mForceUpdate == false) {
//            dialog.setNegativeButton("后台下载", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface arg0, int arg1) {
//                    // TODO Auto-generated method stub
//                    arg0.dismiss();
//                    cancelFlag = false;
//                }
//            });
//        }
        alertDialog2  = dialog.create();
        alertDialog2.setCancelable(false);
        alertDialog2.show();

        //下载apk
        downloadAPK();
    }

    /** 下载apk的线程 */
    private void downloadAPK() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    URL url = new URL(apkUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();

                    int length = conn.getContentLength();
                    InputStream is = conn.getInputStream();

                    File file = new File(savePath);
                    if(!file.exists()){
                        file.mkdir();
                    }
                    String apkFile = saveFileName;
                    File ApkFile = new File(apkFile);
                    FileOutputStream fos = new FileOutputStream(ApkFile);

                    int count = 0;
                    byte buf[] = new byte[1024];

                    do{
                        int numread = is.read(buf);
                        count += numread;
                        progress = (int)(((float)count / length) * 100);
                        //更新进度
                        mHandler.sendEmptyMessage(DOWNLOADING);
                        if(numread <= 0){
                            //下载完成通知安装
                            mHandler.sendEmptyMessage(DOWNLOADED);
                            break;
                        }
                        fos.write(buf, 0, numread);
                    }while(!cancelFlag); //点击取消就停止下载.

                    fos.close();
                    is.close();
                } catch(Exception e) {
                    mHandler.sendEmptyMessage(DOWNLOAD_FAILED);
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /** 更新UI的handler */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case DOWNLOADING:
                    mProgress.setProgress(progress);
                    break;
                case DOWNLOADED:
                    if (alertDialog2 != null)
                        alertDialog2.dismiss();

                    if (Build.VERSION.SDK_INT >= 23) {
                        if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                                ) {
                            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    2);
                        } else {
                            installAPK();
                        }
                    }else{
                        installAPK();
                    }

                    break;
                case DOWNLOAD_FAILED:
                    Toast.makeText(mContext, "网络断开，请稍候再试", Toast.LENGTH_LONG).show();
                    alertDialog2.dismiss();
                    break;
                default:
                    break;
            }
        }
    };

    /** 下载完成后自动安装apk */
    public void installAPK() {
        File apkFile = new File(saveFileName);
        if (!apkFile.exists()) {
            UIUtils.showToast("文件已损坏或不存在，请重新下载");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
//判断是否是AndroidN以及更高的版本  配置FileProvider 的时候跟随takephoto的配置具体查看takephoto
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mContext, "com.a99live.zhibo.live" + ".fileprovider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        mContext.startActivity(intent);
        if (mForceUpdate){
            System.exit(0);
        }


//        File apkFile = new File(saveFileName);
//        if (!apkFile.exists()) {
//            return;
//        }
////        System.exit(0);
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setDataAndType(Uri.parse("file://" + apkFile.toString()), "application/vnd.android.package-archive");
//        mContext.startActivity(intent);
    }

}
