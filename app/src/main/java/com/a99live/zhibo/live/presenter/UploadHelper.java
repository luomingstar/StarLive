package com.a99live.zhibo.live.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.protocol.CosProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TCConstants;
import com.tencent.upload.Const;
import com.tencent.upload.UploadManager;
import com.tencent.upload.task.ITask;
import com.tencent.upload.task.IUploadTaskListener;
import com.tencent.upload.task.data.FileInfo;
import com.tencent.upload.task.impl.PhotoUploadTask;

import java.util.ArrayList;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Cos人图片上传类
 */
public class UploadHelper implements IUploadTaskListener{
    private CosProtocol cosProtocol;

    private final static int THREAD_GET_SIG = 1;
    private final static int THREAD_UPLAOD = 2;
    private final static int THREAD_GETSIG_UPLOAD = 3;

    private final static int MAIN_CALL_BACK = 1;
    private final static int MAIN_PROCESS = 2;

    private Context mContext;
    private OnUploadListener mListerner;
    private HandlerThread mThread;
    private Handler mHandler ;
    private Handler mMainHandler;

    public UploadHelper(Context context, OnUploadListener listener) {
        cosProtocol = new CosProtocol();
        mContext = context;
        mListerner = listener;

        mThread = new HandlerThread("upload");
        mThread.start();
        mHandler = new Handler( Looper.getMainLooper(),new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case THREAD_GET_SIG:
//                        doUpdateSig();
                        break;
                    case THREAD_UPLAOD:
                        Log.d("livelog", "万象有图开始");
                        doUpdateSig((String) msg.obj);
//                        doUploadCover((String) msg.obj);
                        break;
                    case THREAD_GETSIG_UPLOAD:
                        doUpdateSig((String) msg.obj);
//                        doUploadCover((String) msg.obj);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });

        mMainHandler = new Handler( Looper.getMainLooper(),new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MAIN_CALL_BACK:
                        if (mListerner != null) {
                            mListerner.onUploadResult(msg.arg1, msg.obj);
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    //获取签名
    private void doUpdateSig(final String path) {
        LiveRequestParams params = new LiveRequestParams();
        cosProtocol.getUploadSign(params)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.i("cosSign---",s);
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                ArrayList<Map<String, String>> dataList = JsonUtil.getListMapByJson(map.get("data"));
                                if (dataList.size()>0){
                                    Map<String, String> dataMap = dataList.get(0);
//                                    saveCosSign(dataMap);
                                    doUploadCover(path,dataMap);
                                }

                            }else{
                                //wrong
                                Message msg = new Message();
                                msg.what = MAIN_CALL_BACK;
                                msg.arg1 = 1;
                                msg.obj = map.get("msg");

                                mMainHandler.sendMessage(msg);
                            }
                        }else{
                            //wrong
                            Message msg = new Message();
                            msg.what = MAIN_CALL_BACK;
                            msg.arg1 = 1;
                            msg.obj = s;

                            mMainHandler.sendMessage(msg);
                        }


//                        UploadModel sign = JsonParser.fromJson(s, UploadModel.class);
//                        saveCosSign(sign);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Message msg = new Message();
                        msg.what = MAIN_CALL_BACK;
                        msg.arg1 = 1;
                        msg.obj = "网络异常，请重试或反馈给我们";

                        mMainHandler.sendMessage(msg);
                    }
                });
    }

    private void saveCosSign( Map<String, String> dataMap) {
        Log.d("livelog", "upload: " +  dataMap.get("bucket")+"/" +  dataMap.get("fileId")
                +"/"+ dataMap.get("sign"));
        SPUtils.putString(SPUtils.SIGN, dataMap.get("sign"));
        SPUtils.putString(SPUtils.BUCKET, dataMap.get("bucket"));
        SPUtils.putString(SPUtils.FILE_ID, dataMap.get("fileId"));
    }

    private void doUploadCover(final String path,Map<String, String> dataMap) {
        // 实例化Photo业务上传管理类
        UploadManager photoUploadMgr = new UploadManager(mContext, TCConstants.COS_APPID,
                Const.FileType.Photo, "livephoto");
        Log.d("livelog","uploadCover do upload path:"+path);
        //上传图片
        PhotoUploadTask photoUploadTask = new PhotoUploadTask(path, this);

//        Log.d("livelog", "upload: " + SPUtils.getString(SPUtils.BUCKET)+"/" + SPUtils.getString(SPUtils.FILE_ID)
//        +"/"+SPUtils.getString(SPUtils.SIGN));

        photoUploadTask.setBucket(dataMap.get("bucket"));
        photoUploadTask.setFileId(dataMap.get("fileId"));
        photoUploadTask.setAuth(dataMap.get("sign"));
        photoUploadMgr.upload(photoUploadTask);
        Log.d("livelog", "upload:2222222222222222222222222222222222 " + path);
    }

    /**
     * 获取签名
     */
//    public void updateSign() {
//        mHandler.sendEmptyMessage(THREAD_GET_SIG);
//    }

    /**
     * 上传图片
     */
    public void uploadCover(String path) {
        Message msg = new Message();
        msg.what = THREAD_UPLAOD;
        msg.obj = path;

        mHandler.sendMessage(msg);
        Log.d("livelog","启动线程开始上传");
    }

    @Override
    public void onUploadSucceed(final FileInfo result) {
        Log.d("livelog","上传图片成功:"+result);
        Message msg = new Message();
        msg.what = MAIN_CALL_BACK;
        msg.arg1 = 0;
        msg.obj = result;
//        SPUtils.putString(SPUtils.USER_AVATAR_ID, result.fileId);
//        SPUtils.putString(SPUtils.FILE_ID, result.fileId);


        mMainHandler.sendMessage(msg);

    }

    @Override
    public void onUploadFailed(final int i,final String s) {
        Log.w("livelog", "上传图片失败:" + i + ",msg:" + s);
        Message msg = new Message();
        msg.what = MAIN_CALL_BACK;
        msg.arg1 = i;
        msg.obj = s;

        mMainHandler.sendMessage(msg);

    }

    @Override
    public void onUploadProgress(final long l, final long l1) {
        Log.d("livelog", "上传图片进度: " + l + "/" + l1);
    }

    @Override
    public void onUploadStateChange(final ITask.TaskState taskState) {
        Log.d("livelog", "上传图片状态: " + taskState);
    }

    public interface OnUploadListener {
        void onUploadResult(int code, Object object);
    }
}
