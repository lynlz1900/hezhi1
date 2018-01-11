package com.wukong.hezhi.ui.segment;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.ui.fragment.BaseFragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class CommdityWebFragment extends BaseFragment {
	@ViewInject(R.id.layout_web)
	private LinearLayout layout_web;

	@ViewInject(R.id.webview)
	private WebView webview;

	@ViewInject(R.id.myProgressBar)
	private ProgressBar myProgressBar;

	/** 商品信息 */
	private CommodityInfo commodityInfo;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_commdity_web, container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		commodityInfo = (CommodityInfo) getArguments().getSerializable("commodityInfo");
		initView();
		return rootView;
	}

	public void initView() {
		if (commodityInfo == null) {
			return;
		}

		String url = commodityInfo.getUrl();
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setLoadWithOverviewMode(true);
		webview.getSettings().setUseWideViewPort(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webview.loadUrl(url);
		webview.setWebChromeClient(new MyWebChromeClient());
	}

	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			// TODO Auto-generated method stub
			@SuppressWarnings("unused")
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
