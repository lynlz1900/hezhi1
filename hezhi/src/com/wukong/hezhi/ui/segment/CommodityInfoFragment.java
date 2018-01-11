package com.wukong.hezhi.ui.segment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.MyFragmentPagerAdapter;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.ui.activity.CommodityInfoActivity;
import com.wukong.hezhi.ui.activity.CommodityInfoActivity.onKeydown;
import com.wukong.hezhi.ui.fragment.BaseFragment;
import com.wukong.hezhi.ui.view.NoPreloadViewPager;
import com.wukong.hezhi.ui.view.NoPreloadViewPager.OnPageChangeListener;

import java.util.ArrayList;

/**
 * 
 * @ClassName: CommodityInfoFragment
 * @Description: TODO(商品详情，tab内容显示界面)
 * @author HuangFeiFei
 * @date 2017-12-13
 * 
 */
public class CommodityInfoFragment extends BaseFragment {
	
	@ViewInject(R.id.pager)
	private NoPreloadViewPager viewPager;

	@ViewInject(R.id.main_radiogroup)
	private RadioGroup main_radiogroup;

	@ViewInject(R.id.main_rb_detail)
	private RadioButton main_rb_detail;
	
	/** 商品信息 */
	private CommodityInfo commodityInfo;

	/** 当前选中的fragment */
	private int currentFragmentState = 0;
	
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_commodity_info, container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		commodityInfo = (CommodityInfo) getArguments().getSerializable("commodityInfo");
		initView();
		return rootView;
	}
	
	public CommodityInfo getCommodityInfo() {
		return commodityInfo;
	}
	
	public RadioGroup getRadioGroup() {
		return main_radiogroup;
	}
	
	public int getViewId() {
		return R.id.main_rb_appraise;
	}
	
	private void initView() {
		if(commodityInfo != null)
			addFragment();
		
		((CommodityInfoActivity) getActivity()).setKeyDown(new onKeydown() {
			@Override
			public void onKeyDown() {
				gotoBack();
			}
		});
	}

	private void addFragment() {
		main_radiogroup.setOnCheckedChangeListener(new myCheckChangeListener());// RadioGroup选中状态改变监听
		ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new CommodityDetailFragment());// 商品
		fragmentList.add(new CommodityWebFragment());// 详情
		fragmentList.add(new CommodityAppraiseFragment());// 评价

		viewPager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList));
		viewPager.setOnPageChangeListener(new myOnPageChangeListener());
		viewPager.setOffscreenPageLimit(2);
		main_radiogroup.check(R.id.main_rb_commodity);
	}

	/**
	 * MapViewPager切换Fragment,RadioGroup做相应变化
	 */
	private class myOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			switch (position) {
			case 0:
				main_radiogroup.check(R.id.main_rb_commodity);
				break;
			case 1:
				main_radiogroup.check(R.id.main_rb_detail);
				break;
			case 2:
				main_radiogroup.check(R.id.main_rb_appraise);
				break;
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	}
	
	/**
	 * RadioButton切换Fragment
	 */
	private class myCheckChangeListener implements RadioGroup.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.main_rb_commodity:
				currentFragmentState = 0;
				break;
			case R.id.main_rb_detail:
				currentFragmentState = 1;
				break;
			case R.id.main_rb_appraise:
				currentFragmentState = 2;
				break;
			}
			viewPager.setCurrentItem(currentFragmentState, true);
		}
	}
	
	@OnClick(value = { R.id.header_left })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_left:
			gotoBack();
			break;
		}
	}
	
	/**  返回功能 **/
	private void gotoBack(){
		if(currentFragmentState == 2 || currentFragmentState == 1){
			currentFragmentState = 0;
			viewPager.setCurrentItem(currentFragmentState, true);
		}else{
			getActivity().finish();
		}
	}
}
