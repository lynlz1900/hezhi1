package com.wukong.hezhi.ui.segment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.ui.fragment.BaseFragment;
import com.wukong.hezhi.utils.ContextUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class CommdityBannerImgFragment extends BaseFragment {
	@ViewInject(R.id.comm_iv)
	private ImageView comm_iv;

	private String urlImg;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_commdity_bannerimg, container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		initView();
		return rootView;
	}

	public void initView() {
		urlImg = (String) getArguments().getSerializable("imgUrl");
		
		Glide.with(ContextUtil.getContext()).load(urlImg).asBitmap().format(DecodeFormat.PREFER_ARGB_8888)
		.fitCenter().diskCacheStrategy(DiskCacheStrategy.SOURCE)
		.into(comm_iv);
		
	}

}
