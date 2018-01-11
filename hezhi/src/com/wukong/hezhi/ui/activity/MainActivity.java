package com.wukong.hezhi.ui.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.LiveFragmentAdapter;
import com.wukong.hezhi.adapter.MyFragmentPagerAdapter;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.ui.fragment.CustomizationFragment;
import com.wukong.hezhi.ui.fragment.DistingiushFragment;
import com.wukong.hezhi.ui.fragment.FindFragment;
import com.wukong.hezhi.ui.fragment.LiveFragments;
import com.wukong.hezhi.ui.fragment.PersonalFragment;
import com.wukong.hezhi.ui.view.NoScrollViewPager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.StatusBarUtil;

import java.util.ArrayList;

/**
 * 
 * @ClassName: MainActivity
 * @Description: TODO(盒知主页面)
 * @author HuZhiyin
 * @date 2016-9-13 上午8:46:58
 * 
 */
public class MainActivity extends BaseActivity {
	// public String pageName = ContextUtil.getString(R.string.main_page);
	// public String pageCode = "main_page";

	@ViewInject(R.id.pager)
	private NoScrollViewPager viewPager;

	@ViewInject(R.id.main_radiogroup)
	private RadioGroup main_radiogroup;

	@ViewInject(R.id.red_point0)
	private ImageView red_point0;// 第一个tab的小红点

	@ViewInject(R.id.red_point1)
	private ImageView red_point1;// 第二个tab的小红点

	@ViewInject(R.id.red_point2)
	private ImageView red_point2;// 第三个tab的小红点

	@ViewInject(R.id.red_point3)
	private ImageView red_point3;// 第四个tab的小红点

	@ViewInject(R.id.red_point4)
	private ImageView red_point4;// 第五个tab的小红点

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
		return R.layout.activity_main;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		setSwipeBackEnable(false);// 主页禁止侧滑消除
		initView();// 初始化控件
	}

	private void initView() {
		main_radiogroup.setOnCheckedChangeListener(new BottomCheckChangeListener());// RadioGroup选中状态改变监听

		ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new CustomizationFragment());// 盒知
		fragmentList.add(new DistingiushFragment());// 鉴真
		fragmentList.add(new LiveFragments());// 品牌直播
		fragmentList.add(new FindFragment());// 发现
		fragmentList.add(new PersonalFragment());// 我

		viewPager.setAdapter(new MyFragmentPagerAdapter(this.getSupportFragmentManager(), fragmentList)); // ViewPager设置适配器
		viewPager.setOffscreenPageLimit(fragmentList.size() - 1);
//		main_radiogroup.check(R.id.distinguish_rb); // 默认使ViewPager显示第二个Fragment
		main_radiogroup.check(R.id.hezhi_rb); // 默认使ViewPager显示第二个Fragment
	}

	/**
	 * RadioButton切换Fragment
	 */
	private class BottomCheckChangeListener implements RadioGroup.OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			switch (checkedId) {

			case R.id.hezhi_rb:
				currentFragmentState = 0;
				ObserveManager.getInstance().notifyState(CustomizationFragment.class, MainActivity.class, null);// 通知CustomizationFragment
				StatusBarUtil.setColor(mActivity, ContextUtil.getColor(R.color.white));// 设置状态栏颜色
				break;
			case R.id.distinguish_rb:
				currentFragmentState = 1;
				ObserveManager.getInstance().notifyState(DistingiushFragment.class, MainActivity.class, null);// 通知DistingiushFragment
				StatusBarUtil.setColor(mActivity, ContextUtil.getColor(R.color.white));// 设置状态栏颜色
				break;
			case R.id.live_rb:
				currentFragmentState = 2;
				ObserveManager.getInstance().notifyState(LiveFragmentAdapter.class, MainActivity.class, null);// 通知LiveFragmentAdapter
				StatusBarUtil.setColor(mActivity, ContextUtil.getColor(R.color.white));// 设置状态栏颜色
				break;
			case R.id.find_rb:
				currentFragmentState = 3;
				ObserveManager.getInstance().notifyState(FindFragment.class, MainActivity.class, null);// 通知FindFragment
				StatusBarUtil.setColor(mActivity, ContextUtil.getColor(R.color.white));// 设置状态栏颜色
				break;
			case R.id.person_rb:
				currentFragmentState = 4;
				ObserveManager.getInstance().notifyState(PersonalFragment.class, MainActivity.class, null);// 通知PersonalFragment
				StatusBarUtil.setColor(mActivity, ContextUtil.getColor(R.color.base));// 设置状态栏颜色
				break;
			}
			setViewPagerCurrentImen();
		}
	}

	/** 设置选中的Viewpager条目 */
	private void setViewPagerCurrentImen() {
		viewPager.setCurrentItem(currentFragmentState, false);// false,取消viewpager滑动的动画
		setRedPoint(currentFragmentState, false);// 选中后后，隐藏当前TAB上附着的小红点
	}

	/** 设置附着在tab上的小红点的显示和隐藏 */
	private void setRedPoint(int point, boolean visble) {
		switch (point) {
		case 0:

			if (visble) {
				red_point0.setVisibility(View.VISIBLE);
			} else {
				red_point0.setVisibility(View.GONE);
			}

			break;
		case 1:
			if (visble) {
				red_point1.setVisibility(View.VISIBLE);
			} else {
				red_point1.setVisibility(View.GONE);
			}
			break;
		case 2:
			if (visble) {
				red_point2.setVisibility(View.VISIBLE);
			} else {
				red_point2.setVisibility(View.GONE);
			}
			break;
		case 3:
			if (visble) {
				red_point3.setVisibility(View.VISIBLE);
			} else {
				red_point3.setVisibility(View.GONE);
			}
			break;
		case 4:
			if (visble) {
				red_point4.setVisibility(View.VISIBLE);
			} else {
				red_point4.setVisibility(View.GONE);
			}
			break;

		}
	}

	long firstPressTime = 0; // 记录第一次点击退出的时间

	/***
	 * 重写此方法，点击后退键后，程序隐藏后台。
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - firstPressTime > 2000) {
				ScreenUtil.showToast(ContextUtil.getString(R.string.click_two));
				firstPressTime = System.currentTimeMillis();
			} else {
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * unity切换横竖屏幕的时候，需要复写此方法，且
	 * android:configChanges="orientation|keyboardHidden|screenSize，否则会出问题
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		// TODO Auto-generated method stub
		super.updateState(notifyTo, notifyFrom, msg);
		if (notifyTo.equals(getClass())) {
			// setRedPoint((int) msg, true);// 显示小红点
			if (notifyFrom.equals(OrderPayResultActivity.class) || notifyFrom.equals(OrderPayActivity.class)
					|| notifyFrom.equals(MyCustomizationTobuyActivity.class)) {
				try {
					String jumpFlag = (String) msg;
					LogUtil.i(jumpFlag);
					toAc(jumpFlag);
				} catch (Exception e) {
					LogUtil.i(e.toString());
				}
			}
		}
	}

	/** 页面跳转 */
	private void toAc(String jumpFlag) {
		if (jumpFlag.equals(Constant.JumpToAc.ACTIVITY_ORDERCUST)) { // 跳转到我的定制页面
			main_radiogroup.check(R.id.person_rb);
			toActivity(OrderCustomActivity.class);
		} else if (jumpFlag.equals(Constant.JumpToAc.ACTIVITY_COMMODITY)) {// 跳转到选择定制的商品页面
			main_radiogroup.check(R.id.find_rb);
			toActivity(CommodityActivity.class);
		}
	}

}
