package com.wukong.hezhi.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.LogUtil;

/***
 *
 * @ClassName: WebViewActivity
 * @Description: TODO(网页)
 * @author HuZhiyin
 * @date 2017-2-28 上午9:50:22
 *
 */
public class WebViewActivity extends BaseActivity {
    public String pageName = ContextUtil.getString(R.string.web);
    public String pageCode = "web";

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

    /**
     * 设置webview
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void setWebview() {
        final WebSettings settings = webview.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
        // android 5.0以上默认不支持Mixed Content
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(
                    WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
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

            @Override
            public void onReceivedSslError(WebView view,
                                           SslErrorHandler handler, SslError error) {
                handler.proceed();// 接受所有网站的证书
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                if (newProgress == 100) {
                    myProgressBar.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(webview.getTitle())) {// 如果webview的头部有标题，则设置webview自带的标题
                        headView.setTitleStr(title);
                    }

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

    @OnClick(value = {R.id.header_left, R.id.header_right})
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

}
