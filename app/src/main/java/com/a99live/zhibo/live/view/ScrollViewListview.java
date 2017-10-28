package com.a99live.zhibo.live.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ScrollViewListview extends ListView{

	    public ScrollViewListview(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	 
	    public ScrollViewListview(Context context, AttributeSet attrs) {
	        super(context, attrs);
	    }
	 
	    public ScrollViewListview(Context context, AttributeSet attrs, int defStyle) {
	        super(context, attrs, defStyle);
	    }
	 
	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
	                MeasureSpec.AT_MOST);
	        super.onMeasure(widthMeasureSpec, expandSpec);
	    }
	 

}
