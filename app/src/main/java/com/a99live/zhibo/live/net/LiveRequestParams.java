package com.a99live.zhibo.live.net;

import java.util.TreeMap;

/**
 * Created by fuyk on 2016/9/1.
 */
public class LiveRequestParams {


    private static final String TIMESTAMP = "t";


    private TreeMap<String, Object> mParams;


    public LiveRequestParams() {
        mParams = new TreeMap<>();
    }

    public void put(String key, Object value) {
        if (mParams != null) {
            mParams.put(key, value);
        }
    }

    public void remove(String key) {
        if (mParams != null) {
            mParams.remove(key);
        }
    }

    public TreeMap<String, Object> getParamsMap() {
        return mParams;
    }

    public int size() {
        if (mParams != null) {
            return mParams.size();
        }
        return 0;
    }

}
