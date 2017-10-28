package com.a99live.zhibo.live.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.net.LiveRequestParams;
import com.a99live.zhibo.live.presenter.UploadHelper;
import com.a99live.zhibo.live.protocol.UserProtocol;
import com.a99live.zhibo.live.utils.JsonUtil;
import com.a99live.zhibo.live.utils.SPUtils;
import com.a99live.zhibo.live.utils.TCConstants;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.MyProgressDialog;
import com.a99live.zhibo.live.view.weight.NewCircleImageView;
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
import com.umeng.socialize.UMShareAPI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/10/26.
 */
public class SetNameActivity extends BaseActivity implements UploadHelper.OnUploadListener ,TakePhoto.TakeResultListener,InvokeListener {

    private UserProtocol userProtocol;

    private String sex;

    @Bind(R.id.et_nick_name)
    EditText et_nick_name;

    @Bind(R.id.rg_sex)
    RadioGroup rg_sex;

    @Bind(R.id.login_head)
    NewCircleImageView login_head;

    @Bind(R.id.tv_complete)
    TextView tv_complete;
    private Dialog mPicChsDialog;
    private static final int CAPTURE_IMAGE_CAMERA = 100;
    private static final int IMAGE_STORE = 200;
    private static final int CROP_CHOOSE = 10;
    private Uri fileUri, cropUri;
//    private boolean mPermission = false;
    private UploadHelper uploadHelper;
    private boolean isAvatarOk;


    private static final String TAG = TakePhotoActivity.class.getName();
    private TakePhoto takePhoto;
    private InvokeParam invokeParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);
        ButterKnife.bind(this);
        initData();
        initView();
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

    private void initData() {
        userProtocol = new UserProtocol();
        //检测推流权限
        checkPublishPermission();
        uploadHelper = new UploadHelper(this, this);
        initPhotoDialog();
    }

    private void initView() {
        sex = "1";
        SPUtils.putString(SPUtils.USER_GENDER, "男");
        rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int radioButtonId = radioGroup.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(radioButtonId);
                if (rb.getText().equals("男")){
                    sex = "1";
                    SPUtils.putString(SPUtils.USER_GENDER, "男");
                }else if (rb.getText().equals("女")){
                    sex = "2";
                    SPUtils.putString(SPUtils.USER_GENDER, "女");
                }
            }
        });
    }

    /**
     * 设置用戶昵称和性别
     */
    public void setUser(String name, String sex) {
        LiveRequestParams params = new LiveRequestParams();
        params.put("nickname", name);
        params.put("gender", sex);

        userProtocol.SetUserInfo(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("livelog", "修改用户资料成功" + s);
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                startActivity(new Intent(SetNameActivity.this, HomeActivity.class));
                                if (dialog != null)
                                    dialog.dismiss();
                                finish();
                                startActivity(new Intent(SetNameActivity.this, InterestActivity.class));
                                overridePendingTransition(R.anim.tran_bottom_in, R.anim.scale_small_out);
                            }else {
                                UIUtils.showToast(map.get("msg"));
                                if (dialog != null)
                                    dialog.dismiss();
                            }
                        }else{
                            UIUtils.showToast(s);
                            if (dialog != null)
                                dialog.dismiss();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast("修改用户资料失败,请重试");
                        Log.d("livelog", "修改用户资料失败");
                        if (dialog != null)
                            dialog.dismiss();
                    }
                });
    }

    @OnClick({ R.id.tv_complete , R.id.login_head })
    void onClick(View view){
        switch (view.getId()){
            case R.id.tv_complete:

                String name = et_nick_name.getText().toString().trim();
                if (!isAvatarOk){
                    UIUtils.showToast("请设置头像");
                }else if (name.length() == 0){
                    UIUtils.showToast("请输入昵称");
                }else if (name.length() > 12){
                    UIUtils.showToast("昵称不要超过12位哦~");
                } else {
                    getProgress();
                    setUser(name, sex);
                }
                break;

            case R.id.login_head:
                mPicChsDialog.show();
                break;
        }

    }

    private void getProgress(){
        dialog = new MyProgressDialog(this,"提交信息中");
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        UIUtils.showToast("请设置个人信息");
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

    /**
     * 修改用戶資料
     */
    public void setUser(String value) {
        LiveRequestParams params = new LiveRequestParams();
        params.put("avatar", value);

        userProtocol.SetUserInfo(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("livelog", "修改头像成功" + s);
                        UIUtils.showToast("设置头像成功");
                        isAvatarOk = true;
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.d("livelog", "修改头像失败");
                        UIUtils.showToast("服务器繁忙。设置头像失败");
                        isAvatarOk = false;
                    }
                });

    }

    @Override
    public void onUploadResult(int code, Object obj) {
        if (0 == code) {
            FileInfo fileInfo = (FileInfo) obj;
            String fileId = fileInfo.fileId;
            SPUtils.putString(SPUtils.USER_AVATAR_ID,fileId);
//            Toast.makeText(this, getString(R.string.publish_upload_success), Toast.LENGTH_SHORT).show();
            setUser(fileId);
        } else {
            Toast.makeText(this, "上传封面失败", Toast.LENGTH_SHORT).show();
            Log.d("livelog","错误码+"+code);
            isAvatarOk = false;
        }
        if (dialog != null){
            dialog.dismiss();
        }
    }

    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)) {
                permissions.add(Manifest.permission.RECORD_AUDIO);
            }
//            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
//                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
//            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(this,
                        permissions.toArray(new String[permissions.size()]),
                        TCConstants.WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
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
//        if (resultCode == RESULT_OK) {
//            switch (requestCode) {
//                //拍照
//                case CAPTURE_IMAGE_CAMERA:
//                    startPhotoZoom(fileUri);
//                    break;
//                //本地
//                case IMAGE_STORE:
//                    String path = TCUtils.getPath(this, data.getData());
//                    if (null != path) {
//                        Log.d("livelog", "startPhotoZoom->path:" + path);
//                        File file = new File(path);
//                        startPhotoZoom(Uri.fromFile(file));
//                    }
//                    break;
//                case CROP_CHOOSE:
//
//                    initProgressDialog();
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (dialog != null && dialog.isShowing()){
//                                dialog.dismiss();
//                                UIUtils.showToast("上传失败");
//                            }
//                        }
//                    },7000);
//
//                    login_head.setImageBitmap(null);
//                    login_head.setImageURI(cropUri);
//                    uploadHelper.uploadCover(cropUri.getPath());
//                    break;
//
//            }
//        }
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        Log.d("livelog","UMENG ActivityResult");
    }

    private MyProgressDialog dialog;
    private void initProgressDialog() {
        dialog = new MyProgressDialog(this,"封面上传中");
        dialog.setCancelable(false);
        dialog.show();

    }

    public void startPhotoZoom(Uri uri) {
        cropUri = createCoverUri("_crop");

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 750);
        intent.putExtra("aspectY", 750);
        intent.putExtra("outputX", 750);
        intent.putExtra("outputY", 750);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, CROP_CHOOSE);
    }

    @Override
    public void takeSuccess(TResult result) {

        ArrayList<TImage> images = result.getImages();
        if (images.size()>0){
            TImage tImage = images.get(0);
            String path = tImage.getOriginalPath();
            if (TextUtils.isEmpty(path)){
                return;
            }
            login_head.setImageBitmap(null);
            login_head.setImageURI(Uri.parse(path));

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
        }
    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    public void takeCancel() {

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
