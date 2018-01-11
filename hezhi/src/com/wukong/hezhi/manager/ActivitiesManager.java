package com.wukong.hezhi.manager;

import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.wukong.hezhi.utils.ContextUtil;

/**
 * 
 * @ClassName: ActivitiesManager
 * @Description: TODO(Activity管理类)
 * @author HuZhiyin
 * @date 2016-12-9 上午10:12:43
 * 
 */
public class ActivitiesManager {

	private ActivitiesManager() {
	}

	private static class Holder {
		private static final ActivitiesManager SINGLETON = new ActivitiesManager();
	}

	/**
	 * 单一实例
	 * */
	public static ActivitiesManager getInstance() {
		return Holder.SINGLETON;
	}

	private static Stack<Activity> activityStack;

	/**
	 * 添加Activity到堆栈
	 */
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	/**
	 * 获取当前Activity（堆栈中最后一个压入的）
	 */
	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	/**
	 * 结束当前Activity（堆栈中最后一个压入的）
	 */
	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	/**
	 * 结束指定的Activity
	 */
	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}

	/**
	 * 结束指定类名的Activity
	 */
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
//				finishActivity(activity);
				activity.finish();
			}
		}
	}

	/**
	 * 结束所有Activity
	 */
	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}

	/**
	 * 退出应用程序
	 */
	public void AppExit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityMgr = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			activityMgr.restartPackage(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {
		}
	}

	private void moveAppToFront(String packName) {

		ActivityManager am = (ActivityManager) ContextUtil.getContext()
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<ActivityManager.RunningTaskInfo> recentList = am
				.getRunningTasks(30);

		recentList.remove(0); // 去掉锁屏本身

		for (ActivityManager.RunningTaskInfo info : recentList) {

			if (info.topActivity.getPackageName().equals(packName)) {

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

					am.moveTaskToFront(info.id, 0);

				}

				return;

			}

		}

	}

	/**
	 * 判断某个界面是否在前台
	 * 
	 * @param context
	 * @param className
	 *            某个界面名称
	 */
	public boolean isForeground(Context context, String className) {
		if (context == null || TextUtils.isEmpty(className)) {
			return false;
		}
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(1);
		if (list != null && list.size() > 0) {
			ComponentName cpn = list.get(0).topActivity;
			if (className.equals(cpn.getClassName())) {
				return true;
			}
		}
		return false;
	}
}