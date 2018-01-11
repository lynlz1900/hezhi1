package com.wukong.hezhi.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ArticleTabInfo;
import com.wukong.hezhi.bean.ArticleTabInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.ui.fragment.FocusFragment;
import com.wukong.hezhi.ui.view.viewpagerindicator.TabPageIndicator;
import com.wukong.hezhi.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @ClassName: FocusActivity
 * @Description: TODO(盒知热点主页面)
 * @author HuZhiyin
 * @date 2017-7-28 上午9:44:41
 * 
 */
public class FocusActivity extends BaseActivity {

//	public String pageName = ContextUtil.getString(R.string.hezhi_foucus);
//	public String pageCode = "hezhi_foucus";

	@ViewInject(R.id.pager)
	private ViewPager pager;

	@ViewInject(R.id.indicator)
	private TabPageIndicator indicator;

	@ViewInject(R.id.title_tv)
	private TextView title_tv;

	private List<ArticleTabInfo> articleTabInfos;

	@OnClick(value = { R.id.header_left, R.id.search_rl })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_left:
			finish();
			break;
		case R.id.search_rl:
			toActivity(SearchArticleActivity.class);
			break;
		}
	}

	public void getArticleTabData() {
		showProgressDialog();
		String URL = HttpURL.URL1 + HttpURL.ARTICLE_MENU;
		HttpManager.getInstance().post(URL, null,
				new RequestCallBack<String>() {
					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onSuccess(ResponseInfo<String> responseInfo) {
						// TODO Auto-generated method stub
						dismissProgressDialog();
						ResponseJsonInfo info = JsonUtil.fromJson(
								responseInfo.result, ArticleTabInfos.class);
						if (info != null) {
							if (info.getHttpCode().equals(HttpCode.SUCESS)) {
								ArticleTabInfos infos = (ArticleTabInfos) info
										.getBody();
								articleTabInfos = infos.getDataList();
								initView();
							}
						}
					}

				});
	}

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_focus;
	}

	@Override
	protected void init() {
		articleTabInfos = (List<ArticleTabInfo>) getIntent()
				.getSerializableExtra(Constant.Extra.ARTICLE_TAB_INFO);
		initView();
	}

	private void initView() {
		if (articleTabInfos.size() == 1) {
			title_tv.setVisibility(View.VISIBLE);// 当只有一个item的时候，用这个title覆盖indicator
			title_tv.setText(articleTabInfos.get(0).getName());
		} else if (articleTabInfos.size() == 0) {// 启动页接口请求失败（无网络或者其他原因），将上次保存的
			articleTabInfos = new ArrayList<ArticleTabInfo>();
			ArticleTabInfo articleTabInfo = new ArticleTabInfo();
			articleTabInfo.setName("盒知看点");
			articleTabInfos.add(articleTabInfo);
			title_tv.setVisibility(View.VISIBLE);// 当只有一个item的时候，用这个title覆盖indicator
			title_tv.setText(articleTabInfos.get(0).getName());
		}

		FragmentManager fragmentManager = this.getSupportFragmentManager();// 这句是关键，fragment的里面嵌套fragment需要使用getChildFragmentManager()而不能用getSupportFragmentManager()
		FragmentPagerAdapter adapter = new TabPageIndicatorAdapter(
				fragmentManager);
		pager.setAdapter(adapter);
		indicator.setViewPager(pager);
	}

	/**
	 * ViewPager适配器
	 * 
	 * @author len
	 * 
	 */
	class TabPageIndicatorAdapter extends FragmentPagerAdapter {
		public TabPageIndicatorAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new FocusFragment();
			Bundle bundle = new Bundle();
			bundle.putString("tpye", articleTabInfos.get(position).getId());
			fragment.setArguments(bundle);
			return fragment;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return articleTabInfos.get(position % (articleTabInfos.size()))
					.getName();
		}

		@Override
		public int getCount() {
			return articleTabInfos.size();
		}
	}

}
