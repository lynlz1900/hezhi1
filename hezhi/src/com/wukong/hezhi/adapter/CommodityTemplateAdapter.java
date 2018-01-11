package com.wukong.hezhi.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.TempletInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.ui.activity.CustomComposeActivity;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

public class CommodityTemplateAdapter extends BaseAdapter {
	private List<TempletInfo> infos;
	private Activity mActivity;

	/** 商品信息 */
	private CommodityInfo commodityInfo;
	
	public CommodityTemplateAdapter(List<TempletInfo> infos, Activity mActivity,CommodityInfo commodityInfo) {
		super();
		if (infos == null) {
			this.infos = new ArrayList<TempletInfo>();
		} else {
			this.infos = infos;
		}
		this.mActivity = mActivity;
		this.commodityInfo = commodityInfo;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return infos.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = ScreenUtil.inflate(R.layout.item_grid_commodity_template);
			holder = new ViewHolder();
			holder.image_template = (ImageView) convertView.findViewById(R.id.image_template);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final TempletInfo info = infos.get(position);
		GlideImgUitl.glideLoader(mActivity, info.getTempletResultUrl(), holder.image_template, 2);

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mActivity != null && info != null && commodityInfo != null){
					Intent intent = new Intent();
					intent.putExtra(Constant.Extra.COMMDITYINFO, commodityInfo);
					intent.putExtra(Constant.Extra.TEMPLATE, info);
					intent.setClass(mActivity, CustomComposeActivity.class);
					mActivity.startActivity(intent);
				}
			}
		});

		return convertView;
	}

	static class ViewHolder {
		ImageView image_template;
	}

}
