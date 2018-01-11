package com.wukong.hezhi.manager;

import java.io.File;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.VersionInfo;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.ui.activity.UpdateActivity;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.ui.view.CustomAlterProgressDialog;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.InstallUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * 
 * @ClassName: UpgradeManager
 * @Description: TODO(版本升级)
 * @author HuZhiyin
 * @date 2016-12-29 上午11:29:32
 * 
 */
public class UpgradeManager {
	private UpgradeManager() {
	}

	private static class Holder {
		private static final UpgradeManager SINGLETON = new UpgradeManager();
	}

	/**
	 * 单一实例
	 */
	public static UpgradeManager getInstance() {
		return Holder.SINGLETON;
	}

	/** 升级的回调接口 */
	private UpgradeManagerCallBack upgradeManagerCallBack;
	/** 包信息 */
	private PackageInfo packInfo = null;
	/** 版本信息 */
	private VersionInfo versionInfo;
	/** 版本 */
	private String version;
	/** 版本号 */
	private int versionCode;
	/** progressDialog */
	private CustomAlterProgressDialog customProgressDialog;

	/** 版本更新 */
	public void upgradeVersion(final UpgradeManagerCallBack upgradeManagerCallBack) {
		this.upgradeManagerCallBack = upgradeManagerCallBack;
		initView();
		initVersion();// 初始化版本信息
		checkVersion(upgradeManagerCallBack);// 检查版本是否升级
	}

	/***
	 * 初始化View
	 */
	private void initView() {
		customProgressDialog = new CustomAlterProgressDialog.Builder(ActivitiesManager.getInstance().currentActivity())
				.setCancelable(false).setMsg(ContextUtil.getString(R.string.onloading)).build();
		customProgressDialog.show();
	}

	/** 初始化版本信息 */
	private void initVersion() {
		packInfo = PackageUtil.getPackageInfo();
		version = packInfo.versionName;
		versionCode = packInfo.versionCode;
	}

	/** 获取版本信息 */
	private void checkVersion(final UpgradeManagerCallBack upgradeManagerCallBack) {
		String URL = HttpURL.URL + HttpURL.UP_VISION;
		HttpManager.getInstance().get(URL, new RequestCallBack<String>() {

			@Override
			public void onFailure(HttpException error, String msg) {
				// upgradeManagerCallBack.ugradeFail();
				customProgressDialog.dismiss();
				upgradeManagerCallBack.cancel();
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				customProgressDialog.dismiss();
				ResponseJsonInfo<VersionInfo> info = JsonUtil.fromJson(responseInfo.result, VersionInfo.class);
				if (info != null) {
					String httpCode = info.getHttpCode();
					versionInfo = info.getBody();
					if (HttpCode.SUCESS.equals(httpCode) && versionInfo != null) {
						if (versionInfo.getVersionsCode() > versionCode) {// 服务器的版本比此版本高，提示升级
							updateDialog();
						} else {// 不升级
							upgradeManagerCallBack.cancel();
						}
					} else {
						upgradeManagerCallBack.cancel();
					}
				} else {
					upgradeManagerCallBack.cancel();
				}
			}
		});
	}

	/** 升级弹框提示 */
	@SuppressLint("InlinedApi")
	private void updateDialog() {
		
		final Activity activity = ActivitiesManager.getInstance().currentActivity();
		new CustomAlterDialog.Builder(activity)
				.setTitle(ContextUtil.getString(R.string.update_tip))
				.setMsg(ContextUtil.getString(R.string.find_new_version)).setCancelable(false)
				.setCancelButton(ContextUtil.getString(R.string.cancel), new OnDialogClickListener() {

					@Override
					public void onDialogClick(View v, CustomAlterDialog dialog) {
						// TODO Auto-generated method stub
						if ("TRUE".equals(versionInfo.getForceUpdateType())) {// 如果是强制升级,则按取消按钮直接退出
							upgradeManagerCallBack.ugradeFail();
						} else {
							upgradeManagerCallBack.cancel();
						}
					}
				}).setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {
					@Override
					public void onDialogClick(View v, CustomAlterDialog dialog) {
						// TODO Auto-generated method stub
						upgradeManagerCallBack.upgradeSucess();
//						downLoadApk();
						JumpActivityManager.getInstance().toActivity(activity, UpdateActivity.class);
					}
				}).build().show();

	}

	/** 下载安装 */
	private void downLoadApk() {
		final LinearLayout linearLayout = (LinearLayout) ScreenUtil.inflate(R.layout.layout_download);
		final ProgressBar progressBar = (ProgressBar) linearLayout.findViewById(R.id.download_pb);
		CustomAlterDialog.Builder builder = new CustomAlterDialog.Builder(
				ActivitiesManager.getInstance().currentActivity());
		builder.setTitle(ContextUtil.getString(R.string.loading)).setView(linearLayout).setCancelable(false);
		final CustomAlterDialog dialog = builder.build();
		dialog.show();
		final String filePath = HezhiConfig.FILE_APK;;
		String url = HttpURL.URL + HttpURL.APK_DOWN;
		HttpManager.getInstance().download(url, filePath, new RequestCallBack<File>() {

			@Override
			public void onLoading(long total, long current, boolean isUploading) {
				super.onLoading(total, current, isUploading);
				progressBar.setMax((int) total);
				progressBar.setProgress((int) current);
			}

			@Override
			public void onSuccess(ResponseInfo<File> arg0) {
				InstallUtil.installApkFromSD(filePath);// 安装apk
				dialog.dismiss();
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				dialog.dismiss();
			}
		});

	}

	/** 升级回调接口 */
	public interface UpgradeManagerCallBack {
		void upgradeSucess();// 下载成功

		void cancel();// 取消下载

		void ugradeFail();// 下载失败
	}
}
