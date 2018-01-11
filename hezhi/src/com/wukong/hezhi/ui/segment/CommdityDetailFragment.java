package com.wukong.hezhi.ui.segment;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.ui.fragment.BaseFragment;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.DataUtil;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CommdityDetailFragment extends BaseFragment {
	@ViewInject(R.id.name_tv)
	private TextView name_tv;

	@ViewInject(R.id.discountPrice_tv)
	private TextView discountPrice_tv;

	@ViewInject(R.id.costPrice_tv)
	private TextView costPrice_tv;

	@ViewInject(R.id.sale_num_tv)
	private TextView sale_num_tv;
	/** 商品信息 */
	private CommodityInfo commodityInfo;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_commdity_detail, container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		commodityInfo = (CommodityInfo) getArguments().getSerializable("commodityInfo");
		init();
		return rootView;
	}

	private void init() {
		if (commodityInfo != null) {
			name_tv.setText(commodityInfo.getName());
			if (commodityInfo.getPrice() == commodityInfo.getCostPrice()) {
				costPrice_tv.setVisibility(View.INVISIBLE);
			} else {
				costPrice_tv.setVisibility(View.VISIBLE);
			}
			discountPrice_tv.setText(
					ContextUtil.getString(R.string.rmb) + DataUtil.double2pointString(commodityInfo.getPrice()));
			costPrice_tv.setText(
					ContextUtil.getString(R.string.rmb) + DataUtil.double2pointString(commodityInfo.getCostPrice()));
			sale_num_tv.setText(ContextUtil.getString(R.string.volume) + commodityInfo.getVolume());
			costPrice_tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); // 中间横线
			costPrice_tv.getPaint().setAntiAlias(true);// 抗锯齿
		}
	}
}