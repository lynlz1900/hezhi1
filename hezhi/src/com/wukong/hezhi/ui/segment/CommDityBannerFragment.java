package com.wukong.hezhi.ui.segment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.ui.fragment.BaseFragment;

/**
 * 
 * @ClassName: CommDityBannerFragment
 * @Description: TODO(商品详情轮播图)
 * @author HuZhiyin
 * @date 2017-8-14 上午9:33:32
 * 
 */
public class CommDityBannerFragment extends BaseFragment {

	@ViewInject(R.id.view_pager)
	private ViewPager view_pager;

	@ViewInject(R.id.select_pic_tv)
	private TextView select_pic_tv;

	private List<String> imgUrls = new ArrayList<String>();
	/** 商品信息 */
	private CommodityInfo commodityInfo;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_commdity_banner, container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		commodityInfo = (CommodityInfo) getArguments().getSerializable("commodityInfo");
		init();
		return rootView;
	}

	private void init() {
		if (commodityInfo == null) {
			return;
		}
		String urls = commodityInfo.getBannerUrl();
		String[] url = new String[] {};
		try {
			url = urls.split(",");
		} catch (Exception e) {
			return;
		}

		for (int i = 0; i < url.length; i++) {
			imgUrls.add(url[i]);
		}

		FragmentManager fragmentManager = this.getChildFragmentManager();// 这句是关键，fragment的里面嵌套fragment需要使用getChildFragmentManager()而不能用getSupportFragmentManager()
		FragmentPagerAdapter adapter = new TabPageIndicatorAdapter(fragmentManager);
		view_pager.setAdapter(adapter);
		view_pager.addOnPageChangeListener(new MyOnPageChangeListener());

		if (imgUrls.size() > 1) {
			select_pic_tv.setText(1 + "/" + imgUrls.size());
		} else {
			select_pic_tv.setVisibility(View.GONE);
		}
	}

	/** viewpager变化监听 */
	private class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onPageSelected(int arg0) {
			// TODO Auto-generated method stub
			int currentPos = arg0 + 1;
			int total = imgUrls.size();
			select_pic_tv.setText(currentPos + "/" + total);
		}

	}

	/** ViewPager适配器 */
	private class TabPageIndicatorAdapter extends FragmentPagerAdapter {
		public TabPageIndicatorAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			Fragment fragment = new CommdityBannerImgFragment();
			Bundle bundle = new Bundle();
			bundle.putString("imgUrl", imgUrls.get(position).toString());
			fragment.setArguments(bundle);
			return fragment;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return null;
		}

		@Override
		public int getCount() {
			return imgUrls.size();
		}
	}
}
