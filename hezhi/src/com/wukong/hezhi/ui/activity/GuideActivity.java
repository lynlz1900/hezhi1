package com.wukong.hezhi.ui.activity;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.MyFragmentPagerAdapter;
import com.wukong.hezhi.ui.fragment.GuideFragment1;
import com.wukong.hezhi.ui.fragment.GuideFragment2;
import com.wukong.hezhi.ui.fragment.GuideFragment3;
import com.wukong.hezhi.utils.ContextUtil;

/**
 * 
 * @ClassName: GuideActivity
 * @Description: TODO(引导页)
 * @author HuZhiyin
 * @date 2017-5-24 上午8:33:40
 * 
 */
public class GuideActivity extends BaseActivity {

	public String pageName = ContextUtil.getString(R.string.guide);
	public String pageCode = "guide";

	@ViewInject(R.id.view_pager)
	private ViewPager view_pager;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_guide;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		ArrayList<Fragment> fragmentList = new ArrayList<Fragment>();
		fragmentList.add(new GuideFragment1());
		fragmentList.add(new GuideFragment2());
		fragmentList.add(new GuideFragment3());
		view_pager.setAdapter(new MyFragmentPagerAdapter(this
				.getSupportFragmentManager(), fragmentList));
		view_pager.setOffscreenPageLimit(fragmentList.size() - 1);
	}

}
