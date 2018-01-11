package com.wukong.hezhi.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UpgradeManager;
import com.wukong.hezhi.manager.UpgradeManager.UpgradeManagerCallBack;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 
 * @ClassName: AboutActivity
 * @Description: TODO(关于页面)
 * @author HuZhiyin
 * @date 2017-1-10 下午3:01:22
 * 
 */
public class AboutActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.about);
	public String pageCode = "about";
	@ViewInject(R.id.update_ll)
	private LinearLayout update_ll;

	@ViewInject(R.id.abouthezhi_ll)
	private LinearLayout abouthezhi_ll;

	@ViewInject(R.id.function_ll)
	private LinearLayout function_ll;

	@ViewInject(R.id.copyright_ll)
	private Button copyright_ll;

	@ViewInject(R.id.visionCode_tv)
	private TextView visionCode_tv;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_about;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		initVersion();
		headView.setTitleStr(ContextUtil.getString(R.string.about));
		visionCode_tv.setText("v" + version);
	}

	/** 包信息 */
	private PackageInfo packInfo = null;
	/** 版本 */
	private String version;
	/** 版本号 */
	private int versionCode;

	/** 初始化版本信息 */
	private void initVersion() {
		packInfo = PackageUtil.getPackageInfo();
		version = packInfo.versionName;
		versionCode = packInfo.versionCode;
	}

	/** google api 数组数表示点击的次数 */
	long[] mHits = new long[3];

	@OnClick(value = { R.id.update_ll, R.id.abouthezhi_ll, R.id.function_ll, R.id.visionCode_tv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.update_ll:
			UpgradeManager.getInstance().upgradeVersion(new UpgradeManagerCallBack() {

				@Override
				public void upgradeSucess() {
					// TODO Auto-generated method stub

				}

				@Override
				public void ugradeFail() {
					// TODO Auto-generated method stub

				}

				@Override
				public void cancel() {
					// TODO Auto-generated method stub

				}
			});// 升级
			break;
		case R.id.abouthezhi_ll:
			toAboutHezhi();
			break;
		case R.id.function_ll:
			toFunctionIntroduce();
			break;

		case R.id.visionCode_tv:
			// 每点击一次 实现左移一格数据
			System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
			// 给数组的最后赋当前时钟值
			mHits[mHits.length - 1] = SystemClock.uptimeMillis();
			// 当0出的值大于当前时间-500时 证明在500秒内点击了3次
			if (mHits[0] > SystemClock.uptimeMillis() - 500) {
				ScreenUtil.showToast("versionCode:" + versionCode + "\nversionName:" + version);
			}
			break;

		}
	}

	private void toFunctionIntroduce() {
		Intent intent = new Intent(this, WebViewActivity.class);
		intent.putExtra(Constant.Extra.WEB_URL, HttpURL.URL1 + HttpURL.FUNCTION);
		intent.putExtra(Constant.Extra.WEBVIEW_TITLE, getString(R.string.function_introduce));
		startActivity(intent);
	}

	private void toAboutHezhi() {
		Intent intent = new Intent(this, WebViewActivity.class);
		intent.putExtra(Constant.Extra.WEB_URL, HttpURL.URL1 + HttpURL.ABOUTUS);
		intent.putExtra(Constant.Extra.WEBVIEW_TITLE, getString(R.string.about_hezhi));
		startActivity(intent);
	}

}
