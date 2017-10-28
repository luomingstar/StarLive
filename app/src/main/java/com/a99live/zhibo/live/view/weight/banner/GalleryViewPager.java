package com.a99live.zhibo.live.view.weight.banner;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * 
 * @author LZe
 *
 */
public class GalleryViewPager extends ViewPager{
	private int auto; // 1一直滑动，2暂时不滑动，0停止滑动
	private ViewPager viewPager;
    private AdapterViewPager adapterVP;
    private Thread thread;
    private int nowPosition;
    private float mLastMotionX; // 手指触碰屏幕的最后一次x坐标
    private final int MOVE_LIMITATION=150;
	
	public GalleryViewPager(Context context) {
		super(context);

	}

	public GalleryViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);

	}

	//暂停、播放、停止自动播放
	public void stop(){ auto=0;}
	public void start(){ auto=1;}
	public void pause(){ auto=2;}
	
	/**
	 * 初始化轮播
	 * @param views		轮播的view
	 * @param time		轮播时间间隔
	 * @param helper	轮播事件
	 */
	public void init(final ArrayList<? extends View> views,final int time,final Helper helper){
		nowPosition=1;
		adapterVP=new AdapterViewPager(views);
		start();
		if(thread!=null) thread.interrupt();
		this.setAdapter(adapterVP);
        this.setCurrentItem(nowPosition);
        this.viewPager=this;

		//设置滑动速度
		try {
			Field mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
//			Field localField2 = ViewPager.class.getDeclaredField("sInterpolator");
//			localField2.setAccessible(true);
//			this.vW = new a(getContext(), (Interpolator)localField2.get(null));
			ChangeSpeedScroller scroller = new ChangeSpeedScroller(viewPager.getContext(), new DecelerateInterpolator());
			scroller.setDuration(300);
			mScroller.set(viewPager, scroller);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
//				viewPager.setCurrentItem(nowPosition+1,true);
				viewPager.setCurrentItem(nowPosition+1);
			}
		};
		thread=new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if(thread.isInterrupted()) break;
					try {
						Thread.sleep(time);
					} catch (InterruptedException e) {
					}
					switch (auto) {
					case 0:break;
					case 1:
						handler.sendEmptyMessage(0);
						break;
					case 2:
						auto = 1;
						break;
					}
				}
			}
		});
		thread.start();
		// 手动滑动时暂停自动
		this.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				auto = 2;
				return false;
			}
		});
		//绑定事件
		this.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if ( views.size() > 1) { //多于1，才会循环跳转
					if ( position < 1) { //首位之前，跳转到末尾（N）
						position = views.size()-2;
						final int mPositon = position;
						
						//延时加载
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								viewPager.setCurrentItem(mPositon, false);
							}
						}, 300);
						
						return;
					} else if ( position > views.size()-2) { //末位之后，跳转到首位（1）
						position = 1;
						final int mPositon = position;
						//延时加载
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								viewPager.setCurrentItem(mPositon, false);
							}
						}, 300);
//						viewPager.setCurrentItem(position, false); //false:不显示跳转过程的动画
						return;
					}
				}
				nowPosition=position;
				if(helper!=null){
					if(views.size()>1){
						if(position==0) helper.onChange(views.size()-3);
						else if(position==views.size()-1) helper.onChange(0);
						else helper.onChange(position-1);
					}
					else helper.onChange(0);
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
		});
		//绑定点击事件
		for(int i=0;i<views.size();i++){
			final int index=i;
			views.get(i).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(views.size()>1){
						if(index==0) helper.onClick(v, views.size()-3);
						else if(index==views.size()-1) helper.onClick(v, 0);
						else helper.onClick(v, index-1);
					}
					else helper.onClick(v,0);
				}
			});
		}
	}

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		final int action = event.getAction();
//		final float x = event.getX();
//		switch (action) {
//		case MotionEvent.ACTION_DOWN:
//			mLastMotionX = x;
//			break;
//		case MotionEvent.ACTION_MOVE:
//			break;
//		case MotionEvent.ACTION_UP:
////			StringManager.print("d", "相差："+(mLastMotionX-x));
//			//右往左
//			if(mLastMotionX-x>MOVE_LIMITATION){
//				viewPager.setCurrentItem(nowPosition+1,true);
//				return true;
//			}
//			else if(mLastMotionX-x<-MOVE_LIMITATION){
//				viewPager.setCurrentItem(nowPosition-1,true);
//				return true;
//			}
//			break;
//		default:
//			break;
//		}
//		return super.onTouchEvent(event);
//	}
	
	public class AdapterViewPager extends PagerAdapter{

	    private ArrayList<? extends View> mViews;
	    
	    public AdapterViewPager(ArrayList<? extends View> views){
	    	this.mViews=views;
	    }

	    @Override
		public int getCount() {
			return mViews.size();
		}
		
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(mViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = mViews.get(position);
			container.addView(view);
			return view;
		}
	}
	public interface Helper {
		/**
		 * 点击响应
		 */
		public void onClick(View view, int position);
		/**
		 * 切换响应
		 */
		public void onChange(int position);
	}

	public class ChangeSpeedScroller extends Scroller {
		private int mDuration=250;

		public void setDuration(int duration) {
			mDuration = duration;
		}

		public ChangeSpeedScroller(Context context){
			super(context);
		}
		public ChangeSpeedScroller(Context context, Interpolator interpolator){
			super(context,interpolator);
		}
		public ChangeSpeedScroller(Context context, Interpolator interpolator, boolean flywheel){
			super(context,interpolator,flywheel);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy, int duration) {
			super.startScroll(startX, startY, dx, dy, mDuration);
		}

		@Override
		public void startScroll(int startX, int startY, int dx, int dy) {
			super.startScroll(startX, startY, dx, dy);
		}
	}
}
