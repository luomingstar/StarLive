package com.a99live.zhibo.live.gift;

import android.view.animation.Interpolator;



/**
 * Created by JJGCW on 2016/10/15.
 */

public class MyTimeInterpolator implements Interpolator {
    @Override
    public float getInterpolation(float v) {
        return ((4*v-2)*(4*v-2)*(4*v-2))/16f + 0.5f;

    }
}
