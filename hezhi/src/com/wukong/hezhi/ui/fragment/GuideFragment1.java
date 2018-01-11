package com.wukong.hezhi.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.ViewUtils;
import com.wukong.hezhi.R;
import com.wukong.hezhi.utils.ContextUtil;

/**
 * 
 * @ClassName: GuideFragment1
 * @Description: TODO(第一个引导页)
 * @author HuZhiyin
 * @date 2016-9-13 下午3:55:22
 * 
 */
public class GuideFragment1 extends BaseFragment {
	public String pageName = ContextUtil.getString(R.string.guide1);
	public String pageCode ="guide1";
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_guide1, container,
				false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		initView();
		return rootView;
	}

	public void initView() {
	}

}
