package com.wukong.hezhi.ui.activity;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ShareInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.ShareWindows;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.PackageUtil;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/***
 * 
 * @ClassName: WebViewActivity
 * @Description: TODO(大转盘抽奖H5网页)
 * @author HuangFeiFei
 * @date 2017-11-23 16:15
 * 
 */
public class WebPrizeActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.web_prize);
	public String pageCode = "web_prize";
	
	@ViewInject(R.id.text_no)
	private TextView text_no;
	
	@ViewInject(R.id.webview)
	private WebView webview;

	@ViewInject(R.id.myProgressBar)
	private ProgressBar myProgressBar;

	@ViewInject(R.id.layout_web)
	private RelativeLayout layout_web;

	private String url;// 链接地址
	private String title;// 头部信息

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_web_prize;
	}

	@Override
	protected void init() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//全屏
		url = getIntent().getStringExtra(Constant.Extra.WEB_URL);
		title = getIntent().getStringExtra(Constant.Extra.WEBVIEW_TITLE);
		if (TextUtils.isEmpty(title)) {
			title = "";
		}
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
		webview.loadUrl(url+"&memberId=" + UserInfoManager.getInstance().getUserId() + "&deviceId=" + PackageUtil.getPhoneId());
		LogUtil.i(url+"&memberId=" + UserInfoManager.getInstance().getUserId() + "&deviceId=" + PackageUtil.getPhoneId());
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtil.i(url);
				if (url.startsWith("http://") || url.startsWith("https://"))
					return false;
				else
					return true;
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				text_no.setVisibility(View.VISIBLE);
				myProgressBar.setVisibility(View.GONE);
				webview.setVisibility(View.GONE);
			}
		});
		webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					myProgressBar.setVisibility(View.GONE);
				} else {
					if (View.GONE == myProgressBar.getVisibility()) {
						myProgressBar.setVisibility(View.VISIBLE);
					}
					myProgressBar.setProgress(newProgress);
				}
				super.onProgressChanged(view, newProgress);
			}
		});
		
		webview.addJavascriptInterface(new prizeJs(), "prize");
	}

	@OnClick(value = { R.id.header_left, R.id.header_right, R.id.close_iv})
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
			finish();
			break;
		case R.id.close_iv:
			if (webview.canGoBack()) {
				webview.goBack();
			} else {
				finish();
			}
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
			webview.goBack();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
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

	public class prizeJs{
		
		public prizeJs(){
			
		}
		
		 @JavascriptInterface
	    public void prizeShared(){
			share();
	    }
	} 
	
	 private void share() {
		 this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String user = "";
				if(UserInfoManager.getInstance().getUserInfo() != null)
					user = UserInfoManager.getInstance().getUserInfo().getNickName();
				ShareInfo shareInfo = new ShareInfo();
		        shareInfo.setTitle(ContextUtil.getString(R.string.prize_zp_shared_title));
		        shareInfo.setUrl(url);
		        shareInfo.setImagUrl(HezhiConfig.HEZHI_LOGO_URL);
		        shareInfo.setDescription(user + ContextUtil.getString(R.string.prize_zp_shared_content));
		        final View view = getWindow().getDecorView().findViewById(
		                android.R.id.content);// 获取一个view,popubwindow会用到
		        ShareWindows.getInstance().show(view, shareInfo);
			}
		});
		
	 }
}
