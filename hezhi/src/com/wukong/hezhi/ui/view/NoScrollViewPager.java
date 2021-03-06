package com.wukong.hezhi.ui.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
/**
 * 
* @ClassName: NoScrollViewPager 
* @Description: TODO(不能左右划的ViewPager) 
* @author HuZhiyin 
* @date 2016-10-10 上午8:34:45 
*
 */
public class NoScrollViewPager extends NoPreloadViewPager {

	public NoScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoScrollViewPager(Context context) {
		super(context);
	}

	// 表示事件是否拦截, 返回false表示不拦截, 可以让嵌套在内部的viewpager相应左右划的事件
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return false;
	}

	/**
	 * 重写onTouchEvent事件,什么都不用做
	 */
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		return false;
	}
}
