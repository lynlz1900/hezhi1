package com.wukong.hezhi.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.ui.activity.MainActivity;
import com.wukong.hezhi.utils.ContextUtil;

/**
 * 
 * @ClassName: GuideFragment3
 * @Description: TODO(第三引导页)
 * @author HuZhiyin
 * @date 2016-9-13 下午3:55:22
 * 
 */
public class GuideFragment3 extends BaseFragment {
	public String pageName = ContextUtil.getString(R.string.guide3);
	public String pageCode ="guide3";
	@ViewInject(R.id.guide3_iv)
	private ImageView guide3_iv;
	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_guide3, container,
				false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		initView();
		return rootView;
	}

	@OnClick(value = { R.id.guide3_iv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.guide3_iv:
			toActivity(MainActivity.class);
			getActivity().finish();
			break;
		}
	}

	public void initView() {
	}

}
