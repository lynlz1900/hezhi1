package com.wukong.hezhi.ui.segment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ArticleInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.ui.activity.ArticleActivity;
import com.wukong.hezhi.ui.activity.WebViewActivity;
import com.wukong.hezhi.ui.fragment.BaseFragment;
import com.wukong.hezhi.utils.LogUtil;

/**
 * 
 * @ClassName: ArticleWebFragment
 * @Description: TODO(文章主体部分)
 * @author HuZhiyin
 * @date 2016-9-13 下午3:55:22
 * 
 */
public class ArticleWebFragment extends BaseFragment {
	@ViewInject(R.id.myProgressBar)
	private ProgressBar myProgressBar;

	@ViewInject(R.id.webview)
	private WebView webview;

	@ViewInject(R.id.layout_web)
	private LinearLayout layout_web;

	private ArticleInfo info = new ArticleInfo();

	public WebView getWebView() {
		return webview;
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_article_web,
				container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		init();
		return rootView;
	}

	public void init() {
		info = (ArticleInfo) getArguments().get("info");
		initView();
	}

	public void initView() {
		setHeaderView();
	}

	private void setHeaderView() {

		String url = info.getUrl();
		LogUtil.i(url);
		// String url = "https://www.baidu.com";
		// 设置WebView属性，能够执行Javascript脚本
		webview.getSettings().setJavaScriptEnabled(true);// 设置为false否则shouldOverrideUrlLoading会执行两次
		// 自适应屏幕
		// webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setUseWideViewPort(true);
		webview.getSettings()
				.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		// 加载需要显示的网页
		webview.loadUrl(url);
		// 设置Web视图
		webview.setWebViewClient(new MyWebViewClient());
		webview.setWebChromeClient(new MyWebChromeClient());

	}

	public static String SEPARATOR = "&hezhi_app_content/";

	private boolean hasLoad = true;// 因为shouldOverrideUrlLoading会加载两次，如果将setJavaScriptEnabled(false)，又会导致界面不能更新。所以暂且用一个标记为控制。

	private class MyWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			LogUtil.i(url);
			// url =
			// "https://test.wukongvision.com:200/remoteMobile/article/shareArticle?articleId=79&hezhi_app_content/articleId=79";
			if (hasLoad) {
				hasLoad = false;
				if (url.contains(SEPARATOR)) {// 如果url包含有约定的格式
					try {
						String[] strArr = url.split("\\&hezhi_app_content\\/");
						String url1 = strArr[0];
						String content = strArr[1];
						String id = content.split("articleId=")[1];
						ArticleInfo articleInfo = new ArticleInfo();
						articleInfo.setId(id);
						articleInfo.setUrl(url);
						toActivity(ArticleActivity.class,
								Constant.Extra.ARTICLE, articleInfo);
					} catch (Exception e) {
						toActivity(WebViewActivity.class,
								Constant.Extra.WEB_URL, url);
						return true;
					}

				} else {
					toActivity(WebViewActivity.class, Constant.Extra.WEB_URL,
							url);
				}
			}
			return true;
		}
	}

	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			String url = view.getUrl();
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
		hasLoad = true;// 每次回到页面，都置为true
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
