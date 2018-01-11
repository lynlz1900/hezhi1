package com.wukong.hezhi.adapter;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommoditySceneInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.ui.activity.CustomizationActivity;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;

public class CommoditySceneAdapter extends BaseAdapter {
	private List<CommoditySceneInfo> infos;
	private Activity mActivity;

	public CommoditySceneAdapter(List<CommoditySceneInfo> infos, Activity mActivity) {
		super();
		if (infos == null) {
			this.infos = new ArrayList<CommoditySceneInfo>();
		} else {
			this.infos = infos;
		}
		this.mActivity = mActivity;
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
			convertView = ScreenUtil.inflate(R.layout.item_grid_commodity_scene);
			holder = new ViewHolder();
			holder.image_scene = (ImageView) convertView.findViewById(R.id.image_scene);
			holder.text_title = (TextView) convertView.findViewById(R.id.text_title);
			holder.text_content = (TextView) convertView.findViewById(R.id.text_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final CommoditySceneInfo info = infos.get(position);
		GlideImgUitl.degree = 4;
		GlideImgUitl.glideLoader(mActivity, info.getImgUrl(), holder.image_scene, 1);
//		holder.text_title.setText(info.getCaseName());
//		holder.text_content.setText(info.getDescription());

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(info != null){
					Constant.customScene.customSceneId = info.getId();
					Constant.customScene.isFromCustomScene = true;
					Constant.customScene.customSceneInfo = info;
					JumpActivityManager.getInstance().toActivity(mActivity, CustomizationActivity.class);
				}
			}
		});

		return convertView;
	}

	static class ViewHolder {
		ImageView image_scene;
		TextView text_title;
		TextView text_content;
	}

}
