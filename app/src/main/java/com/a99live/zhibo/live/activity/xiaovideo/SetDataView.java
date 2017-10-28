package com.a99live.zhibo.live.activity.xiaovideo;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.a99live.zhibo.live.activity.xiaovideo.adapter.AdapterSimple;

/**
 * 
 * @author LZe
 *
 */
public class SetDataView {
	
	
	
	
	/**
	 * 水平滚动中的view
	 * 
	 * @param scrollView
	 * @param adapter
	 * @param clickId
	 *            点击事件id
	 * @param func
	 *            点击事件
	 */
	public static void horizontalView(HorizontalScrollView scrollView, AdapterSimple adapter, int[] clickId, ClickFunc[] func) {
		int i = 0;
		for (i = 0; i < adapter.getCount(); i++) {
			View view = adapter.getView(i, null, scrollView);
			// 添加响应
			if (func != null) {
				if (clickId != null) {
					for (int j = 0; j < clickId.length; j++) {
						view.findViewById(clickId[j]).setOnClickListener(getClicker(i, func.length > 1 ? func[j] : func[0]));
					}
				} else {
					view.setOnClickListener(getClicker(i, func[0]));
				}
			}
			((LinearLayout) scrollView.getChildAt(0)).addView(view);
		}
	}
	
	private static OnClickListener getClicker(final int i, final ClickFunc func) {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				func.click(i, arg0);
			}
		};
	}
	
	public interface ClickFunc {
		/**
		 * 点击后的响应
		 * 
		 * @param index
		 *            点击的物件序号
		 * @param view
		 *            TODO
		 */
		public void click(int index, View view);
	}


	/**
	 * 设置table中View
	 * @param table TableLayout
	 * @param span 一行有多少个View
	 * @param adapter ImgSimpleAdapter
	 * @param clickId 点击事件id
	 * @param func 点击事件
	 */
	public static void view(TableLayout table, int span, AdapterSimple adapter, int[] clickId, ClickFunc[] func) {
		Context context = table.getContext();
		TableRow row = new TableRow(context);
		row.setLayoutParams(new TableRow.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		int i = 0;
		for (i = 0; i < adapter.getCount(); i++) {
			View view = adapter.getView(i, null, row);
			// 添加响应
			if (func != null) {
				if (clickId != null) {
					for (int j = 0; j < clickId.length; j++) {
						view.findViewById(clickId[j]).setOnClickListener(getClicker(i, j > func.length - 1 ? func[func.length - 1] : func[j]));
					}
				} else
					view.setOnClickListener(getClicker(i, func[0]));
			}
			TableRow.LayoutParams lp = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT);
			lp.weight = 1;
			view.setLayoutParams(lp);
			row.addView(view);
			if ((i + 1) % span == 0) {
				table.addView(row);
				row = new TableRow(context);
			}
		}
		// 补一行
		if (i % span > 0) {
			table.addView(row);
			// 补充数量
			while (i % span > 0) {
				View view = new View(table.getContext());
				TableRow.LayoutParams lp = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT);
				lp.weight = 1;
				row.addView(view, lp);
				i++;
			}
		}
	}

	/**
	 * 设置table中View
	 * @param table TableLayout
	 * @param span 一行有多少个View
	 * @param adapter SimpleAdapter
	 * @param clickId 点击事件id
	 * @param func 点击事件
	 * @param     width
	 * @param     height
	 */
//	public static void view(TableLayout table, int span, AdapterSimpleWithStar adapter, int[] clickId, ClickFunc[] func,
//                            int width, int height) {
//		Context context = table.getContext();
//		TableRow row = new TableRow(context);
//		row.setLayoutParams(new TableRow.LayoutParams(width, height));
//		int i = 0;
//		for (i = 0; i < adapter.getCount(); i++) {
//			View view = adapter.getView(i, null, row);
//			// 添加响应
//			if (func != null) {
//				if (clickId != null) {
//					for (int j = 0; j < clickId.length; j++) {
//						view.findViewById(clickId[j]).setOnClickListener(
//								getClicker(i, j > func.length - 1 ? func[func.length - 1] : func[j]));
//					}
//				} else
//					view.setOnClickListener(getClicker(i, func[0]));
//			}
//			TableRow.LayoutParams lp = new TableRow.LayoutParams(0, height);
//			lp.weight = 1;
//			view.setLayoutParams(lp);
//			row.addView(view);
//			if ((i + 1) % span == 0) {
//				table.addView(row);
//				row = new TableRow(context);
//			}
//		}
//		// 补一行
//		if (i % span > 0) {
//			table.addView(row);
//			// 补充数量
//			while (i % span > 0) {
//				View view = new View(table.getContext());
//				TableRow.LayoutParams lp = new TableRow.LayoutParams(0, height);
//				lp.weight = 1;
//				row.addView(view, lp);
//				i++;
//			}
//		}
//	}


}
