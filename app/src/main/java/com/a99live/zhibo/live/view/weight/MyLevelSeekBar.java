package com.a99live.zhibo.live.view.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

/**
 * Created by JJGCW on 2017/2/10.
 */

public class MyLevelSeekBar extends SeekBar {
    public MyLevelSeekBar(Context context) {
        super(context);
    }

    public MyLevelSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLevelSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        //原来是要将TouchEvent传递下去的,我们不让它传递下去就行了
        //return super.onTouchEvent(event);

        return false ;
    }
}
