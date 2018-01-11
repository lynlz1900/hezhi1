package com.wukong.hezhi.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 适配listview中的gridview
 */
public class GridViewForListview extends GridView {

	public GridViewForListview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GridViewForListview(Context context) {
		super(context);
	}

	public GridViewForListview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}
