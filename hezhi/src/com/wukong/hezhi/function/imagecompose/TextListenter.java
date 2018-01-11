package com.wukong.hezhi.function.imagecompose;

import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class TextListenter implements OnTouchListener {

	private int mode = 0;// 初始状态
	/** 拖拉照片模式 */
	private static final int MODE_DRAG = 1;
	/** 放大缩小照片模式 */
	private static final int MODE_ZOOM = 2;
	float oldDist;
	float textSize = 0;

	TextView textView = null;

	private float startx;// down事件发生时，手指相对于view左上角x轴的距离
	private float starty;// down事件发生时，手指相对于view左上角y轴的距离
	private float endx; // move事件发生时，手指相对于view左上角x轴的距离
	private float endy; // move事件发生时，手指相对于view左上角y轴的距离
	private int left; // DragTV左边缘相对于父控件的距离
	private int top; // DragTV上边缘相对于父控件的距离
	private int right; // DragTV右边缘相对于父控件的距离
	private int bottom; // DragTV底边缘相对于父控件的距离
	private int hor; // 触摸情况下，手指在x轴方向移动的距离
	private int ver; // 触摸情况下，手指在y轴方向移动的距离

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		textView = (TextView) v;
		if (textSize == 0) {
			textSize = textView.getTextSize();
		}
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			// 手指刚触摸到屏幕的那一刻，手指相对于View左上角水平和竖直方向的距离:startX startY
			startx = event.getX();
			starty = event.getY();
			mode = MODE_DRAG;
			break;
		case MotionEvent.ACTION_UP:
			mode = 0;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mode = 0;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			mode = MODE_ZOOM;
			break;

		case MotionEvent.ACTION_MOVE:
			if (mode == MODE_ZOOM) {
				float newDist = spacing(event);
				if (newDist > oldDist + 1) {
					zoom(newDist / oldDist);
					oldDist = newDist;
				}
				if (newDist < oldDist - 1) {
					zoom(newDist / oldDist);
					oldDist = newDist;
				}
			} else if (mode == MODE_DRAG){
				// 手指停留在屏幕或移动时，手指相对与View左上角水平和竖直方向的距离:endX endY
				endx = event.getX();
				endy = event.getY();
				// 获取此时刻 View的位置。
				left = textView.getLeft();
				top = textView.getTop();
				right = textView.getRight();
				bottom = textView.getBottom();
				// 手指移动的水平距离
				hor = (int) (endx - startx);
				// 手指移动的竖直距离
				ver = (int) (endy - starty);
				// 当手指在水平或竖直方向上发生移动时，重新设置View的位置（layout方法）
				if (hor != 0 || ver != 0) {
					textView.layout(left + hor, top + ver, right + hor, bottom + ver);
				}
			}
			break;
		}
		return true;
	}

	private void zoom(float f) {
		textView.setTextSize(textSize *= f);
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

}