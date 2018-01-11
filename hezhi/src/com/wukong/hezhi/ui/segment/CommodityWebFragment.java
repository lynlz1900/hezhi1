package com.wukong.hezhi.ui.segment;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.ui.fragment.BaseFragment;
import com.wukong.hezhi.ui.view.ObservableScrollView;
import com.wukong.hezhi.ui.view.ObservableScrollView.OnScollChangedListener;
import com.wukong.hezhi.utils.ScreenUtil;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * 
 * @ClassName: CommodityDetailFragment
 * @Description: TODO(商品tab详情页)
 * @author HuangFeiFei
 * @date 2017-12-13
 * 
 */
public class CommodityWebFragment extends BaseFragment {
	@ViewInject(R.id.layout_web)
	private LinearLayout layout_web;

	@ViewInject(R.id.webview)
	private WebView webview;

	@ViewInject(R.id.myProgressBar)
	private ProgressBar myProgressBar;

	@ViewInject(R.id.empty_ll)
	private LinearLayout empty_ll;
	
	@ViewInject(R.id.top_iv)
	private ImageView top_iv;
	
	@ViewInject(R.id.sclv)
	private ObservableScrollView sclv;
	
	/** 商品信息 */
	private CommodityInfo commodityInfo;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_commdity_web, container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		commodityInfo = ((CommodityInfoFragment)getParentFragment()).getCommodityInfo();
		initView();
		return rootView;
	}

	@SuppressLint("SetJavaScriptEnabled")
	public void initView() {
		if (commodityInfo == null) {
			return;
		}

		String url = commodityInfo.getUrl();
		
		if(TextUtils.isEmpty(url)){
			webview.setVisibility(View.GONE);
			myProgressBar.setVisibility(View.GONE);
			empty_ll.setVisibility(View.VISIBLE);
		}else{
			webview.setVisibility(View.VISIBLE);
			myProgressBar.setVisibility(View.VISIBLE);
			empty_ll.setVisibility(View.GONE);
			
			webview.getSettings().setJavaScriptEnabled(true);
			webview.getSettings().setLoadWithOverviewMode(true);
			webview.getSettings().setUseWideViewPort(true);
			webview.getSettings().setBuiltInZoomControls(true);
			webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
			webview.loadUrl(url);
			webview.setWebChromeClient(new MyWebChromeClient());
		}
		
		sclv.setOnScollChangedListener(new MyScrollListener());
	}

	/** 滑动的监听 */
	private class MyScrollListener implements OnScollChangedListener {

		@Override
		public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
			// TODO Auto-generated method stub
			if (y >= ScreenUtil.getScreenHeight()) {// 当移动的距离大于屏幕的高度
				top_iv.setVisibility(View.VISIBLE);
			} else {
				top_iv.setVisibility(View.INVISIBLE);
			}
		}
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

	@OnClick(value = { R.id.top_iv})
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.top_iv:// 回到最顶端
			sclv.smoothScrollTo(0, 0);
			break;
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
