package com.wukong.hezhi.ui.view;

import com.wukong.hezhi.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * 
 * @ClassName: RatioLayoutHeiht
 * @Description: TODO(可以设置宽高比的布局控件，以高为参照对象)
 * @author HuZhiyin
 * @date 2017-4-20 上午11:54:31
 *
 */
public class RatioLayoutHeiht extends FrameLayout {

	private float mPicRitio = 2.4f; // 一个固定的宽高比，后面创建属性自定义来设置宽高比

	public RatioLayoutHeiht(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout);
		mPicRitio = typedArray.getFloat(R.styleable.RatioLayout_picRatio, 0);

		typedArray.recycle();
	}

	public RatioLayoutHeiht(Context context) {
		this(context, null);// 这样都会走到上一个构造函数
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// 父控件是否是固定值或者是match_parent
		int mode = MeasureSpec.getMode(widthMeasureSpec);
		if (mode == MeasureSpec.EXACTLY) {

			int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
			int childHeight = parentHeight - getPaddingTop() - getPaddingBottom();
			int childWidth = (int) (parentHeight / mPicRitio + 0.5f);
			int parentWidth = childWidth + getPaddingLeft() + getPaddingRight();
			// 测量子控件,固定孩子的大小
			int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
			int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
			measureChildren(childWidthMeasureSpec, childHeightMeasureSpec);
			// 测量
			setMeasuredDimension(parentWidth, parentHeight);

		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

	}
}