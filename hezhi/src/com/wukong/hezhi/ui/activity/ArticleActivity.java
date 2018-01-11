package com.wukong.hezhi.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ArticleInfo;
import com.wukong.hezhi.bean.ShareInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.segment.ArticleEditCommentFragment;
import com.wukong.hezhi.ui.segment.ArticleWebFragment;
import com.wukong.hezhi.ui.view.ShareWindows;
import com.wukong.hezhi.utils.ContextUtil;

/**
 * 
 * @ClassName: ArticleActivity
 * @Description: TODO(文章)
 * @author HuZhiyin
 * @date 2017-6-16 上午11:51:47
 * 
 */
public class ArticleActivity extends BaseActivity {

	public String pageName = ContextUtil.getString(R.string.article_more);
	public String pageCode = "article_more";

	@ViewInject(R.id.header_title)
	TextView header_title;

	private ArticleWebFragment articleWebFragment;
	private ArticleEditCommentFragment articleEditCommentFragment;
	private ArticleInfo info;

	@OnClick(value = { R.id.header_left, R.id.header_right })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_left:
			WebView webview = articleWebFragment.getWebView();
			if (webview != null && webview.canGoBack()) {
				webview.goBack();
			} else {
				finish();
			}
			break;
		case R.id.header_right:
			share();
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		WebView webview = articleWebFragment.getWebView();
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (webview != null && webview.canGoBack()) {
				webview.goBack();
			} else {
				finish();
			}
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}

	}

	private void share() {
		if (info != null) {
			WebView webview = articleWebFragment.getWebView();
			String url = webview.getUrl();
			url = HttpURL.URL1 + HttpURL.SHARE_ARTICLE + "?articleId="
					+ info.getId();
			String title = webview.getTitle();
			ShareInfo shareInfo = new ShareInfo();
			if(UserInfoManager.getInstance().getUserInfo() == null)
				shareInfo.setTitle(ContextUtil.getString(R.string.article_shared));
			else
				shareInfo.setTitle(UserInfoManager.getInstance().getUserInfo().getNickName()
						+ContextUtil.getString(R.string.article_shared));
			shareInfo.setUrl(url);
			shareInfo.setImagUrl(info.getThumbnail());
			shareInfo.setDescription(title);
			final View view = getWindow().getDecorView().findViewById(
					android.R.id.content);// 获取一个view,popubwindow会用到
			ShareWindows.getInstance().show(view, shareInfo);
		}
	}

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_article;
	}

	@Override
	public void init() {
//		header_title.setText(ContextUtil.getString(R.string.hezhi_foucus));
		info = (ArticleInfo) getIntent().getSerializableExtra(
				Constant.Extra.ARTICLE);
		addFragment();
	}

	private void addFragment() {
		Bundle bundle = new Bundle();
		bundle.putSerializable("info", info);

		FragmentManager fm = this.getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		articleWebFragment = new ArticleWebFragment();
		articleWebFragment.setArguments(bundle);
		transaction.replace(R.id.comment_fr, articleWebFragment);
		
		articleEditCommentFragment = new ArticleEditCommentFragment();
		articleEditCommentFragment.setArguments(bundle);
		transaction.replace(R.id.edit_comment_fr, articleEditCommentFragment);
		
		transaction.commitAllowingStateLoss();
	}

}
