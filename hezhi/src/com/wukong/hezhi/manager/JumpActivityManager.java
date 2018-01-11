package com.wukong.hezhi.manager;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;

public class JumpActivityManager {

	private JumpActivityManager() {
	}

	private static class Holder {
		private static final JumpActivityManager SINGLETON = new JumpActivityManager();
	}

	/**
	 * 单一实例
	 */
	public static JumpActivityManager getInstance() {
		return Holder.SINGLETON;
	}

	/** 跳转页面 */
	public void toActivity(Activity activity, Class<?> cls) {
		toActivity(activity, cls, null, null);
	}

	/** 带参数跳转页面 */
	public void toActivity(Activity activity, Class<?> cls, String key, Object value) {
		if (activity == null) {
			return;
		}

		Intent intent = new Intent(activity, cls);
		if (value instanceof String) {
			intent.putExtra(key, (String) value);
		} else if (value instanceof Serializable) {
			intent.putExtra(key, (Serializable) value);
		}
		activity.startActivity(intent);
	}
}
