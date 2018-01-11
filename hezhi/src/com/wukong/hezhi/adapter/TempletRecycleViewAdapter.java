package com.wukong.hezhi.adapter;

import java.util.ArrayList;
import java.util.List;

import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.TempletInfo;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.ui.activity.CommodityDesignActivity;
import com.wukong.hezhi.ui.view.TempletWindows;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

public class TempletRecycleViewAdapter extends RecyclerView.Adapter<TempletRecycleViewAdapter.ViewHolder> {

	private List<TempletInfo> templetInfos;
	private Activity mActivity;

	public TempletRecycleViewAdapter(List<TempletInfo> templetInfos, Activity mActivity) {
		super();
		if (templetInfos == null) {
			this.templetInfos = new ArrayList<TempletInfo>();
		} else {
			this.templetInfos = templetInfos;
		}
		this.mActivity = mActivity;
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		public ViewHolder(View arg0) {
			super(arg0);
		}

		ImageView templet_iv;
		CheckBox select_cb;
	}

	@Override
	public int getItemCount() {
		return templetInfos.size();
	}

	/**
	 * 创建ViewHolder
	 */
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = ScreenUtil.inflate(R.layout.item_recycleview_templet);
		ViewHolder viewHolder = new ViewHolder(view);
		viewHolder.templet_iv = (ImageView) view.findViewById(R.id.templet_iv);
		viewHolder.select_cb = (CheckBox) view.findViewById(R.id.select_cb);
		return viewHolder;
	}

	/**
	 * 设置值
	 */
	@Override
	public void onBindViewHolder(final ViewHolder viewHolder, final int i) {
		// viewHolder.templet_iv.setImageResource(templetInfos.get(i));
		GlideImgUitl.glideLoaderNoAnimation(mActivity, templetInfos.get(i).getTempletUrl(), viewHolder.templet_iv, 2);
		viewHolder.templet_iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ObserveManager.getInstance().notifyState(CommodityDesignActivity.class, TempletRecycleViewAdapter.class,
						templetInfos.get(i).getTempletUrl());
				TempletWindows.getInstance().dismiss();
			}
		});
	}

}