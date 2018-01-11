package com.wukong.hezhi.ui.fragment;

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
import com.wukong.hezhi.bean.ProductInfo;
import com.wukong.hezhi.ui.activity.TraceActivity;
import com.wukong.hezhi.utils.ContextUtil;

/**
 * 
 * @ClassName: DetailFragment
 * @Description: TODO(产品详情介绍)
 * @author HuZhiyin
 * @date 2016-9-13 下午3:52:31
 * 
 */
public class DetailFragment extends BaseFragment {
	public String pageName = ContextUtil.getString(R.string.product_detail);
	public String pageCode = "product_detail";

	@ViewInject(R.id.webview)
	private WebView webview;

	@ViewInject(R.id.layout_web)
	private LinearLayout layout_web;

	@ViewInject(R.id.myProgressBar)
	private ProgressBar myProgressBar;

	private ProductInfo productInfo;

	private boolean isFirst = true;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_detail, container,
				false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		return rootView;
	}

	public void initView() {
		productInfo = ((TraceActivity) getActivity()).getProductInfo();
		if (productInfo == null) {
			return;
		}
		String url = productInfo.getMaterialExtInfo();
		webview.getSettings().setJavaScriptEnabled(true); // 设置WebView属性，能够执行Javascript脚本
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setUseWideViewPort(true);
		webview.getSettings()
				.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webview.loadUrl(url); // 加载需要显示的网页
		webview.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return super.shouldOverrideUrlLoading(view, url);
			}
		});
		webview.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				// TODO Auto-generated method stub
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
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		if (isFirst && isVisibleToUser) {// 显示当前fragment才加载数据，加载一次后，不再加载。
			initView();
			isFirst = false;
		}

		if (webview != null) {
			if (isVisibleToUser) {
				webview.onResume();
			} else {// 不在当前的fragment，暂停webview。
				webview.onPause();
			}
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
	public void onDestroyView() {
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
		super.onDestroyView();

	}
}
