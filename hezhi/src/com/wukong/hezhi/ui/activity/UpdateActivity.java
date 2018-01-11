package com.wukong.hezhi.ui.activity;

import java.io.File;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.ActivitiesManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.InstallUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.ThreadUtil;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UpdateActivity extends BaseActivity {

	@ViewInject(R.id.download_pb)
	private ProgressBar progressBar;

	@ViewInject(R.id.install_tv)
	private TextView install_tv;

	@ViewInject(R.id.tip_tv)
	private TextView tip_tv;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_update;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		setSwipeBackEnable(false);// 禁止侧滑消除
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
		downLoadAPK();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
	}

	long firstPressTime = 0; // 记录第一次点击退出的时间

	/***
	 * 重写此方法，点击后退键后，程序隐藏后台。
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - firstPressTime > 2000) {
				ScreenUtil.showToast(ContextUtil.getString(R.string.canncel_update));
				firstPressTime = System.currentTimeMillis();
			} else {
				ActivitiesManager.getInstance().AppExit(ContextUtil.getContext());
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private String filePath;

	private void downLoadAPK() {
		filePath = HezhiConfig.FILE_APK;
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

				ThreadUtil.postDelayed(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						install_tv.setVisibility(View.VISIBLE);
						tip_tv.setText(ContextUtil.getString(R.string.update_done));
					}
				}, 1000);

			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
			}
		});
	}

	@OnClick(value = { R.id.install_tv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.install_tv:
			InstallUtil.installApkFromSD(filePath);// 安装apk
			break;
		}
	}

}
