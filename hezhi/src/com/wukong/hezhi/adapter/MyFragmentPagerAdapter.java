package com.wukong.hezhi.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @ClassName: myFragmentPagerAdapter
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author HuZhiyin
 * @date 2016-9-13 下午4:09:29
 * 
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> list;

	public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> list) {
		super(fm);
		if (list == null) {
			this.list = new ArrayList<Fragment>();
		} else {
			this.list = list;
		}
	}

	@Override
	public Fragment getItem(int position) {
		return list.get(position);
	}

	@Override
	public int getCount() {
		return list.size();
	}
}