package com.wukong.hezhi.ui.activity;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.UnityManger;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.LogUtil;
import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/***
 * 
 * @ClassName: WebARListActivity
 * @Description: TODO(ar推荐列表列表H5网页)
 * @author HuangFeiFei
 * @date 2017-12-27 
 */
public class WebARListActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.ar_list);
	public String pageCode = "ar_list";
	
	@ViewInject(R.id.webview)
	private WebView webview;

	@ViewInject(R.id.myProgressBar)
	private ProgressBar myProgressBar;

	@ViewInject(R.id.layout_web)
	private LinearLayout layout_web;

	private String url;// 链接地址
	private String title;// 头部信息

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_webview;
	}

	@Override
	protected void init() {
		url = getIntent().getStringExtra(Constant.Extra.WEB_URL);
		title = getIntent().getStringExtra(Constant.Extra.WEBVIEW_TITLE);
		if (TextUtils.isEmpty(title)) {
			title = "";
		}
		headView.setTitleStr(title);
        headView.setRihgtTitleText("", R.drawable.icon_close1);
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
		LogUtil.i(url);
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
	       public void onPageFinished(WebView view, String url) {  
			 	LogUtil.i(view.getTitle());
			 	headView.setTitleStr(view.getTitle());
	       }
			 
			@Override
			public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				myProgressBar.setVisibility(View.GONE);
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

	@OnClick(value = { R.id.header_left, R.id.header_right})
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
	    public void prizeGame(){
	    }
		
		 @JavascriptInterface
	    public void prizeGame(String pare){
			 LogUtil.i(pare);
			 if(pare == null) return;
			 
			String unityStr = UnityManger.getInstance().toUTFormat(
						UnityManger.AR_LIST, pare);
			JumpActivityManager.getInstance().toActivity(mActivity,
					UnityPlayerActivity.class,
					Constant.Extra.UNITY_INFO, unityStr);
	    }
	} 
}
