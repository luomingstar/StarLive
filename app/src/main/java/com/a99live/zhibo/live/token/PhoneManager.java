package com.a99live.zhibo.live.token;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.a99live.zhibo.live.LiveZhiBoApplication;
import com.a99live.zhibo.live.utils.UIUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.UUID;

/**
 * Created by fuyk on 2016/9/1.
 */
public class PhoneManager extends Activity {

    public static final String XG_CODE = "1111";

    public static final String NETTYPE_WIFI = "WIFI";
    public static final String NETTYPE_CMWAP = "WAP";
    public static final String NETTYPE_CMNET = "NET";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);

        //手机型号
        String phoneType = android.os.Build.MODEL;
        //系统版本
        String systemType = android.os.Build.VERSION.RELEASE;

        /**
         * 电话方位
         */
        tm.getCellLocation();

    }

    /**
     * 获取手机UUID
     */
    public static String getMyUUID(Context mContext) {
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(mContext.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString().replaceAll("\\-", "");
        Log.d("debug", "uuid=" + uniqueId);
        return uniqueId;
    }

    /**
     * 获取版本号
     */
    public static int getVersionCode(Context mContext) {
        PackageManager packageManager = mContext.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 110;
    }

    /**
     * 获取版本号
     */
    public static String getVersionName(Context mContext) {
        PackageManager packageManager = mContext.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo;
        try {
            packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1.0.0";
    }
    /**
     * 获取当前网络类型
     * @return 0：没有网络   1：WIFI网络   2：WAP网络    3：NET网络
     */
    public static String getNetworkType() {
        String netType = "无网";
        ConnectivityManager connectivityManager = (ConnectivityManager) LiveZhiBoApplication.getApp().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!TextUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    /**
     * 获取友盟渠道号
     *
     * @param mContext
     * @return
     */
    public static String getChannel(Context mContext) {
        String channel = "wsc";
        try {
            ApplicationInfo appInfo;
            appInfo = mContext.getPackageManager().getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            channel = appInfo.metaData.getString("UMENG_CHANNEL");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel;
    }

    /**
     * 手机内存大小
     * @param context
     * @return
     */
    private static long getRamMemory(Context context) {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小

            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }

            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

        } catch (IOException e) {
        }
        //return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
        System.out.println("总运存--->>>" + initial_memory / (1024 * 1024));
        return initial_memory / (1024 * 1024);
    }

    /**
     * 获取包名
     * @param c
     * @return
     */
    public static String getPackageName(Context c) {
        return c.getPackageName();
    }

    /**
     * 获取经纬度
     */
    public static String getGeo() {
        LocationManager locationManager = (LocationManager) LiveZhiBoApplication.getApp().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        double latitude = 0;
        double longitude = 0;

        String strAddress = "";
        String strGPSStatus = "Disabled";
        //TextView txtOther = (TextView)findViewById(R.id.txtOther);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            strGPSStatus = "Enabled";
            if (ActivityCompat.checkSelfPermission(LiveZhiBoApplication.getApp(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LiveZhiBoApplication.getApp(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return "";
            }
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null){
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            } else {
                strAddress = "Can't located.";
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                } else {
                    strAddress = "Still can't located.";
                }
            }
        } else {
            strGPSStatus = "Disabled";
            strAddress = "something wrong.";
        }
        String station = "0#0";
        if (longitude != 0){
            DecimalFormat df = new DecimalFormat(".#####");
            String jingdu = df.format(longitude);
            String weidu = df.format(latitude);
            station = jingdu + "#" + weidu;
        }
        return station;
    }

    /**
     * 获取Device
     * @return
     */
    public static String getDeviceStr() {
        StringBuilder sb = new StringBuilder("android");
        sb.append("#").append(getPackageName(LiveZhiBoApplication.getApp()))
                .append("#").append(getVersionName(LiveZhiBoApplication.getApp()))
                .append("#").append(android.os.Build.VERSION.RELEASE)
                .append("#").append(android.os.Build.MODEL)
                .append("#").append(getRamMemory(LiveZhiBoApplication.getApp()))
                .append("#").append("wifi")
                .append("#").append(UIUtils.getScreenWidth())
                .append("#").append(UIUtils.getScreenHeight())
                .append("#").append(getChannel(LiveZhiBoApplication.getApp()));
        return sb.toString();
    }

    /**
     * 获取Token
     * @return
     */
    public static String getCookieInfo(){
        StringBuilder sb = new StringBuilder();
        sb.append("device=").append(PhoneManager.getDeviceStr()).append(";")
                .append("geo=").append(getGeo()).append(";")
                .append("acode=").append(getMyUUID(LiveZhiBoApplication.getApp())).append(";");
        return sb.toString();
    }
}
