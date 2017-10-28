package com.a99live.zhibo.live.activity.user;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.activity.BaseActivity;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.presenter.UploadHelper;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.MyProgressDialog;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
import com.tencent.upload.task.data.FileInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * 认证页面
 * Created by JJGCW on 2016/11/15.
 */

public class Approve extends BaseActivity implements UploadHelper.OnUploadListener, TakePhoto.TakeResultListener,InvokeListener {

    @Bind(R.id.name)
    EditText mNameEdit;

    @Bind(R.id.id_card)
    EditText mIdCardEdit;

    @Bind(R.id.approve_camera)
    ImageView mApproveCsmera;

    @Bind(R.id.camare_icon)
    ImageView mCamareIcon;

    @Bind(R.id.upload_approve)
    TextView mUploadApprove;

    @Bind(R.id.tv_title)
    TextView mTvTitle;

    @Bind(R.id.hide_soft_key)
    RelativeLayout hide_soft_key;

    private static final int CAPTURE_IMAGE_CAMERA = 100;
    private static final int IMAGE_STORE = 200;
    private static final int CROP_CHOOSE = 10;
    private static final String TAG = TakePhotoActivity.class.getName();
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;
    private Dialog mPicChsDialog;
    private UploadHelper uploadHelper;
    private Uri fileUri, cropUri;
    private String path;
    private boolean isGetImageSuccess;
    private UserProtocol userProtocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_approve);
        ButterKnife.bind(this);
        mTvTitle.setText("用户认证");
        uploadHelper = new UploadHelper(this, this);
        initPhotoDialog();
        userProtocol = new UserProtocol();

        hide_soft_key.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                return imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        });
    }

    public static void goApprove(Context context){
        Intent intent = new Intent(context,Approve.class);
        context.startActivity(intent);
    }

    @OnClick({R.id.approve_camera,R.id.upload_approve,R.id.layout_back,R.id.hide_soft_key})
    void onClick(View view){
        switch (view.getId()){
            case R.id.layout_back:
                onBackPressed();
                break;
            case R.id.approve_camera:
                if (mPicChsDialog != null)
                    mPicChsDialog.show();
                break;

            case R.id.upload_approve:
                if ("".equals(mNameEdit.getText().toString().trim())){
                    UIUtils.showToast("请输入姓名");
                    break;
                }
                if ("".equals(mIdCardEdit.getText().toString().trim())){
                    UIUtils.showToast("请输入身份证号");
                    break;
                }
                if (isGetImageSuccess && path != null && !"".equals(path)){
                    initProgressDialog();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog != null && dialog.isShowing()){
                                dialog.dismiss();
                                UIUtils.showToast("上传失败");
                            }
                        }
                    },10000);

                    uploadHelper.uploadCover(path);
                }else{
                    UIUtils.showToast("请选择图片");
                }

                break;
        }
    }

    private void uploadApprove(String id){
        LiveRequestParams params = new LiveRequestParams();
        params.put("name",mNameEdit.getText().toString().trim());
        params.put("id_card",mIdCardEdit.getText().toString().trim());
        params.put("id_img",id);
        userProtocol.getUserApprove(params).observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size()>0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                ApproveSuccess.goApproveSuccess(Approve.this);
                                finish();
                            }else{
                                UIUtils.showToast(map.get("msg"));
                            }

                        }else{
                            UIUtils.showToast(R.string.net_error);
                        }
                        if (dialog != null && dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast(R.string.net_error);
                        if (dialog != null && dialog.isShowing()){
                            dialog.dismiss();
                        }
                    }
                });
    }

    /**
     *  获取TakePhoto实例
     * @return
     */
    public TakePhoto getTakePhoto(){
        if (takePhoto==null){
            takePhoto= (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this,this));
        }
        return takePhoto;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    /**
     * 图片选择对话框
     */
    private void initPhotoDialog() {
        mPicChsDialog = new Dialog(this, R.style.floag_dialog);
        mPicChsDialog.setContentView(R.layout.dialog_pic_choose);

        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        Window dlgwin = mPicChsDialog.getWindow();
        WindowManager.LayoutParams lp = dlgwin.getAttributes();
        dlgwin.setGravity(Gravity.BOTTOM);
        lp.width = (int) (display.getWidth()); //设置宽度

        mPicChsDialog.getWindow().setAttributes(lp);

        TextView camera = (TextView) mPicChsDialog.findViewById(R.id.chos_camera);
        TextView picLib = (TextView) mPicChsDialog.findViewById(R.id.pic_lib);
        TextView cancel = (TextView) mPicChsDialog.findViewById(R.id.btn_cancel);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(CAPTURE_IMAGE_CAMERA);
                mPicChsDialog.dismiss();
            }
        });

        picLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicFrom(IMAGE_STORE);
                mPicChsDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPicChsDialog.dismiss();
            }
        });
    }

    private MyProgressDialog dialog;
    private void initProgressDialog() {
        dialog = new MyProgressDialog(this,"照片上传中");
        dialog.setCancelable(false);
        dialog.show();

    }

    @Override
    public void takeSuccess(TResult result) {
        ArrayList<TImage> images = result.getImages();
        if (images.size()>0){
            TImage tImage = images.get(0);
            path = tImage.getOriginalPath();
            if (TextUtils.isEmpty(path)){
                return;
            }
            mApproveCsmera.setImageBitmap(null);
            mApproveCsmera.setImageURI(Uri.parse(path));
            isGetImageSuccess = true;
        }
    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

    }

    @Override
    public void onUploadResult(int code, Object obj) {
        if (0 == code) {
            FileInfo fileInfo = (FileInfo) obj;
            String fileId = fileInfo.fileId;

            uploadApprove(fileId);
//            setUser(SPUtils.getString(SPUtils.USER_AVATAR_ID));
        } else {
            Toast.makeText(this, "上传封面失败", Toast.LENGTH_SHORT).show();
            Log.d("livelog","错误码+"+code);
        }
        if (dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    /**
     * 获取图片资源
     *
     * @param type 类型（本地IMAGE_STORE/拍照CAPTURE_IMAGE_CAMERA）
     */
    private void getPicFrom(int type) {
//        if (!mPermission) {
//            Toast.makeText(this, getString(R.string.tip_no_permission), Toast.LENGTH_SHORT).show();
//            return;
//        }
        switch (type) {
            //拍照
            case CAPTURE_IMAGE_CAMERA:
                fileUri = createCoverUri("");

                getTakePhoto();
                //是否压缩
                takePhoto.onEnableCompress(null,false);
                //是否裁剪
//                File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
//                if (!file.getParentFile().exists())file.getParentFile().mkdirs();
//                Uri imageUri = Uri.fromFile(file);
                //相册
//                takePhoto.onPickFromGalleryWithCrop(fileUri,getCropOptions());

                //拍照
                takePhoto.onPickFromCaptureWithCrop(fileUri,getCropOptions());

//                Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                startActivityForResult(intent_photo, CAPTURE_IMAGE_CAMERA);
                break;
            //本地
            case IMAGE_STORE:
                fileUri = createCoverUri("_select");

                getTakePhoto();
                //是否压缩
                takePhoto.onEnableCompress(null,false);
                //是否裁剪
//                File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
//                if (!file.getParentFile().exists())file.getParentFile().mkdirs();
//                Uri imageUri = Uri.fromFile(file);
                //相册
                takePhoto.onPickFromGalleryWithCrop(fileUri,getCropOptions());

                //拍照
//                takePhoto.onPickFromCaptureWithCrop(fileUri,getCropOptions());


//                Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
//                intent_album.setType("image/*");
//                startActivityForResult(intent_album, IMAGE_STORE);
                break;

        }
    }

    private CropOptions getCropOptions(){
        //不裁剪
//        if(rgCrop.getCheckedRadioButtonId()!=R.id.rbCropYes)return null;
        int height= 800;//Integer.parseInt(etCropHeight.getText().toString());
        int width= 800;//Integer.parseInt(etCropWidth.getText().toString());
        boolean withWonCrop= false;//rgCropTool.getCheckedRadioButtonId()==R.id.rbCropOwn? true:false;

        CropOptions.Builder builder=new CropOptions.Builder();

//        if(rgCropSize.getCheckedRadioButtonId()==R.id.rbAspect){
//            builder.setAspectX(width).setAspectY(height);
//        }else {
        builder.setOutputX(width).setOutputY(height);
        builder.setAspectX(1).setAspectY(1);
//        }
        builder.setWithOwnCrop(withWonCrop);
        return builder.create();
    }

    private Uri createCoverUri(String type) {
        String filename = SPUtils.getString(SPUtils.USER_IDENTITY) + type + ".jpg";
        File outputImage = new File(Environment.getExternalStorageDirectory(), filename);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, TCConstants.WRITE_PERMISSION_REQ_CODE);
            return null;
        }
        if (outputImage.exists()) {
            outputImage.delete();
        }

        return Uri.fromFile(outputImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("livelog","UMENG ActivityResult");
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type=PermissionManager.checkPermission(TContextWrap.of(this),invokeParam.getMethod());
        if(PermissionManager.TPermissionType.WAIT.equals(type)){
            this.invokeParam=invokeParam;
        }
        return type;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type=PermissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
        PermissionManager.handlePermissionsResult(this,type,invokeParam,this);
    }
}
