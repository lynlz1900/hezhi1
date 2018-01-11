package com.wukong.hezhi.adapter;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.KDNiaoInfo.Traces;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 
 * @ClassName: ExpressViewAdapter
 * @Description: TODO(物流信息适配器)
 * @author HuangFeiFei
 * @date 2017-10-11 10:37
 * 
 */
public class ExpressViewAdapter extends BaseAdapter {

	@SuppressWarnings("unused")
	private Activity mActivity;
	private List<Traces> traces;

	public ExpressViewAdapter(List<Traces> traces,  Activity mActivity) {
		super();

		if (traces == null) {
			this.traces = new ArrayList<>();
		} else {
			this.traces = traces;
		}
		this.mActivity = mActivity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (traces != null && traces.size() > 0) {

			return traces.size();

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
			convertView = ScreenUtil.inflate(R.layout.item_list_express_view);
			holder = new ViewHolder();
			holder.time_tv = (TextView) convertView.findViewById(R.id.time_tv);
			holder.wuliu_tv = (TextView) convertView
					.findViewById(R.id.wuliu_tv);
			holder.dot_view = (View) convertView.findViewById(R.id.dot_view);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (position == 0) {// 第一行字体为绿色
			holder.time_tv.setTextColor(ContextUtil.getResource().getColor(
					R.color.green));
			holder.wuliu_tv.setTextColor(ContextUtil.getResource().getColor(
					R.color.green));
			holder.dot_view.setBackground(ContextUtil.getResource()
					.getDrawable(R.drawable.shape_dot));
		} else {
			holder.time_tv.setTextColor(ContextUtil.getResource().getColor(
					R.color.black));
			holder.wuliu_tv.setTextColor(ContextUtil.getResource().getColor(
					R.color.text_normal));
			holder.dot_view.setBackground(ContextUtil.getResource()
					.getDrawable(R.drawable.shape_dot1));
		}

		holder.time_tv.setText(traces.get(position).getAcceptTime());
		holder.wuliu_tv.setText(traces.get(position).getAcceptStation());

		return convertView;
	}

	static class ViewHolder {
		TextView time_tv;
		TextView wuliu_tv;
		View dot_view;
	}
}
