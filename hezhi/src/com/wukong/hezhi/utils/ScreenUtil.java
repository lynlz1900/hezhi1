package com.wukong.hezhi.utils;

import java.lang.reflect.Field;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.HezhiConfig;
import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @ClassName: ScreenUtil
 * @Description: TODO(有关屏幕显示及尺寸的显示类)
 * @author HuZhiyin
 * @date 2016-12-30 下午6:10:25
 * 
 */
public class ScreenUtil {
	public static Context mContext = ContextUtil.getContext();

	/**
	 * 得到设备屏幕的宽度
	 */
	public static int getScreenWidth() {
		return mContext.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 得到设备屏幕的高度
	 */
	public static int getScreenHeight() {
		return mContext.getResources().getDisplayMetrics().heightPixels;
	}

	public static float screenRatioW2H() {
		float h = getScreenHeight();
		float w = getScreenWidth();
		return w / h;
	}

	public static float screenRatioH2W() {
		float h = getScreenHeight();
		float w = getScreenWidth();
		return h / w;
	}

	/**
	 * 得到设备的密度
	 */
	public static float getScreenDensity() {
		return mContext.getResources().getDisplayMetrics().density;
	}

	/** dp转换px */
	public static int dp2px(int dp) {
		final float scale = mContext.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	/** px转换dp */

	public static int px2dp(int px) {
		final float scale = mContext.getResources().getDisplayMetrics().density;
		return (int) (px / scale + 0.5f);
	}

	/** px转sp */

	public static float px2sp(float px) {
		float dpi = mContext.getResources().getDisplayMetrics().densityDpi;
		float sp = ((float) px) / (dpi / 160);
		return sp;
	}

	/** sp转px */
	public static float sp2px(float sp) {
		float dpi = mContext.getResources().getDisplayMetrics().densityDpi;
		float px = sp * dpi / 160;
		return px;
	}

	public static float getSpSize(int dimensId) {
		float pxSize = mContext.getResources().getDimension(dimensId);
		float spSize = px2sp(pxSize);
		return spSize;
	}

	/** 移除view的父控件 */
	public static void removeParent(View v) {
		// 先找到爹 在通过爹去移除孩子
		ViewParent parent = v.getParent();
		// 所有的控件 都有爹 爹一般情况下 就是ViewGoup
		if (parent instanceof ViewGroup) {
			ViewGroup group = (ViewGroup) parent;
			group.removeView(v);
		}
	}

	public static View inflate(int id) {
		return View.inflate(mContext, id, null);
	}

	public static Toast mToast;
	public static TextView view;

	/** 单例吐司，在主线程显示 */
	public static void showToast(final String msg) {
		ThreadUtil.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mToast == null) {
					view = new TextView(mContext);
					view.setBackground(mContext.getResources().getDrawable(R.drawable.shape_toast));
					view.setPadding(50, 20, 50, 20);
					view.setTextColor(mContext.getResources().getColor(R.color.white));
					mToast = Toast.makeText(mContext, "", Toast.LENGTH_SHORT);
				}
				view.setText(msg);
				mToast.setView(view);
				mToast.show();
			}
		});
	}

	/** 调试用的吐司 */
	public static void showToastByDebug(final String msg) {
		if (HezhiConfig.DEBUG) {
			showToast(msg);
		}
	}

	/** 获取虚拟功能键高度 */
	public static int getStatusBarHeight() {
		Class<?> c = null;

		Object obj = null;

		Field field = null;

		int x = 0, sbar = 0;

		try {

			c = Class.forName("com.android.internal.R$dimen");

			obj = c.newInstance();

			field = c.getField("status_bar_height");

			x = Integer.parseInt(field.get(obj).toString());

			sbar = mContext.getResources().getDimensionPixelSize(x);

		} catch (Exception e1) {

			e1.printStackTrace();

		}

		return sbar;
	}

	  @SuppressWarnings("deprecation")
		public static int getScreenWidth(Activity activity){
			  int width = 0;
			  WindowManager windowManager = activity.getWindowManager();    
	          Display display = windowManager.getDefaultDisplay();    
	          width=display.getWidth();
			  return width;
		  }
}
