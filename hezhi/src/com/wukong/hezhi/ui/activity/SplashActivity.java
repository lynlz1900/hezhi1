package com.wukong.hezhi.ui.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.AdContentInfo;
import com.wukong.hezhi.bean.AdContentInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.TabInfos;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UpgradeManager;
import com.wukong.hezhi.manager.UpgradeManager.UpgradeManagerCallBack;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.SPUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 
 * @ClassName: SplashActivity
 * @Description: TODO(闪屏页)
 * @author HuZhiyin
 * @date 2016-9-13 上午9:49:17
 * 
 */
public class SplashActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.splash);
	public String pageCode = "splash";

	@ViewInject(R.id.ad_iv)
	private ImageView ad_iv;

	@ViewInject(R.id.ad_fr)
	private FrameLayout ad_fr;

	private UpgradeManager upgradeManager = UpgradeManager.getInstance();

	private boolean isNotFirst = false;// 不是首次

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_splash;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		
		if (!isTaskRoot()) { //在其他地方打开APP时回到上次打开的界面
			finish(); 
			return; 
		}
		
		setSwipeBackEnable(false);// 闪屏页禁止侧滑消除
		isNotFirst = SPUtil.getShareBoolean(ContextUtil.getContext(),
				HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.FIRST);

		getTabData();// 获取产品分类的列表信息
	}

	public void getTabData() {
		String URL = HttpURL.URL1 + HttpURL.TABADATA;
		HttpManager.getInstance().get(URL, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				checkUpadte();// 检查升级
			}

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				savaTabaInfo(responseInfo);// 保存用户信息
				checkUpadte();// 检查升级
			}

		});
	}

	private void savaTabaInfo(ResponseInfo<String> responseInfo) {
		ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result,
				TabInfos.class);
		if (info != null) {
			if (info.getHttpCode().equals(HttpCode.SUCESS)) {
				TabInfos tabInfos = (TabInfos) info.getBody();
				Constant.TABINFOS.clear();
				Constant.TABINFOS.addAll(tabInfos.getArTypeList());
				SPUtil.savaToShared(ContextUtil.getContext(),
						HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.TAB_INFO,
						tabInfos);
			}
		}
	}

	/** 检查升级 */
	private void checkUpadte() {
		upgradeManager.upgradeVersion(new UpgradeManagerCallBack() {

			@Override
			public void upgradeSucess() {
			}

			@Override
			public void ugradeFail() {
				finish();
			}

			@Override
			public void cancel() {
				displayAd();// 展示广告
			}
		});
	}

	int timeCount = 3;// 广告时间。秒
	boolean skip = false;// 直接跳过广告

	public void displayAd() {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("placement", Constant.AdLocation.SPLASH_SCREEN.toString());
		String URL = HttpURL.URL1 + HttpURL.AD;
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {

				ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result,
						AdContentInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					AdContentInfos adContentInfos = (AdContentInfos) info
							.getBody();
					List<AdContentInfo> list = adContentInfos.getData();
					if (list == null) {
						toMyActitiy();
						return;
					}
					for (AdContentInfo adContentInfo : list) {
						ad_iv.setVisibility(View.INVISIBLE);// 隐藏背景
						if (adContentInfo.getType().equals(
								Constant.AdContentType.PICTURE.toString())) {
							String url = adContentInfo.getHttpUrl();
							final String jumurl = adContentInfo.getJumpUrl();

							BitmapUtils bitmapUtils = new BitmapUtils(
									ContextUtil.getContext());
							RelativeLayout layout_ad = (RelativeLayout) ScreenUtil
									.inflate(R.layout.layout_ad_splash);
							ImageView ad_iv = (ImageView) layout_ad
									.findViewById(R.id.ad_iv);
							final TextView skip_tv = (TextView) layout_ad
									.findViewById(R.id.skip_tv);
							bitmapUtils.display(ad_iv, url);
							ad_fr.addView(layout_ad);
							final Handler handler = new Handler();
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									if (timeCount > 0) {
										timeCount--;
										skip_tv.setText("跳过广告  " + timeCount);
										handler.postDelayed(this, 1000);
										if (timeCount == 0 && !skip) {
											toMyActitiy();
										}
									}
								}
							}, 1000);
							skip_tv.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									skip = true;
									toMyActitiy();
								}
							});

							ad_iv.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									if (!TextUtils.isEmpty(jumurl)) {
										skip = true;
										toMyActitiy();// 先正常顺序跳转
										Intent intent = new Intent(
												SplashActivity.this,
												WebViewActivity.class);
										intent.putExtra(Constant.Extra.WEB_URL,
												jumurl);
										startActivity(intent);// 再跳转至广告显示的web
									}
								}
							});

						}
					}
				} else {
					toMyActitiy();
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				toMyActitiy();
			}
		});
	}

	public void toMyActitiy() {
		if (!isNotFirst) {// 如果是第一次
			SPUtil.savaToShared(ContextUtil.getContext(),
					HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.FIRST, true);
			toActivity(GuideActivity.class);
			finish();
		} else {
			toActivity(MainActivity.class);
			finish();
		}
	}
}
