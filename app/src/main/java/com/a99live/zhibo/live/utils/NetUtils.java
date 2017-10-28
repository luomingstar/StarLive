package com.a99live.zhibo.live.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by JJGCW on 2017/4/10.
 */

public class NetUtils {
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    /**
     * Is the live streaming still available
     * @return is the live streaming is available
     */
    public static boolean isLiveStreamingAvailable() {
        // Todo: Please ask your app server, is the live streaming still available
        return true;
    }
}
