package com.wukong.hezhi.ui.view;

import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.wukong.hezhi.R;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.ScreenUtil;

public class RedBagWindows extends PopupWindow {
	private ImageView redbug_iv, close_iv;
	private TextView redbug_tv;
	private LinearLayout ll_popup;

	private RedBagWindows() {
		View view = View.inflate(ContextUtil.getContext(),
				R.layout.layout_popupwindow_redbug, null);
		this.setAnimationStyle(R.style.popwindow_anim_buttomfromparent_style);
		ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		redbug_iv = (ImageView) view.findViewById(R.id.redbug_iv);
		close_iv = (ImageView) view.findViewById(R.id.close_iv);
		redbug_tv = (TextView) view.findViewById(R.id.redbug_tv);

		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		// setBackgroundDrawable(null);// 不响应返回键
		// setBackgroundDrawable(new BitmapDrawable());
		setFocusable(true);
		// setOutsideTouchable(false);
		setContentView(view);
	} // 构造方法是私有的，从而避免外界利用构造方法直接创建任意多实例。

	private static class Holder {
		private static final RedBagWindows SINGLETON = new RedBagWindows();
	}

	/**
	 * 单一实例
	 * */
	public static RedBagWindows getInstance() {
		return Holder.SINGLETON;
	}

	public void show(View parent, String tips, OnClickListener openListener) {
		redbug_tv.setText(tips + "给你发了一个红包");
		redbug_iv.setOnClickListener(openListener);
		close_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});

		int heght = 0;
		if (Build.VERSION.SDK_INT >= 24) {//如果是7.0以上的系统，需要获取状态栏的高度。
			heght = ScreenUtil.getStatusBarHeight();
		}
		
		try {
			showAtLocation(parent, Gravity.BOTTOM, 0, heght);
			update();
		} catch (Exception e) {
		}
	}

}