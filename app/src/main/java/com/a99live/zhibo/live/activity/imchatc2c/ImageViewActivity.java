package com.a99live.zhibo.live.activity.imchatc2c;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.a99live.zhibo.live.R;
import com.a99live.zhibo.live.utils.UIUtils;
import com.a99live.zhibo.live.view.weight.CircleProgressView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.File;
import java.io.IOException;

public class ImageViewActivity extends Activity {

    private ImageView imageView;
    private CircleProgressView progressView;

    public static void goImageViewActivityWithUrl(Context context, String url){
        Intent intent = new Intent(context, ImageViewActivity.class);
        intent.putExtra("imageurl",url);
        context.startActivity(intent);
    }

    public static void goImageViewActivityWithPath(Context context,String path){
        Intent intent = new Intent(context,ImageViewActivity.class);
        intent.putExtra("path",path);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_view);
        RelativeLayout root = (RelativeLayout) findViewById(R.id.root);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageView = (ImageView) findViewById(R.id.image);
        progressView = (CircleProgressView) findViewById(R.id.progress);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            if (bundle.containsKey("path")){
                String path = bundle.getString("path");
                showImage(path);
            }else if (bundle.containsKey("imageurl")){
                String imageurl = bundle.getString("imageurl");
                progressView.setVisibility(View.VISIBLE);
                progressView.spin();

//                try {
//                    Glide.with(this)
//                            .load(imageurl)
//                            .asBitmap()
//                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                            .get();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                }
                Glide.with(this)
                        .load(imageurl)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .fitCenter()
                        .crossFade()
                        .into(new GlideDrawableImageViewTarget(imageView){
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                super.onResourceReady(resource, animation);
                                progressView.stopSpinning();
                                progressView.setVisibility(View.GONE);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);
                                progressView.stopSpinning();
                                progressView.setVisibility(View.GONE);
                                UIUtils.showToast("加载图片失败");
                            }
                        });
//                        .into(new GlideDrawableImageViewTarget(imageView) {
//                            @Override
//                            public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
//                                super.onResourceReady(drawable, anim);
//                                //在这里添加一些图片加载完成的操作
//                            };
            }else{
                String file = getIntent().getStringExtra("filename");

                Bitmap bitmap = getImage(FileUtil.getCacheFilePath(file));
                if (bitmap != null){
                    imageView.setImageBitmap(bitmap);
                }
            }


        }



    }

    private Bitmap getImage(String path){
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int reqWidth, reqHeight, width=options.outWidth, height=options.outHeight;
        if (width > height){
            reqWidth = getWindowManager().getDefaultDisplay().getWidth()/2;
            reqHeight = (reqWidth * height)/width;
        }else{
            reqHeight = getWindowManager().getDefaultDisplay().getHeight()/2;
            reqWidth = (width * reqHeight)/height;
        }
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        try{
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;
            Matrix mat = new Matrix();
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            if (bitmap == null) {
                Toast.makeText(this, getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
                return null;
            }
            ExifInterface ei =  new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch(orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    mat.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    mat.postRotate(180);
                    break;
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true);
        }catch (IOException e){
            return null;
        }
    }


    private void showImage(String path){
        if (path.equals("")) return;
        File file = new File(path);
        if (file.exists()){
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            if (file.length() == 0 && options.outWidth == 0) {
                finish();
                return;
            }
            long fileLength = file.length();
            if (fileLength == 0) {
                fileLength = options.outWidth*options.outHeight/3;
            }
            int reqWidth, reqHeight, width=options.outWidth, height=options.outHeight;
            if (width > height){
                reqWidth = getWindowManager().getDefaultDisplay().getWidth();
                reqHeight = (reqWidth * height)/width;
            }else{
                reqHeight = getWindowManager().getDefaultDisplay().getHeight();
                reqWidth = (width * reqHeight)/height;
            }
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {
                final int halfHeight = height / 2;
                final int halfWidth = width / 2;
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }
//            isOri.setText(getString(R.string.chat_image_preview_ori) + "(" + getFileSize(fileLength) + ")");
            try{
                options.inSampleSize = inSampleSize;
                options.inJustDecodeBounds = false;
                float scaleX = (float) reqWidth / (float) (width/inSampleSize);
                float scaleY = (float) reqHeight / (float) (height/inSampleSize);
                Matrix mat = new Matrix();
                mat.postScale(scaleX, scaleY);
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                ExifInterface ei =  new ExifInterface(path);
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch(orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        mat.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        mat.postRotate(180);
                        break;
                }
//                ImageView imageView = (ImageView) findViewById(R.id.image);
                imageView.setImageBitmap(Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true));
            }catch (IOException e){
                Toast.makeText(this, getString(R.string.chat_image_preview_load_err), Toast.LENGTH_SHORT).show();
            }
        }else{
            finish();
        }
    }

    private String getFileSize(long size){
        StringBuilder strSize = new StringBuilder();
        if (size < 1024){
            strSize.append(size).append("B");
        }else if (size < 1024*1024){
            strSize.append(size/1024).append("K");
        }else{
            strSize.append(size/1024/1024).append("M");
        }
        return strSize.toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressView != null){
            progressView.stopSpinning();
        }
    }


}
