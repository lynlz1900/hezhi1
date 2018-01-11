package com.wukong.hezhi.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.ui.activity.CommodityInfoActivity;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.DataUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;
import java.util.ArrayList;
import java.util.List;

public class CommodityActivityAdapter extends BaseAdapter {
	private List<CommodityInfo> infos;
	private Activity mActivity;

	public CommodityActivityAdapter(List<CommodityInfo> infos, Activity mActivity) {
		super();
		if (infos == null) {
			this.infos = new ArrayList<CommodityInfo>();
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
			convertView = ScreenUtil.inflate(R.layout.item_grid_commodity);
			holder = new ViewHolder();
			holder.commodity_iv = (ImageView) convertView.findViewById(R.id.commodity_iv);
			holder.name_tv = (TextView) convertView.findViewById(R.id.name_tv);
			holder.discountPrice_tv = (TextView) convertView.findViewById(R.id.discountPrice_tv);
			holder.costPrice_tv = (TextView) convertView.findViewById(R.id.costPrice_tv);
			holder.text_succ_count = (TextView) convertView.findViewById(R.id.text_succ_count);
			holder.text_commodity_empty = (TextView) convertView.findViewById(R.id.text_commodity_empty);
			holder.image_custom_type = (ImageView) convertView.findViewById(R.id.image_custom_type);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CommodityInfo info = infos.get(position);
		GlideImgUitl.glideLoader(mActivity, info.getImageUrl(), holder.commodity_iv, 2);
		holder.name_tv.setText(info.getName());

		if (info.getPrice() == info.getCostPrice()) {
			holder.costPrice_tv.setVisibility(View.INVISIBLE);

		} else {
			holder.costPrice_tv.setVisibility(View.VISIBLE);
		}

		holder.discountPrice_tv
				.setText(ContextUtil.getString(R.string.rmb) + DataUtil.double2pointString(info.getPrice()));
		holder.costPrice_tv
				.setText(ContextUtil.getString(R.string.rmb) + DataUtil.double2pointString(info.getCostPrice()));
		holder.costPrice_tv.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); // 中间横线
		holder.costPrice_tv.getPaint().setAntiAlias(true);// 抗锯齿
		holder.text_succ_count.setText(ContextUtil.getString(R.string.volume) + info.getVolume());
		if(info.getFaceCustom() == 1){
			holder.image_custom_type.setVisibility(View.VISIBLE);
			holder.image_custom_type.setImageResource(R.drawable.icon_customsurface);
		}
		else if(info.getNfcCustom() == 1){
			holder.image_custom_type.setVisibility(View.VISIBLE);
			holder.image_custom_type.setImageResource(R.drawable.icon_custommedio);
		}
		else{
			holder.image_custom_type.setVisibility(View.GONE);
		}
		
		if(info.getInventory() <1)
			holder.text_commodity_empty.setVisibility(View.VISIBLE);
		else
			holder.text_commodity_empty.setVisibility(View.INVISIBLE);
		
		holder.commodity_iv.setOnClickListener(new MyClickListener(info));

		return convertView;
	}

	private class MyClickListener implements OnClickListener {
		CommodityInfo info;

		public MyClickListener(CommodityInfo info) {
			super();
			this.info = info;
		}

		@Override
		public void onClick(View v) {
			JumpActivityManager.getInstance().toActivity(mActivity, CommodityInfoActivity.class,
					Constant.Extra.PRODUCTID, info.getId());
		}

	}

	static class ViewHolder {
		ImageView commodity_iv;
		TextView name_tv;
		TextView discountPrice_tv;
		TextView costPrice_tv;
		TextView text_succ_count;
		TextView text_commodity_empty;
		ImageView image_custom_type;
	}

}
