package com.wukong.hezhi.ui.activity;

import java.util.ArrayList;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.MyFragmentPagerAdapter;
import com.wukong.hezhi.bean.ProductInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.ui.fragment.CustomFragment;
import com.wukong.hezhi.ui.fragment.DetailFragment;
import com.wukong.hezhi.ui.fragment.IntroduceFragment;
import com.wukong.hezhi.ui.fragment.LogisticsFragment;
import com.wukong.hezhi.ui.view.NoPreloadViewPager;
import com.wukong.hezhi.ui.view.NoPreloadViewPager.OnPageChangeListener;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * 
 * @ClassName: TraceActivity
 * @Description: TODO(溯源主页)
 * @author HuZhiyin
 * @date 2016-10-10 上午10:12:11
 * 
 */
public class TraceActivity extends BaseActivity {
	// public String pageName = ContextUtil.getString(R.string.suyuan_page);
	// public String pageCode = "suyuan_page";

	@ViewInject(R.id.pager)
	private NoPreloadViewPager viewPager;

	@ViewInject(R.id.main_radiogroup)
	private RadioGroup main_radiogroup;

	@ViewInject(R.id.main_rb_detail)
	private RadioButton main_rb_detail;

	private ProductInfo productInfo;
	/** 当前选中的fragment */
	private int currentFragmentState = 0;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_trace;
	}

	@Override
	protected void init() {
		productInfo = (ProductInfo) getIntent().getSerializableExtra(Constant.Extra.PVRESULTACTIVITY_PRODUCTINFO);
		initView();
	}

	public ProductInfo getProductInfo() {
		return productInfo;
	}

	private void initView() {

		main_radiogroup.setOnCheckedChangeListener(new myCheckChangeListener());// RadioGroup选中状态改变监听
		ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new IntroduceFragment());// 产品信息（溯源信息）
		fragmentList.add(new CustomFragment());// 定制信息
		fragmentList.add(new LogisticsFragment());// 物流信息
		fragmentList.add(new DetailFragment());// 详细详情

		// if (TextUtils.equals(NFCManager.authCode,
		// HezhiConfig.MAITAO_AUTHCODE)) {// 如果是茅台醇，隐藏物流信息
		// main_rb_detail.setVisibility(View.GONE);
		// } else {
		// main_rb_detail.setVisibility(View.VISIBLE);
		// fragmentList.add(new LogisticsFragment());// 物流信息
		// }
		viewPager.setAdapter(new MyFragmentPagerAdapter(this.getSupportFragmentManager(), fragmentList));
		viewPager.setOnPageChangeListener(new myOnPageChangeListener());
		viewPager.setOffscreenPageLimit(fragmentList.size() - 1);
		main_radiogroup.check(R.id.main_rb_pv);
	}

	/**
	 * RadioButton切换Fragment
	 */
	private class myCheckChangeListener implements RadioGroup.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {
			case R.id.main_rb_pv:
				currentFragmentState = 0;
				break;
			case R.id.main_rb_custom:
				currentFragmentState = 1;
				break;
			case R.id.main_rb_logstics:
				currentFragmentState = 2;
				break;
			case R.id.main_rb_detail:
				currentFragmentState = 3;
				break;

			}
			viewPager.setCurrentItem(currentFragmentState, false);
		}
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
				main_radiogroup.check(R.id.main_rb_pv);
				break;
			case 1:
				main_radiogroup.check(R.id.main_rb_custom);
				break;
			case 2:
				main_radiogroup.check(R.id.main_rb_logstics);
				break;
			case 3:
				main_radiogroup.check(R.id.main_rb_detail);
				break;
		

			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	}

	@OnClick(value = { R.id.header_left })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_left:
			finish();
			break;
		}
	}

}
