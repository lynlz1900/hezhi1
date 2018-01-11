package com.wukong.hezhi.ui.view;

import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import com.wukong.hezhi.R;
import com.wukong.hezhi.manager.ActivitiesManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 
 * 定制保存成功提示
 * @author HuangFeiFei  by 2017-11-7 10:16
 *
 */
public class CustomSuccessWindows extends PopupWindow{

	@SuppressWarnings("deprecation")
	private CustomSuccessWindows() {
		View view = View.inflate(ContextUtil.getContext(),
				R.layout.layout_popupwindow_customsuccess, null);
		this.setAnimationStyle(R.style.popwindow_anim_buttomfromparent_style);
		setWidth(LayoutParams.FILL_PARENT);
		setHeight(LayoutParams.FILL_PARENT);
		setFocusable(true);
		setOutsideTouchable(false);
		setContentView(view);
	}

	private static class Holder {
		private static final CustomSuccessWindows SINGLETON = new CustomSuccessWindows();
	}

	/**
	 * 单一实例
	 * */
	public static CustomSuccessWindows getInstance() {
		return Holder.SINGLETON;
	}

	public void show() {
		final View parent = ActivitiesManager.getInstance().currentActivity().getWindow().getDecorView()
				.findViewById(android.R.id.content);// 获取一个view,popubwindow会用到
		
		int heght = 0;
		if (Build.VERSION.SDK_INT >= 24) {// 如果是7.0以上的系统，需要获取状态栏的高度。
			heght = ScreenUtil.getStatusBarHeight();
		}
		showAtLocation(parent, Gravity.CENTER, 0, heght);
		update();
	}
}