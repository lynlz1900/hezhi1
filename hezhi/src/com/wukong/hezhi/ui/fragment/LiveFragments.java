package com.wukong.hezhi.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.TabInfo;
import com.wukong.hezhi.bean.TabInfos;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.ui.activity.SearchLiveActivity;
import com.wukong.hezhi.ui.activity.UnityPlayerActivity;
import com.wukong.hezhi.ui.view.viewpagerindicator.TabPageIndicator;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.PermissionUtil;
import com.wukong.hezhi.utils.SPUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.util.List;

/**
 * 
 * @ClassName: LiveFragments
 * @Description: TODO(品牌直播，外层Fragment)
 * @author HuZhiyin
 * @date 2017-3-14 下午1:41:31
 * 
 */
public class LiveFragments extends BaseFragment {
	public String pageName = ContextUtil.getString(R.string.band_live);
	public String pageCode = "band_live";
	@ViewInject(R.id.pager)
	private ViewPager pager;

	@ViewInject(R.id.indicator)
	private TabPageIndicator indicator;

	@ViewInject(R.id.search_ll)
	private LinearLayout search_ll;

	/** Tab标题 */
	private List<TabInfo> tabInfos = Constant.TABINFOS;

	@OnClick(value = { R.id.search_ll,R.id.header_left })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.search_ll:
			toActivity(SearchLiveActivity.class);
			break;
		case R.id.header_left:
			toUnity();
			break;
		}
	}

	/*** 跳转到unit播放界面 **/
	private void toUnity() {
		if (!PermissionUtil.cameraPermission()) {
			ScreenUtil.showToast(ContextUtil.getString(R.string.photo_permission_tip));
			return;
		}
		toActivity(UnityPlayerActivity.class);
	}
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_lives, container,
				false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		initView();
		return rootView;
	}

	public void initView() {

		if (tabInfos != null && tabInfos.size() == 0) {
			TabInfos tabInfosCache = (TabInfos) SPUtil.getShareObject(
					ContextUtil.getContext(), HezhiConfig.SP_COMMON_CONFIG,
					Constant.Sp.TAB_INFO);
			if (tabInfosCache != null) {
				tabInfos.addAll(tabInfosCache.getArTypeList());
			}
		}
		FragmentManager fragmentManager = this.getChildFragmentManager();// 这句是关键，fragment的里面嵌套fragment需要使用getChildFragmentManager()而不能用getSupportFragmentManager()
		FragmentPagerAdapter adapter = new TabPageIndicatorAdapter(
				fragmentManager);
		pager.setAdapter(adapter);
		indicator.setViewPager(pager);

	}

	/** ViewPager适配器 */
	class TabPageIndicatorAdapter extends FragmentPagerAdapter {
		public TabPageIndicatorAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = new LiveFragment();
			Bundle bundle = new Bundle();
			// args.putString("arg", TITLE[position]);
			bundle.putString("tpye", tabInfos.get(position).getId());
			fragment.setArguments(bundle);

			return fragment;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return tabInfos.get(position).getName();
		}

		@Override
		public int getCount() {
			return tabInfos.size();
		}
	}

}
