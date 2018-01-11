package com.wukong.hezhi.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.ui.view.GridViewForListview;
import com.wukong.hezhi.utils.DateUtil;
import com.wukong.hezhi.utils.ScreenUtil;

public class MyCustomAdapter extends BaseAdapter{
	private List<HashMap<String, Object>> list;
	public Activity mActivity;
	private MyCustomizationAdapter adapter;
	private List<CommodityInfo> data;

	public MyCustomAdapter(List<HashMap<String, Object>> list,
			Activity mActivity) {
		super();
		if (list == null) {
			this.list = new ArrayList<HashMap<String, Object>>();
		} else {
			this.list = list;
		}
		this.mActivity = mActivity;
		data = new ArrayList<>();
		adapter = new MyCustomizationAdapter(data, mActivity, 0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder;
		if (convertView == null) {
			convertView = ScreenUtil.inflate(R.layout.item_list_my_custom);
			holder = new ViewHolder();
			holder.gv_custom = (GridViewForListview) convertView
					.findViewById(R.id.gv_custom);
			holder.text_time = (TextView) convertView.findViewById(R.id.text_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		HashMap<String, Object> hashMap = list.get(position);
		
		if(hashMap != null){
			try {
				holder.text_time.setText(DateUtil.formatDateTime((String)hashMap.get("time")));
				List<CommodityInfo> commodityInfos = (List<CommodityInfo>)hashMap.get("data");
				data = commodityInfos;
				adapter = new MyCustomizationAdapter(data, mActivity, position);
				holder.gv_custom.setAdapter(adapter);
			} catch (Exception e) {
			}
		}

		return convertView;
	}

	static class ViewHolder {
		GridViewForListview gv_custom;
		TextView text_time;
	}
}
