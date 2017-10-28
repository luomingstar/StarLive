package com.a99live.zhibo.live.activity.user;

import android.Manifest;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
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
import com.a99live.zhibo.live.utils.TimeUtils;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.MyProgressDialog;
import com.a99live.zhibo.live.view.weight.NewCircleImageView;
import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jph.takephoto.app.TakePhoto;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.AddressPicker;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.util.ConvertUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by fuyk on 2016/9/27.
 */
public class UserInfoActivity extends BaseActivity implements UploadHelper.OnUploadListener ,TakePhoto.TakeResultListener,InvokeListener {

    private UserProtocol userProtocol;
    private UploadHelper uploadHelper;

    /**图片选择框*/
    private Dialog mPicChsDialog;
    private static final int CAPTURE_IMAGE_CAMERA = 300;
    private static final int IMAGE_STORE = 400;
    private static final int CROP_CHOOSE = 10;

    /**昵称和个人签名*/
    private static final int MODIFY_NICK_NAME = 100;
    private static final int MODIFY_SINGLE = 200;
    //权限
    private boolean mPermission = false;

    /**年月日*/
    private int year;
    private int month;
    private int day;

    private Uri fileUri, cropUri;

    private TakePhoto takePhoto;
    private InvokeParam invokeParam;

    /**标头*/
    @Bind(R.id.tv_title)
    TextView tv_title;
    /**99号*/
    @Bind(R.id.tv_id_num)
    TextView tv_id_num;
    /**手机号*/
    @Bind(R.id.tv_phone_num)
    TextView tv_phone_num;
    /**头像*/
    @Bind(R.id.iv_head)
    NewCircleImageView mHeadPhoto;
    /**昵称*/
    @Bind(R.id.tv_nick_name)
    TextView tv_nick_name;
    /**性别*/
    @Bind(R.id.tv_sex_name)
    TextView tv_sex_name;
    /**出生日期*/
    @Bind(R.id.tv_birth_date)
    TextView tv_birth_date;
    /**星座*/
    @Bind(R.id.tv_constellation_name)
    TextView tv_constellation_name;
    /**所在地*/
    @Bind(R.id.tv_address_name)
    TextView tv_address;
    /**签名*/
    @Bind(R.id.tv_motto_name)
    TextView tv_motto_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getTakePhoto().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);
        ButterKnife.bind(this);
        initView();
        initData();
        initPhotoDialog();
        //检测权限
        mPermission = checkPublishPermission();
    }

    /**获取TakePhoto实例*/
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

    private void initData() {
        userProtocol = new UserProtocol();
        uploadHelper = new UploadHelper(this, this);

        //初始化Calendar日历对象
        Calendar mycalendar = Calendar.getInstance(Locale.CHINA);
        Date mydate = new Date(); //获取当前日期Date对象
        mycalendar.setTime(mydate);////为Calendar对象设置时间为当前日期

        year = mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
        month = mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
        day = mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天
//        tv_birth_date.setText(year + "-" + (month + 1) + "-" + day); //显示当前的年月日
    }

    /**初始化界面*/
    private void initView() {
        tv_title.setText(R.string.single_data);

        tv_id_num.setText(SPUtils.getString(SPUtils.USER_IDENTITY));
        tv_phone_num.setText(SPUtils.getString(SPUtils.USER_MOBILE));
        Glide.with(this)
                .load(SPUtils.getString(SPUtils.USER_AVATAR))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .centerCrop()
                .placeholder(R.mipmap.head)
                .dontAnimate()
                .into(mHeadPhoto);
        tv_nick_name.setText(SPUtils.getString(SPUtils.USER_NAME));
        tv_sex_name.setText(SPUtils.getString(SPUtils.USER_GENDER));
        tv_birth_date.setText(SPUtils.getString(SPUtils.USER_BIRTHDAY));
        tv_constellation_name.setText(SPUtils.getString(SPUtils.USER_STAR));
        tv_motto_name.setText(SPUtils.getString(SPUtils.USER_SIGN));
        tv_address.setText(SPUtils.getString(SPUtils.USER_REGION));
    }

    /**修改用户资料*/
    public void setUser(final String key, final String value) {
        LiveRequestParams params = new LiveRequestParams();
        params.put(key, value);

        userProtocol.SetUserInfo(params)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ArrayList<Map<String, String>> listMapByJson = JsonUtil.getListMapByJson(s);
                        if (listMapByJson.size() > 0){
                            Map<String, String> map = listMapByJson.get(0);
                            if ("0".equals(map.get("code"))){
                                Log.d("livelog", "修改用户资料成功" + s);
                                if ("nickname".equals(key)) {
                                    tv_nick_name.setText(value);
                                    SPUtils.putString(SPUtils.USER_NAME, value);
                                }
                            }else {
                                UIUtils.showToast(map.get("msg"));
                            }
                        }else {
                            UIUtils.showToast("修改资料失败");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        UIUtils.showToast("修改资料失败");
                    }
                });
    }

    /**点击事件：返回，头像，昵称，性别，出生日期，星座，所在地，个性签名*/
    @OnClick({R.id.layout_back, R.id.layout_logo_head, R.id.layout_nick_name, R.id.layout_sex, R.id.layout_birth, R.id.layout_star, R.id.layout_address, R.id.layout_single_motto})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_back:
                back();
                break;
            case R.id.layout_logo_head:
                mPicChsDialog.show();
                break;
            case R.id.layout_nick_name:
                goNickNameActivity();
                break;
            case R.id.layout_sex:
                onOptionPicker();
                break;
            case R.id.layout_birth:
                String s = tv_birth_date.getText().toString();
                showDate(s);
                break;
            case R.id.layout_star:
                onConstellationPicker();
                break;
            case R.id.layout_address:
                onAddressPicker();
                break;
            case R.id.layout_single_motto:
                goSingleMottoActivity();
                break;
        }
    }

    /**性别*/
    public void onOptionPicker() {
        OptionPicker picker = new OptionPicker(this, new String[]{
                "男", "女"
        });
        picker.setHeight(640);
        picker.setOffset(2);
        picker.setSelectedIndex(1);
        picker.setTextSize(18);
        picker.setTextColor(0xFFFF0000, 0xFF999999);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int position, String option) {
                tv_sex_name.setText(option);
                setUser("gender", String.valueOf(position + 1));
                if ((position+1) == 1){
                    SPUtils.putString(SPUtils.USER_GENDER, "男");
                }else {
                    SPUtils.putString(SPUtils.USER_GENDER, "女");
                }
            }
        });
        picker.show();
    }

    /**选择星座*/
    private void onConstellationPicker() {
        OptionPicker picker = new OptionPicker(this, new String[]{
                "水瓶", "双鱼", "白羊", "金牛", "双子", "巨蟹", "狮子", "处女", "天秤", "天蝎", "射手", "摩羯",
        });
        picker.setTopBackgroundColor(0xFFEEEEEE);
        picker.setLineVisible(true);
        picker.setTopHeight(42);
        picker.setTitleText("请选择");
        picker.setTitleTextColor(0xFF999999);
        picker.setTitleTextSize(20);
        picker.setCancelTextColor(0xFF33B5E5);
        picker.setCancelTextSize(18);
        picker.setSubmitTextColor(0xFF33B5E5);
        picker.setSubmitTextSize(18);
        picker.setTextColor(0xFFFF0000, 0xFF999999);
        picker.setLineColor(0xFFEE0000);
        picker.setSelectedItem("水瓶");
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int position, String option) {
                tv_constellation_name.setText(option);
                setUser("star", String.valueOf(position+1));
                SPUtils.putString(SPUtils.USER_STAR, option);
            }
        });
        picker.show();
    }

    /**选择所在地*/
    public void onAddressPicker() {
        try {
            ArrayList<Province> data = new ArrayList<Province>();
            String json = ConvertUtils.toString(getAssets().open("city.json"));

            data.addAll(JSON.parseArray(json, Province.class));
            AddressPicker picker = new AddressPicker(this, data);
            picker.setSelectedItem("北京", "北京", "海淀区");
            picker.setOnAddressPickListener(new AddressPicker.OnAddressPickListener() {
                @Override
                public void onAddressPicked(Province province, City city, County county) {
                    if (county == null) {
                        tv_address.setText(province.getAreaName() + " " + city.getAreaName() +" " +county.getAreaName());
                        setUser("region",city.getAreaId());
                    } else {
                        tv_address.setText(province.getAreaName() + " " + city.getAreaName() +" " +county.getAreaName());
                        setUser("region",county.getAreaId());
                    }
                }
            });
            picker.show();
        } catch (Exception e) {

        }
    }

    /**选择出生日期*/
    private void showDate(String s) {
        DatePickerDialog dpd = new DatePickerDialog(this, Datelistener, 1970, 00, 01);
        String[] split = s.split("-");
        if (split.length==3){
            dpd.updateDate(Integer.parseInt(split[0]),Integer.parseInt(split[1])-1,Integer.parseInt(split[2]));
        }
        try {
            dpd.getDatePicker().setMinDate(TimeUtils.stringToLong("1930年01月01日","yyyy年MM月dd日"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpd.show();//显示DatePickerDialog组件
    }

    private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener() {
        /**params：view：该事件关联的组件
         * params：myyear：当前选择的年
         * params：monthOfYear：当前选择的月
         * params：dayOfMonth：当前选择的日
         */
        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear, int dayOfMonth) {
            year = myyear;
            month = monthOfYear;
            day = dayOfMonth;
            updateDate();
        }

        private void updateDate() {
            String curData = TimeUtils.getHMS(System.currentTimeMillis());
            String birthday = year + "-" + (month + 1) + "-" + day;
            String dataParmas = birthday + " " + curData;
            Log.d("livelog", "birthday=" + curData);
            setUser("birthday", dataParmas);
            tv_birth_date.setText(birthday);
            SPUtils.putString(SPUtils.USER_BIRTHDAY, birthday);
        }
    };

    /**修改个性签名*/
    private void goSingleMottoActivity() {
        Intent intent = new Intent(this, SingleMottoActivity.class);
        intent.putExtra("single_motto", tv_motto_name.getText().toString());
        startActivityForResult(intent, MODIFY_SINGLE);
        overridePendingTransition(R.anim.tran_right_in, R.anim.scale_small_out);
    }

    /**修改昵称*/
    private void goNickNameActivity() {
        Intent intent = new Intent(this, NickNameActivity.class);
        intent.putExtra("pet_name", tv_nick_name.getText().toString());
        startActivityForResult(intent, MODIFY_NICK_NAME);
        overridePendingTransition(R.anim.tran_right_in, R.anim.scale_small_out);
    }

    /**图片选择对话框*/
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

    /**
     * 获取图片资源
     * @param type 类型（本地IMAGE_STORE/拍照CAPTURE_IMAGE_CAMERA）
     */
    private void getPicFrom(int type) {
        if (!mPermission) {
            Toast.makeText(this, getString(R.string.tip_no_permission), Toast.LENGTH_SHORT).show();
            return;
        }
        switch (type) {
            //拍照
            case CAPTURE_IMAGE_CAMERA:
                fileUri = createCoverUri("");

                getTakePhoto();
                //设置是否使用takephoto 自带相册
//        takePhoto.setTakePhotoOptions(new TakePhotoOptions.Builder().setWithOwnGallery(true).create());
                //是否压缩
                takePhoto.onEnableCompress(null,false);
                //是否裁剪
//                File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
//                if (!file.getParentFile().exists())file.getParentFile().mkdirs();
//                Uri imageUri = Uri.fromFile(file);
                //相册
//                takePhoto.onPickFromGalleryWithCrop(fileUri,getCropOptions());

                //拍照
                takePhoto.onPickFromCaptureWithCrop(fileUri, getCropOptions());
//                Intent intent_photo = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent_photo.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                startActivityForResult(intent_photo, CAPTURE_IMAGE_CAMERA);
                break;
            //本地
            case IMAGE_STORE:
                fileUri = createCoverUri("_select");

                getTakePhoto();
                //设置是否使用takephoto 自带相册
//        takePhoto.setTakePhotoOptions(new TakePhotoOptions.Builder().setWithOwnGallery(true).create());
                //是否压缩
                takePhoto.onEnableCompress(null,false);
                //是否裁剪
//                File file=new File(Environment.getExternalStorageDirectory(), "/temp/"+System.currentTimeMillis() + ".jpg");
//                if (!file.getParentFile().exists())file.getParentFile().mkdirs();
//                Uri imageUri = Uri.fromFile(file);
                //相册
                takePhoto.onPickFromGalleryWithCrop(fileUri, getCropOptions());

                //拍照
//                takePhoto.onPickFromCaptureWithCrop(fileUri,getCropOptions());

//                Intent intent_album = new Intent("android.intent.action.GET_CONTENT");
//                intent_album.setType("image/*");
//                startActivityForResult(intent_album, IMAGE_STORE);

//                Intent intent = new Intent(Intent.ACTION_PICK, null);
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        "image/*");
//                startActivityForResult(intent,IMAGE_STORE);
                break;

        }
    }

    private CropOptions getCropOptions(){
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
        if (ContextCompat.checkSelfPermission(UserInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UserInfoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, TCConstants.WRITE_PERMISSION_REQ_CODE);
            return null;
        }
        if (outputImage.exists()) {
            outputImage.delete();
        }

        return Uri.fromFile(outputImage);
    }

    /**照片提示框*/
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //修改昵称
                case MODIFY_NICK_NAME:
                    if (data != null) {
                        Bundle MarsBuddle = data.getExtras();
                        String MarsMessage = MarsBuddle.getString("petName");
                        setUser("nickname", MarsMessage);
                    }
                    break;
                //修改签名
                case MODIFY_SINGLE:
                    if (data != null) {
                        Bundle MarsBuddle = data.getExtras();
                        String MarsMessage = MarsBuddle.getString("single_motto");
                        setUser("sign", MarsMessage);
                        tv_motto_name.setText(MarsMessage);
                        SPUtils.putString(SPUtils.USER_SIGN, MarsMessage);
                    }
                    break;
            }
        }
    }

    /**检查权限*/
    private boolean checkPublishPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            List<String> permissions = new ArrayList<>();
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(UserInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(UserInfoActivity.this, Manifest.permission.CAMERA)) {
                permissions.add(Manifest.permission.CAMERA);
            }
            if (PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(UserInfoActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (permissions.size() != 0) {
                ActivityCompat.requestPermissions(UserInfoActivity.this,
                        permissions.toArray(new String[permissions.size()]),
                        TCConstants.WRITE_PERMISSION_REQ_CODE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type=PermissionManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
        PermissionManager.handlePermissionsResult(this,type,invokeParam,this);
        switch (requestCode){
            case TCConstants.WRITE_PERMISSION_REQ_CODE:
                if (grantResults.length > 0) {
                    for (int i=0;i<grantResults.length;i++){
                        if (grantResults[i]==PackageManager.PERMISSION_GRANTED){
                            mPermission = true;
                        }else{
                            mPermission = false;
                            break;
                        }
                    }
                } else {

                }
                break;
        }
    }

    /**
     * 图片上传的回调
     * @param code：0成功
     * @param obj
     */
    @Override
    public void onUploadResult(int code, Object obj) {
        if (0 == code) {
            FileInfo fileInfo = (FileInfo) obj;
            String fileId = fileInfo.fileId;
            SPUtils.putString(SPUtils.USER_AVATAR_ID, fileId);
            Toast.makeText(this, "上传封面成功", Toast.LENGTH_SHORT).show();
            setUser("avatar", fileId);
        } else {
            Toast.makeText(this, "上传封面失败", Toast.LENGTH_SHORT).show();
        }
        if (dialog != null){
            dialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        back();
    }

    private void back() {
        overridePendingTransition(R.anim.scale_big_in, R.anim.tran_right_out);
        finish();
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
            mHeadPhoto.setImageBitmap(null);
            mHeadPhoto.setImageURI(Uri.parse(path));

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

}
