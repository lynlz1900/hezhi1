package com.wukong.hezhi.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.wukong.hezhi.bean.QQLoginInfo;
import com.wukong.hezhi.bean.QQUserInfo;
import com.wukong.hezhi.bean.ShareInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.ActivitiesManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.io.File;

/**
 *
 * @ClassName: QQActivity
 * @Description: TODO(QQ第三方)
 * @author HuZhiyin
 * @date 2017-1-16 下午6:47:49
 *
 */
public class QQActivity extends BaseActivity {
	// public String pageName = ContextUtil.getString(R.string.qq_login);
	// public String pageCode ="qq_login";
	private Tencent mTencent;
	public static String APP_ID = "1105817044";// 其中APP_ID是分配给第三方应用的appid，类型为String。
	private String APP_KEY = "Ykwj0sYpA6TnoFPH";
	private BaseUiListener listener;

	private boolean isShare = false;
	private ShareInfo shareInfo;

	public void init() {
		mTencent = BaseApplication.mTencent;// Tencent类是SDK的主要实现类，开发者可通过Tencent类访问腾讯开放的OpenAPI。
		listener = new BaseUiListener();
		shareInfo = (ShareInfo) getIntent().getSerializableExtra(Constant.Extra.SHARE_INFO);
		if (shareInfo != null) {
			isShare = true;
			share();
		} else {
			login();
		}
	}

	/** 次分享方法可以回调，掉此方法必须采用跳转activity。此方法备用 */
	private void share() {
		if (!mTencent.isSessionValid()) {
			if (!PackageUtil.isQQClientAvailable(ContextUtil.getContext())) {
				ScreenUtil.showToast("QQ未安装");
				return;
			}
			final Bundle params = new Bundle();
			params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
			params.putString(QQShare.SHARE_TO_QQ_TITLE, shareInfo.getTitle());
			params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareInfo.getDescription());
			params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareInfo.getUrl());
			params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareInfo.getImagUrl());
			if (shareInfo.getType() == QQ_ZONE) {// 分享给空间
				params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
			}
			mTencent.shareToQQ(ActivitiesManager.getInstance().currentActivity(), params, listener);
		}
	}

	/** 登录 */
	public void login() {
		if (!PackageUtil.isQQClientAvailable(ContextUtil.getContext())) {
			ScreenUtil.showToast("QQ未安装");
			finish();
		}
		if (!mTencent.isSessionValid()) {
			mTencent.login(this, "all", listener);
		}
	}

	/** 退出登录 */
	public void logout() {
		mTencent.logout(this);
	}

	/** 获取用户信息 */
	public void getUserInfo() {
		QQToken token = mTencent.getQQToken();
		UserInfo userInfo = new UserInfo(this, mTencent.getQQToken());
		userInfo.getUserInfo(new IUiListener() {

			@Override
			public void onError(UiError arg0) {
				// TODO Auto-generated method stub
				ScreenUtil.showToast("获取qq信息失败");
				finish();

			}

			@Override
			public void onComplete(Object arg0) {
				// TODO Auto-generated method stub
				QQUserInfo qqUserInfo = (QQUserInfo) JsonUtil.parseJson2Obj(arg0.toString(), QQUserInfo.class);
				qqUserInfo.setOpenId(mTencent.getOpenId());
				Intent intent = new Intent();
				intent.putExtra(Constant.Extra.QQ_USER_INFO, qqUserInfo);
				setResult(RESULT_OK, intent);
				logout();
				finish();
			}

			@Override
			public void onCancel() {
				// TODO Auto-generated method stub
				finish();

			}
		});
	}

	private class BaseUiListener implements IUiListener {
		@Override
		public void onError(UiError e) {

			LogUtil.d("QQ登录onError::::" + "code:" + e.errorCode + ", msg:" + e.errorMessage + ", detail:"
					+ e.errorDetail);
			if (isShare) {
				setResult(RESULT_CANCELED);
			}
			finish();
		}

		@Override
		public void onCancel() {
			LogUtil.d("QQ登录onCancel::::" + "");
			if (isShare) {
				setResult(RESULT_CANCELED);
			}

			finish();
		}

		@Override
		public void onComplete(Object value) {
			// TODO Auto-generated method stub
			if (isShare) {
				setResult(RESULT_OK);
				finish();
			} else {
				QQLoginInfo qqLoginInfo = (QQLoginInfo) JsonUtil.parseJson2Obj(value.toString(), QQLoginInfo.class);
				if (qqLoginInfo != null) {
					mTencent.setOpenId(qqLoginInfo.getOpenid());
					mTencent.setAccessToken(qqLoginInfo.getAccess_token(), qqLoginInfo.getExpires_in());
					getUserInfo();
				}
			}

		}
	}

	// 一定要写此方法，要不然不能回调。
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Tencent.onActivityResultData(requestCode, resultCode, data, listener);
	}

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int QQ_ZONE = 3;
	public static int QQ_FRIDEND = 2;

	/** 分享到qq */
	public static void shareToQQ(ShareInfo shareInfo) {
		if (!PackageUtil.isQQClientAvailable(ContextUtil.getContext())) {
			ScreenUtil.showToast("QQ未安装");
			return;
		}
		final Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, shareInfo.getTitle());
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareInfo.getDescription());
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareInfo.getUrl());
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareInfo.getImagUrl());
		if (shareInfo.getType() == QQ_ZONE) {// 分享给空间
			params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
		}
		BaseApplication.mTencent.shareToQQ(ActivitiesManager.getInstance().currentActivity(), params, null);
	}

	/***
	 * 分享图片到qq,系统分享的方法
	 */
	public static void sharPic(String picPath) {
		if (!PackageUtil.isQQClientAvailable(ContextUtil.getContext())) {
			ScreenUtil.showToast("QQ未安装");
			return;
		}
		Uri imageUri = Uri.fromFile(new File(picPath));
		Intent shareIntent = new Intent();
		// 发送图片到qq
		ComponentName comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
		shareIntent.setComponent(comp);
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
		shareIntent.setType("image/*");
		ActivitiesManager.getInstance().currentActivity().startActivity(Intent.createChooser(shareIntent, "分享图片"));
	}

	/***
	 * 分享视频到qq,系统分享的方法
	 */
	public static void sharVideo(String videoPath) {
		if (!PackageUtil.isQQClientAvailable(ContextUtil.getContext())) {
			ScreenUtil.showToast("QQ未安装");
			return;
		}
//		String path = videoPath.substring(7);
		Uri imageUri = Uri.fromFile(new File(videoPath));
		Intent shareIntent = new Intent();
		// 发送图片到qq
		ComponentName comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
		shareIntent.setComponent(comp);
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
		shareIntent.setType("video/*");
		ActivitiesManager.getInstance().currentActivity().startActivity(Intent.createChooser(shareIntent, "分享视频"));
	}

	/** 分享本地照片到QQ空间 */
	public static void shareToQQZone(String picPath) {
		if (!PackageUtil.isQQClientAvailable(ContextUtil.getContext())) {
			ScreenUtil.showToast("QQ未安装");
			return;
		}
		Bundle params = new Bundle();
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, picPath);
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
		params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
		BaseApplication.mTencent.shareToQQ(ActivitiesManager.getInstance().currentActivity(), params, null);
	}

}
