package com.wukong.hezhi.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.ActivitiesManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: VideoPreviewWebActivity
 * @Description: TODO(视频定制信息预览)
 * @author Huzhiyin
 * @date 2017年10月18日 上午11:03:27
 *
 */
public class WebVideoPreviewActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.web_video_preview);
	public String pageCode = "web_video_preview";

	@ViewInject(R.id.webview)
	private WebView webview;

	@ViewInject(R.id.layout_web)
	private RelativeLayout layout_web;

	private String url;// 链接地址

	/** 商品信息 */
	private CommodityInfo commodityInfo;

	/** 视频全屏参数 */
	protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	private View customView;
	private FrameLayout fullscreenContainer;
	private WebChromeClient.CustomViewCallback customViewCallback;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_web_video_preview;
	}

	@Override
	protected void init() {
		commodityInfo = (CommodityInfo) getIntent().getSerializableExtra(Constant.Extra.COMMDITYINFO);
		if (commodityInfo == null) {
			commodityInfo = new CommodityInfo();
		}
		url = commodityInfo.getPreviewUrl();
		setWebview();
	}

	/** 设置webview */
	@SuppressLint("SetJavaScriptEnabled")
	private void setWebview() {
		final WebSettings settings = webview.getSettings();
		settings.setDomStorageEnabled(true);
		settings.setJavaScriptEnabled(true);
		settings.setLoadWithOverviewMode(true);
		settings.setUseWideViewPort(true);
		settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webview.loadUrl(url);
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtil.i(url);
				if (url.startsWith("http://") || url.startsWith("https://"))
					return false;
				else
					return true;
			}
		});
		webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
				super.onProgressChanged(view, newProgress);
			}

			/*** 视频播放相关的方法 **/

			@Override
			public View getVideoLoadingProgressView() {
				FrameLayout frameLayout = new FrameLayout(WebVideoPreviewActivity.this);
				frameLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
				return frameLayout;
			}

			@Override
			public void onShowCustomView(View view, CustomViewCallback callback) {
				showCustomView(view, callback);
			}

			@Override
			public void onHideCustomView() {
				hideCustomView();
			}
		});
	}

	/** 视频播放全屏 **/
	private void showCustomView(View view, CustomViewCallback callback) {
		// if a view already exists then immediately terminate the new one
		if (customView != null) {
			callback.onCustomViewHidden();
			return;
		}

		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		WebVideoPreviewActivity.this.getWindow().getDecorView();

		FrameLayout decor = (FrameLayout) getWindow().getDecorView();
		fullscreenContainer = new FullscreenHolder(WebVideoPreviewActivity.this);
		fullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
		decor.addView(fullscreenContainer, COVER_SCREEN_PARAMS);
		customView = view;
		setStatusBarVisibility(false);
		customViewCallback = callback;
	}

	/** 隐藏视频全屏 */
	private void hideCustomView() {
		if (customView == null) {
			return;
		}

		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setStatusBarVisibility(true);
		FrameLayout decor = (FrameLayout) getWindow().getDecorView();
		decor.removeView(fullscreenContainer);
		fullscreenContainer = null;
		customView = null;
		customViewCallback.onCustomViewHidden();
		webview.setVisibility(View.VISIBLE);
	}

	/** 全屏容器界面 */
	static class FullscreenHolder extends FrameLayout {

		public FullscreenHolder(Context ctx) {
			super(ctx);
			setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
		}

		@SuppressLint("ClickableViewAccessibility")
		@Override
		public boolean onTouchEvent(MotionEvent evt) {
			return true;
		}
	}

	private void setStatusBarVisibility(boolean visible) {
		int flag = visible ? 0 : WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setFlags(flag, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (!visible)
			hideVirtualKey();
		else
			showVirtualKey();

	}

	/**
	 * 隐藏Android底部的虚拟按键
	 */
	@SuppressLint("InlinedApi")
	private void hideVirtualKey() {
		// 隐藏虚拟按键，并且全屏
		if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower
																		// api
			View v = this.getWindow().getDecorView();
			v.setSystemUiVisibility(View.GONE);
		} else if (Build.VERSION.SDK_INT >= 19) {
			// for new api versions.
			View decorView = getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
					| View.SYSTEM_UI_FLAG_FULLSCREEN;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}

	/**
	 * 显示Android底部的虚拟按键
	 */
	private void showVirtualKey() {
		// 隐藏虚拟按键，并且全屏
		if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower
																		// api
			View v = this.getWindow().getDecorView();
			v.setSystemUiVisibility(View.VISIBLE);
		} else if (Build.VERSION.SDK_INT >= 19) {
			// for new api versions.
			View decorView = getWindow().getDecorView();
			int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
			decorView.setSystemUiVisibility(uiOptions);
		}
	}

	@OnClick(value = { R.id.header_left, R.id.header_right })
	@Override
	public void onClick(View arg0) {
		super.onClick(arg0);
		switch (arg0.getId()) {
		case R.id.header_left:
			if (webview.canGoBack()) {
				webview.goBack();
			} else {
				finish();
			}
			break;
		case R.id.header_right:
			postData();
			break;
		}
	}

	private void postData() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("memberId", UserInfoManager.getInstance().getUserId());// 产品id
		map.put("productId", commodityInfo.getId());// 产品id
		map.put("privewUrl", commodityInfo.getPreviewUrl());// 定制链接
		String URL = HttpURL.URL1 + HttpURL.SAVE_NFC;
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				ResponseJsonInfo<CommodityInfo> info = JsonUtil.fromJson(arg0.result, CommodityInfo.class);
				if (info != null && HttpCode.SUCESS.equals(info.getHttpCode())) {
					CommodityInfo comInfo = info.getBody();
					commodityInfo.setCustomId(comInfo.getCustomId());
					ActivitiesManager.getInstance().finishActivity(UploadActivity.class);
					toActivity(MyCustomizationTobuyActivity.class, Constant.Extra.CUSTOMIZATION_INFO, commodityInfo);// 预览
					finish();
					ScreenUtil.showToast(ContextUtil.getString(R.string.save_tip));
				} else {
					ScreenUtil.showToast(ContextUtil.getString(R.string.post_fail));
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
		// webview.goBack();
		// return true;
		// } else {
		// return super.onKeyDown(keyCode, event);
		// }
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			/** 回退键 事件处理 优先级:视频播放全屏-网页回退-关闭页面 */
			if (customView != null) {
				hideCustomView();
			} else if (webview.canGoBack()) {
				webview.goBack();
			} else {
				finish();
			}
			return true;
		default:
			return super.onKeyUp(keyCode, event);
		}
	}

	boolean isOnPause;

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			if (isOnPause) {
				if (webview != null) {
					webview.onResume();
				}
				isOnPause = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// if (getRequestedOrientation() !=
		// ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		// }
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		try {
			if (webview != null) {
				webview.onPause();
				isOnPause = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onDestroy() {
		if (webview != null) {
			webview.clearCache(true);
			webview.clearHistory();
			layout_web.removeAllViews();
			webview.setVisibility(View.GONE);
			webview.removeAllViews();
			webview.destroy();
			webview = null;
		}
		isOnPause = false;
		super.onDestroy();

	}

}
