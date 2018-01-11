package com.wukong.hezhi.utils;

import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.wukong.hezhi.manager.ActivitiesManager;

/**
 * 
 * @ClassName: PackageUtil
 * @Description: TODO(获取包信息工具类)
 * @author HuZhiyin
 * @date 2017-1-3 上午8:27:42
 * 
 */
public class PackageUtil {
	public static Context mContext = ContextUtil.getContext();

	/** 获取包信息 */
	public static PackageInfo getPackageInfo() {
		PackageInfo packInfo = null;
		try {
			packInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return packInfo;
	}

	/**
	 * 获取版本号
	 * 
	 */
	public static String getVersionName() {
		String versionName = "";
		try {
			PackageManager manager = mContext.getPackageManager();
			PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
			versionName = info.versionName;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 获取版本号
	 */
	public static int getVersionCode() {
		int versionCode = 0;
		try {
			PackageManager manager = mContext.getPackageManager();
			PackageInfo info = manager.getPackageInfo(mContext.getPackageName(), 0);
			versionCode = info.versionCode;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	/** 通过应用的包名启动该应用 */
	public static void doStartApplicationWithPackageName(String packagename) {

		// 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
		PackageInfo packageinfo = null;
		try {
			packageinfo = mContext.getPackageManager().getPackageInfo(packagename, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (packageinfo == null) {// 如果不存在，则跳入到下载的连接
			Intent intent = new Intent();
			intent.setAction("android.intent.action.VIEW");
			Uri content_url = Uri.parse("http://www.sodoon.com:8010/Project/67");
			intent.setData(content_url);
			Activity activity = ActivitiesManager.getInstance().currentActivity();
			ActivitiesManager.getInstance().currentActivity().startActivity(intent);
			return;
		}

		// 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);

		// 通过getPackageManager()的queryIntentActivities方法遍历
		List<ResolveInfo> resolveinfoList = mContext.getPackageManager().queryIntentActivities(resolveIntent, 0);

		ResolveInfo resolveinfo = resolveinfoList.iterator().next();
		if (resolveinfo != null) {
			// packagename = 参数packname
			String packageName = resolveinfo.activityInfo.packageName;
			// 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
			String className = resolveinfo.activityInfo.name;
			// LAUNCHER Intent
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);

			// 设置ComponentName参数1:packagename参数2:MainActivity路径
			ComponentName cn = new ComponentName(packageName, className);
			intent.setComponent(cn);
			ActivitiesManager.getInstance().currentActivity().startActivity(intent);
		}
	}

	/** 获取手机信息 */
	public static String getPhoneInfo() {
		String brand = android.os.Build.BRAND + "-" + Build.MODEL;
		return brand;
	}

	/** 获取手机唯一标识 */

	public static String getPhoneId() {
		TelephonyManager TelephonyMgr = (TelephonyManager) mContext.getSystemService(mContext.TELEPHONY_SERVICE);
		String szImei = TelephonyMgr.getDeviceId();
		return szImei;
	}



	/** 判断微信是否安装 */
	public static boolean isWeixinAvilible(Context context) {
		final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.tencent.mm")) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断qq是否可用
	 */
	public static boolean isQQClientAvailable(Context context) {
		final PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
		if (pinfo != null) {
			for (int i = 0; i < pinfo.size(); i++) {
				String pn = pinfo.get(i).packageName;
				if (pn.equals("com.tencent.mobileqq")) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isZhifubaoAvailable(Context context) {
		Uri uri = Uri.parse("alipays://platformapi/startApp");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		ComponentName componentName = intent.resolveActivity(context.getPackageManager());
		return componentName != null;
	}

	public static boolean isLoctionOpen() {
		PackageManager pm = mContext.getPackageManager();
		boolean flag = (PackageManager.PERMISSION_GRANTED == pm.checkPermission("android.permission.RECORD_AUDIO",
				"packageName"));
		return flag;
	}

	public static final String KEY_APP_KEY = "JPUSH_APPKEY";

	// 取得AppKey
	public static String getAppKey(Context context) {
		Bundle metaData = null;
		String appKey = null;
		try {
			ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			if (null != ai)
				metaData = ai.metaData;
			if (null != metaData) {
				appKey = metaData.getString(KEY_APP_KEY);
				if ((null == appKey) || appKey.length() != 24) {
					appKey = null;
				}
			}
		} catch (NameNotFoundException e) {

		}
		return appKey;
	}
}
