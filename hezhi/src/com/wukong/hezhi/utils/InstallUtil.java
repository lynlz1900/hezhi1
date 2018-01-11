package com.wukong.hezhi.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.ActivitiesManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

/**
 * 
 * @ClassName: InstallUtil
 * @Description: TODO(APK安装工具类)
 * @author HuZhiyin
 * @date 2016-10-26 上午9:04:12
 * 
 */
public class InstallUtil {
	public static Context mContext = ContextUtil.getContext();

	/** 从SD里安装文件 */
	public static void installApkFromSD(final String filePath) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.setDataAndType(Uri.fromFile(new File(filePath)),
						"application/vnd.android.package-archive");
				mContext.startActivity(intent);
			}
		}).start();
	}

	/** 从asset里安装文件 */
	public static void installApkFromAsset(final String fileName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				String path = Environment.getExternalStorageDirectory()
						.getAbsolutePath() + "/" + fileName;
				if (copyApkFromAssets(mContext, fileName, path)) {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.setDataAndType(Uri.parse("file://" + path),
							"application/vnd.android.package-archive");
					mContext.startActivity(intent);
				}
			}
		}).start();
	}

	public static boolean copyApkFromAssets(Context context, String fileName,
			String path) {
		boolean copyIsFinish = false;
		try {
			InputStream is = context.getAssets().open(fileName);
			File file = new File(path);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = is.read(temp)) > 0) {
				fos.write(temp, 0, i);
			}
			fos.close();
			is.close();
			copyIsFinish = true;
		} catch (IOException e) {
			e.printStackTrace();
			LogUtil.d(fileName + "不存在");
		}
		return copyIsFinish;
	}

	/** 通过应用的包名启动该应用 */
	public static void doStartApplicationWithPackageName(String packagename,
			String apkUrl) {

		// 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
		PackageInfo packageinfo = null;
		try {
			packageinfo = mContext.getPackageManager().getPackageInfo(
					packagename, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			packageinfo = null;
		}

		if (packageinfo == null) {// 如果不存在，则跳入到下载的连接
			downLoad(apkUrl);// 通过浏览器下载
		} else {
			openApp(packageinfo);// 启动第三方app
		}
	}

	public static void openByAppName(String packagename, String apkUrl,
			InstallCallCack installCallCack) {
		// 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
		PackageInfo packageinfo = null;
		try {
			packageinfo = mContext.getPackageManager().getPackageInfo(
					packagename, 0);
		} catch (NameNotFoundException e) {
			packageinfo = null;
		}
		if (packageinfo == null) {// 如果不存在，则跳入到下载的连接
			downLoadApk(apkUrl, installCallCack);
		} else {
			openApp(packageinfo);// 启动第三方app
		}
	}

	/** 通过浏览器下载 */
	private static void downLoad(String apkUrl) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		Uri content_url = Uri.parse(apkUrl);
		intent.setData(content_url);
		Activity activity = ActivitiesManager.getInstance().currentActivity();
		ActivitiesManager.getInstance().currentActivity().startActivity(intent);
	}

	/** 打开第三方app */

	private static void openApp(PackageInfo packageinfo) {
		// 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
		Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
		resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);

		// 通过getPackageManager()的queryIntentActivities方法遍历
		List<ResolveInfo> resolveinfoList = mContext.getPackageManager()
				.queryIntentActivities(resolveIntent, 0);

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
			ActivitiesManager.getInstance().currentActivity()
					.startActivity(intent);
		}
	}

	/** 后台下载安装 */
	private static void downLoadApk(String apkUrl,
			final InstallCallCack installCallCack) {

		final String filePath = HezhiConfig.FILE_APK;;
		HttpManager.getInstance().download(apkUrl, filePath, new RequestCallBack<File>() {
			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				super.onLoading(total, current, isUploading);
				// progressDialog.show();
				// progressDialog.setMax((int) total);
				// progressDialog.setProgress((int) current);
				installCallCack.onLoading(total, current);
			}

			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				installCallCack.onSuccess();
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				installCallCack.onFail();
			}
		});

	}

	public interface InstallCallCack {
		void onLoading(long total, long current);

		void onSuccess();

		void onFail();
	}

}
