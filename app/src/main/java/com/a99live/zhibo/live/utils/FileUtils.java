package com.a99live.zhibo.live.utils;

import android.os.Environment;
import android.text.TextUtils;

import com.a99live.zhibo.live.LiveZhiBoApplication;
import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by fuyk on 2016/9/2.
 */
public class FileUtils {

    private static final String ROOT_DIR = "99zhibo";

    public static String getLogDir() {
        return getDir("log");
    }

    public static String getDownloadDir() {
        return getDir("download");
    }

    public static String getPicDir() {
        return getDir("cache" + File.separator + "pic");
    }

    public static String getPicClipDir() {
        return getDir("clip_pic");
    }

    public static String getApkFilePath() {
        return getDir("") + File.separator + "99live.apk";
    }
    public static String getSDDir(){
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/99live" +"/";
    }

    /**
     * 根据手机状�?自动挑�?存储介质（SD or 手机内部�?
     *
     * @param string
     * @return
     */
    private static String getDir(String string) {
        if (isSDAvailable()) {
            return getSDDir(string);
        } else {
            return getDataDir(string);
        }
    }

    /**
     * 判断sd卡是否可以用
     *
     * @return
     */
    private static boolean isSDAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED) ? true : false;
    }

    /**
     * 获取到手机内存的目录
     */
    private static String getDataDir(String string) {
        // data/data/包名/cache
        String path = LiveZhiBoApplication.getApp().getCacheDir()
                .getAbsolutePath()
                + File.separator + string;
        File file = new File(path);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return file.getAbsolutePath();
            } else {
                return "";
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * 获取到sd卡的目录
     *
     * @param key_dir
     * @return
     */
    private static String getSDDir(String key_dir) {
        StringBuilder sb = new StringBuilder();
        String absolutePath = getExternalStorageDirectory()
                .getAbsolutePath();// /mnt/sdcard
        sb.append(absolutePath);
        sb.append(File.separator).append(ROOT_DIR).append(File.separator)
                .append(key_dir);

        String filePath = sb.toString();
        File file = new File(filePath);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return file.getAbsolutePath();
            } else {
                return "";
            }
        }

        return file.getAbsolutePath();
    }

    /**
     * 删除文件或目录
     */
    public static void deleteAll(File file) {
        if (file.isFile() || file.list().length == 0) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteAll(files[i]);
                files[i].delete();
            }

            if (file.exists()) { // 如果文件本身就是目录 ，就要删除目�?
                file.delete();
            }
        }
    }

    /**
     * 读取指定路径缓存
     * /F
     */
    public static String readCache(String filePath) {
        String cache = null;
        File cacheFile = new File(filePath);
        if (cacheFile.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(filePath));
                StringBuilder sb = new StringBuilder();
                String temp;
                while ((temp = reader.readLine()) != null) {
                    sb.append(temp);
                }
                cache = sb.toString();
                reader.close();
            } catch (Exception e) {
//                XgoLog.e(e);
            }


            if (TextUtils.isEmpty(cache)) {
//                XgoLog.d("***NOT CACHE***");
            } else {
//                XgoLog.d("***READ CACHE***\r\n" + cache);
            }
        }
        return cache;
    }


    /**
     * 将缓存写入指定文件
     */
    public static Boolean writeCache(String filePath, String cache) {
//        XgoLog.d("writeCache::" + cache);
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            writer.write(cache.toCharArray());
            writer.flush();
            writer.close();
            return true;
        } catch (Exception e) {
        }
        return false;
    }


    public static String getCacheSize() {
        long cacheSize = getFolderSize(new File(getDataDir("")));
        long externalCacheSize = 0;
        if (isSDAvailable()) {
            externalCacheSize = getFolderSize(new File(getSDDir("")));
        }

        return getFormatSize(cacheSize + externalCacheSize);
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        File cacheFile = new File(getDataDir(""));
        if (cacheFile.exists()) {
            deleteAll(cacheFile);
        }

        if (isSDAvailable()) {
            File sdCacheFile = new File(getSDDir(""));
            if (sdCacheFile.exists()) {
                deleteAll(sdCacheFile);
            }
        }

        //Glide图片缓存目录
        File photoCacheDir = Glide.getPhotoCacheDir(UIUtils.getContext());
        if (photoCacheDir.exists()) {
            deleteAll(photoCacheDir);
        }
    }

    /**
     * 获取文件大小
     *
     * @param file
     * @return
     */
    public static long getFolderSize(File file) {
        long size = 0;
        if (file != null && file.exists()) {
            try {
                File[] fileList = file.listFiles();
                for (int i = 0; i < fileList.length; i++) {
                    // 如果下面还有文件
                    if (fileList[i].isDirectory()) {
                        size = size + getFolderSize(fileList[i]);
                    } else {
                        size = size + fileList[i].length();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return size;
    }

    /**
     * 格式化文件大小
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        if (0 == size) {
            return "0K";
        }

        double kiloByte = size / 1024;
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .stripTrailingZeros().toPlainString()
                    + "K";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .stripTrailingZeros().toPlainString()
                    + "M";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .stripTrailingZeros().toPlainString()
                    + "G";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP)
                .stripTrailingZeros().toPlainString()
                + "T";
    }
}
