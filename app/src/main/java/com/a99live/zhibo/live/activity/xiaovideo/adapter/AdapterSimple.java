package com.a99live.zhibo.live.activity.xiaovideo.adapter;

import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;


/**
 * 
 * @author LZe
 *
 */

public class AdapterSimple extends SimpleAdapter {
//	public int imgResource= R.drawable.i_nopic;
	public int roundImgPixels = 0, imgWidth = 0, imgHeight = 0,// 以像素为单位
			roundType = 1; // 1为全圆角，2上半部分圆角
	public boolean imgZoom = false; // 是否允许图片拉伸来适应设置的宽或高
//	public String imgLevel = FileManager.save_cache; // 图片保存等级
	public ScaleType scaleType = ScaleType.CENTER_CROP;
	public int textMaxWidth = 0; // textview的最大宽度
	public int viewWidth = 0; // viewWidth的最小宽度
	public int textWidth = 0; // viewWidth的最小宽度 
	public View mParent;

	public AdapterSimple(View parent, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
		super(parent.getContext(), data, resource, from, to);
		mParent = parent;
	}
//
//	@Override
//	public void setViewImage(final ImageView v, String value) {
//		v.setVisibility(View.VISIBLE);
//		// 异步请求网络图片
//		if (value.indexOf("http") == 0) {
//			if (v.getTag() != null && v.getTag().equals(value))
//				return;
//			
//			v.setScaleType(ScaleType.CENTER_CROP);
//			v.setImageResource(imgResource);
//			
//			if (value.length() < 10)
//				return;
//			
//			v.setTag(value);
//			ReqInternet.loadImageFromUrl(value, getCallback(v), imgLevel);
//		}
//		// 直接设置为内部图片
//		else if (value.indexOf("ico") == 0) {
//			InputStream is = v.getResources().openRawResource(Integer.parseInt(value.replace("ico", "")));
//			Bitmap bitmap=Tools.inputStreamTobitmap(is);
//			bitmap = Tools.toRoundCorner(v.getResources(), bitmap, roundType, roundImgPixels);
//			Tools.setImgViewByWH(v, bitmap, imgWidth, imgHeight, imgZoom);
//		}
//		// 隐藏
//		else if (value.equals("hide") || value.length() == 0)
//			v.setVisibility(View.GONE);
//		// 直接加载本地图片
//		else if (!value.equals("ignore")) {
//			Bitmap bmp = Tools.imgPathToBitmap(value, imgWidth, imgHeight, false);
//			v.setScaleType(scaleType);
//			v.setImageBitmap(bmp);
//		}
//		// 如果为ignore,则忽略图片
//	}
//
//	public InternetCallback getCallback(final ImageView v) {
//		return new InternetCallback() {
//			@Override
//			public void loaded(int flag, String url, Object returnObj) {
//				if (flag > 1) {
//					ImageView img = null;
//					if (v.getTag().equals(url))
//						img = v;
//					if (img != null && returnObj != null) {
//						// 图片圆角和宽高适应
//						v.setScaleType(scaleType);
//						Bitmap bitmap = Tools.toRoundCorner(v.getResources(), (Bitmap) returnObj, roundType, roundImgPixels);
//						Tools.setImgViewByWH(v, bitmap, imgWidth, imgHeight, imgZoom);
//					}
//				}
//			}
//		};
//	}

	@Override
	public void setViewImage(ImageView v, int value) {
		setViewImage(v, value + "");
	}

//	@Override
//	public void setViewText(TextView v, String text) {
//		if (text == null || text.length() == 0 || text.equals("hide"))
//			v.setVisibility(View.GONE);
//		else {
//			if (textMaxWidth > 0 && v.getId() != R.id.tv_itemBurden&& v.getId() != R.id.allclick&&v.getId()!=R.id.myself_favourite_burden) {
//				v.setMaxWidth(textMaxWidth);
//			}
//			if (textWidth > 0) {
//				LayoutParams lp = (LayoutParams) v.getLayoutParams();
//				lp.width = textWidth;
//				lp.setMargins(Tools.dp2px(mParent.getContext(), 2), 0, Tools.dp2px(mParent.getContext(), 2), 0);
//				v.setLayoutParams(lp);
//			}
//			v.setVisibility(View.VISIBLE);
//			if (v.getId() == R.id.element_content) {
//				SpannableStringBuilder style = new SpannableStringBuilder(text);
//				// 设置指定位置文字的颜色
//				style.setSpan(new ForegroundColorSpan(Color.parseColor("#999999")), text.indexOf("/"), text.length(),
//						Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
//				v.setText(style);
//			} else {
////				Tools.setTextViewText(v.getContext(), v, text);
//				 v.setText(text);
//			}
//		}
//	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return super.getCount();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (viewWidth > 0) {
			View view = super.getView(position, convertView, parent);
			ViewGroup.LayoutParams lp = view.getLayoutParams();
			lp.width = viewWidth;
			// lp.setMargins(Tools.dp2px(mParent.getContext(), 2.5f), 0,
			// Tools.dp2px(mParent.getContext(), 2.5f), 0);
			view.setLayoutParams(lp);
			return view;
		}
		return super.getView(position, convertView, parent);
	}

	public ViewGroup getParent() {
		return (ViewGroup) mParent;
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		if (observer != null) {
			super.unregisterDataSetObserver(observer);
		}
	}
}
