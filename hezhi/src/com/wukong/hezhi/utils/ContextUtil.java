package com.wukong.hezhi.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;
import android.widget.Toast;

import com.wukong.hezhi.R;
import com.wukong.hezhi.ui.activity.BaseApplication;

/**
 * 
 * @ClassName: ContextUtil
 * @Description: TODO(获取上下文工具类)
 * @author HuZhiyin
 * @date 2016-1-3 上午8:26:42
 * 
 */
public class ContextUtil {
	/** 获取上下文 */
	public static Context getContext() {
		return BaseApplication.getApplication();
	}

	/** 获取资源 */
	public static Resources getResource() {
		return BaseApplication.getApplication().getResources();
	}

	/** 获取字符串 */
	public static String getString(int id) {
		return BaseApplication.getApplication().getResources().getString(id);
	}

	/** 获取颜色 */
	public static int getColor(int id) {
		return BaseApplication.getApplication().getResources().getColor(id);
	}
	
	/** 获取图片*/
	public static Drawable getDrawable(int id) {
		return BaseApplication.getApplication().getResources().getDrawable(id);
	}
}
