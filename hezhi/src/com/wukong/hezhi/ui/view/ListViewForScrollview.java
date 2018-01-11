package com.wukong.hezhi.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 适配Scrollview中的ListView
 */
public class ListViewForScrollview extends ListView {

	public ListViewForScrollview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListViewForScrollview(Context context) {
		super(context);
	}

	public ListViewForScrollview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
