package com.a99live.zhibo.live.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.a99live.zhibo.live.LiveZhiBoApplication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UIUtils {


    /**
     * @param
     * @param id	dimens文件中的id(仅适用于dp)
     * @return dimen 对应分辨率的dp或者sp值
     */
    public static int getDimen(int id){
        float dimen=0;
        String string=getContext().getResources().getString(id).replace("dip", "");
        dimen=Float.parseFloat(string);
        return dp2px(dimen);
    }
    /*
    * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
    */
    public static int dp2px( float dpValue) {
        int res = 0;
        final float scale = getContext().getResources().getDisplayMetrics().density;
        if (dpValue < 0)
            res = -(int) (-dpValue * scale + 0.5f);
        else
            res = (int) (dpValue * scale + 0.5f);
        return res;
    }
    /**
     * 全局上下文环境
     *
     * @return
     */
    public static Context getContext() {
        return LiveZhiBoApplication.getApp();
    }

    /**
     * 获取屏幕宽度
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static int getScreenWidth() {
        WindowManager wm = ((WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            return size.x;
        } else {
            Display d = wm.getDefaultDisplay();
            return d.getWidth();
        }
    }

    /**
     * 获取屏幕高度
     */
    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static int getScreenHeight() {
        WindowManager wm = ((WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            return size.y;
        } else {
            Display d = wm.getDefaultDisplay();
            return d.getHeight();
        }
    }

    /**
     * 根据原图绘制圆形图片
     */
    static public Bitmap createCircleImage(Bitmap source, int min){
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        if (0 == min){
            min = source.getHeight()>source.getWidth() ? source.getWidth() : source.getHeight();
        }
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        // 创建画布
        Canvas canvas = new Canvas(target);
        // 绘圆
        canvas.drawCircle(min/2, min/2, min/2, paint);
        // 设置交叉模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // 绘制图片
        canvas.drawBitmap(source, 0, 0, paint);
        return target;
    }

    /**
     * 字符串截断
     * @param source
     * @param length
     * @return
     */
    public static String getLimitString(String source, int length){
        if (null!=source && source.length()>length){
            int reallen = 0;
            return source.substring(0, length)+"...";
        }
        return source;
    }

    /**
     * dp转px
     *
     * @param dip
     * @return
     */
    public static int dip2px(float dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * px转换dip
     */

    public static int px2dip(float px) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static int px2sp(float pxValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(float spValue) {
        final float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 吐司 String
     *
     * @param msg
     */
    public static void showToast(String msg, int longTimeType) {
        if (longTimeType == Toast.LENGTH_LONG) {
            Toast.makeText(getContext(), msg, longTimeType).show();
        } else {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showToast(int resId) {
        showToast(resId, 0);
    }

    public static void showToast(String msg) {
        showToast(msg, 0);
    }


    /**
     * 吐司 ResourcesId
     *
     * @param resId
     */
    public static void showToast(int resId, int longTimeType) {
        if (longTimeType == Toast.LENGTH_LONG) {
            Toast.makeText(getContext(), getString(resId), longTimeType).show();
        } else {
            Toast.makeText(getContext(), getString(resId), Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public static void runOnUiThread(Runnable runnable) {
        if (isOnMainThread()) {
            runnable.run();
        } else {
            execute(runnable);
        }
    }

    public static void execute(Runnable runnable) {
        LiveZhiBoApplication.getHandler().post(runnable);
    }

    /**
     * 判断程序是否在主线程运行
     */
    private static boolean isOnMainThread() {
        // 首先获取到主线程的tid == 再判断当前线程的tid
        return LiveZhiBoApplication.getMainThreadId() == android.os.Process.myTid();
    }

    /**
     * getResources
     *
     * @return Resources
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 通过资源id获取对应String数组
     *
     * @param id
     * @return
     */
    public static String[] getStringArray(int id) {
        return getResources().getStringArray(id);
    }

    /**
     * 通过资源id获取对应Int数组
     *
     * @param id
     * @return
     */
    public static int[] getIntegerArray(int id) {
        return getResources().getIntArray(id);
    }

    /**
     * 通过资源id获取对应String
     *
     * @param id
     * @return
     */
    public static String getString(int id) {
        return getResources().getString(id);
    }

    /**
     * 通过资源id获取对应颜色
     *
     * @param id
     * @return
     */
    public static int getColor(int id) {
        return getResources().getColor(id);
    }

    public static Drawable getDrawable(int id) {
        return getResources().getDrawable(id);
    }

    /**
     * 获取View的缩略图
     *
     * @param view
     * @return ImageView
     */
    public static ImageView getDrawingCacheView(View view) {
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(true);
        Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        ImageView iv = new ImageView(getContext());
        iv.setImageBitmap(cache);
        return iv;
    }

    /**
     * 手机号验证
     *
     * @param mobiles
     * @return boolean
     */
    public static boolean isMobileNO(String mobiles) {
        String telRegex = "[1]\\d{10}";
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * 对文本进行编码
     * @param s  文本
     * @param bm 编码格式 utf-8
     *
     1. +  URL 中+号表示空格 %2B

    2. 空格 URL中的空格可以用+号或者编码 %20

    3. /  分隔目录和子目录 %2F

    4. ?  分隔实际的 URL 和参数 %3F

    5. % 指定特殊字符 %25

    6. # 表示书签 %23

    7. & URL 中指定的参数间的分隔符 %26

    8. = URL 中指定参数的值 %3D
     */
    public static String encode(String s, String bm) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0;i<s.length();i++){
            char c = s.charAt(i);
            switch (c){
                case '+':
                    sb.append("%2B");
                    break;
                case ' ':
                    sb.append("%20");
                    break;
                case '/':
                    sb.append("%2F");
                    break;
                case '?':
                    sb.append("%3F");
                    break;
                case '%':
                    sb.append("%25");
                    break;
                case '#':
                    sb.append("%23");
                    break;
//                case '&':
//                    sb.append("%26");
//                    break;
                case '=':
                    sb.append("%3D");
                    break;
                case '$':
                    sb.append("%24");
                    break;
                case '@':
                    sb.append("%40");
                    break;
                case '*':
                    sb.append("%2A");
                    break;
                case '~':
                    sb.append("~");
                    break;
                default:
                    try {
                        sb.append(URLEncoder.encode(String.valueOf(c),bm));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    break;

            }
//            if ("+".equals(c)){
//                sb.append("%2B");
//            }else if(" ".equals(c)){
//                sb.append("%20");
//            }else if("/".equals(c)){
//                sb.append("%2F");
//            }else if("?".equals(c)){
//                sb.append("%3F");
//            }else if("%".equals(c)){
//                sb.append("%25");
//            }else if("#".equals(c)){
//                sb.append("%23");
//            }else if("&".equals(c)){
//                sb.append("%26");
//            }else if("=".equals(c)){
//                sb.append("%3D");
//            }else if("$".equals(c)){
//                sb.append("%24");
//            }else if("*".equals(c)){
//                sb.append("%2A");
//            }else if("@".equals(c)){
//                sb.append("%40");
//            }else if("~".equals(c)){
//                sb.append("~");
//            }else{
//                try {
//                    sb.append(URLEncoder.encode(c,bm));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        return sb.toString();
    }

    /**
     * 自定义土司
     */
//    public static void showCdtToast(int imgId, int strId) {
//        Toast toast = new Toast(getContext());
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.setDuration(Toast.LENGTH_SHORT);
//        View view = View.inflate(getContext(), R.layout.layout_toast, null);
//        ((ImageView) view.findViewById(R.id.iv_icon)).setImageResource(imgId);
//        ((TextView) view.findViewById(R.id.tv_msg)).setText(strId);
//        toast.setView(view);
//        toast.show();
//    }

}
