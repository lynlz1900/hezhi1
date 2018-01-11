package com.wukong.hezhi.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.LogisticsInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 
 * @ClassName: LogisticsAdapter
 * @Description: TODO(物流信息适配器)
 * @author HuZhiyin
 * @date 2016-10-12 下午3:10:03
 * 
 */
public class LogisticsFragmentAdapter extends BaseAdapter {
	private Activity mActivity;
	private ArrayList<LogisticsInfo> infos;
	private ListView lv;

	public LogisticsFragmentAdapter(ArrayList<LogisticsInfo> infos,
			ListView lv, Activity mActivity) {
		super();
		if (infos == null) {
			this.infos = new ArrayList<LogisticsInfo>();
		} else {
			this.infos = infos;
		}
		this.mActivity = mActivity;
		this.lv = lv;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (infos != null) {
			return infos.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder;
		if (convertView == null) {
			convertView = ScreenUtil
					.inflate(R.layout.item_list_logisticsfragment);
			holder = new ViewHolder();
			holder.time_tv = (TextView) convertView.findViewById(R.id.time_tv);
			holder.scantype_tv = (TextView) convertView
					.findViewById(R.id.scantype_tv);
			holder.location_tv = (TextView) convertView
					.findViewById(R.id.location_tv);
			holder.operater_tv = (TextView) convertView
					.findViewById(R.id.operater_tv);
			holder.memo_tv = (TextView) convertView.findViewById(R.id.memo_tv);
			holder.dot_view = (View) convertView.findViewById(R.id.dot_view);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String scanType = "";
		switch (infos.get(position).getInOutType()) {
		case Constant.LOGISTICS.CHECK:
			scanType = ContextUtil.getResource().getString(R.string.check);
			break;
		case Constant.LOGISTICS.INSTORAGE:
			scanType = ContextUtil.getResource().getString(R.string.instorage);
			break;
		case Constant.LOGISTICS.OUTSTORAGE:
			scanType = ContextUtil.getResource().getString(R.string.outstorage);
			break;
		case Constant.LOGISTICS.STOWAGE:
			scanType = ContextUtil.getResource().getString(R.string.stowage);
			break;
		case Constant.LOGISTICS.UNLOADING:
			scanType = ContextUtil.getResource().getString(R.string.unloading);
			break;
		case Constant.LOGISTICS.WHOLESALE:
			scanType = ContextUtil.getResource().getString(R.string.wholesale);
			break;
		}
		if (position == 0) {// 第一行字体为红色
			holder.time_tv.setTextColor(ContextUtil.getResource().getColor(
					R.color.base));
			holder.scantype_tv.setTextColor(ContextUtil.getResource().getColor(
					R.color.base));
			holder.location_tv.setTextColor(ContextUtil.getResource().getColor(
					R.color.base));
			holder.operater_tv.setTextColor(ContextUtil.getResource().getColor(
					R.color.base));
			holder.memo_tv.setTextColor(ContextUtil.getResource().getColor(
					R.color.base));
			holder.dot_view.setBackground(ContextUtil.getResource()
					.getDrawable(R.drawable.shape_dot));
		} else {
			holder.time_tv.setTextColor(ContextUtil.getResource().getColor(
					R.color.black));
			holder.scantype_tv.setTextColor(ContextUtil.getResource().getColor(
					R.color.text_normal));
			holder.location_tv.setTextColor(ContextUtil.getResource().getColor(
					R.color.text_normal));
			holder.operater_tv.setTextColor(ContextUtil.getResource().getColor(
					R.color.text_normal));
			holder.memo_tv.setTextColor(ContextUtil.getResource().getColor(
					R.color.text_normal));
			holder.dot_view.setBackground(ContextUtil.getResource()
					.getDrawable(R.drawable.shape_dot1));
		}

		holder.time_tv.setText(infos.get(position).getBusinessDate());
		holder.scantype_tv.setText("[" + scanType + "]");
		holder.location_tv.setText(infos.get(position).getAddress());
		holder.operater_tv.setText(ContextUtil.getString(R.string.operater)
				+ "：" + infos.get(position).getRealName());
		holder.memo_tv.setText(ContextUtil.getString(R.string.remark) + "："
				+ infos.get(position).getMemo());
		return convertView;
	}

	static class ViewHolder {
		TextView time_tv;
		TextView scantype_tv;
		TextView location_tv;
		TextView operater_tv;
		TextView memo_tv;
		View dot_view;
	}

}
