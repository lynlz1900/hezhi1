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
 * @ClassName: GuideFragment2
 * @Description: TODO(第二个引导页)
 * @author HuZhiyin
 * @date 2016-9-13 下午3:55:22
 * 
 */
public class GuideFragment2 extends BaseFragment {
	public String pageName = ContextUtil.getString(R.string.guide2);
	public String pageCode ="guide2";
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_guide2, container,
				false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		initView();
		return rootView;
	}

	public void initView() {
	}

}
