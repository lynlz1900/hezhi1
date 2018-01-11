package com.wukong.hezhi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.MyGuanZhuInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.ui.activity.BusinessAcitivty;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;

public class MyGuanZhuActivityAdapter extends BaseAdapter {
	private List<MyGuanZhuInfo> myGuanZhuInfos;
	private Activity mActivity;

	public MyGuanZhuActivityAdapter(List<MyGuanZhuInfo> myGuanZhuInfos,
			Activity mActivity) {
		super();
		if (myGuanZhuInfos == null) {
			this.myGuanZhuInfos = new ArrayList<MyGuanZhuInfo>();
		} else {
			this.myGuanZhuInfos = myGuanZhuInfos;
		}
		this.mActivity = mActivity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return myGuanZhuInfos.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		final int pos = position;
		ViewHolder holder;
		if (convertView == null) {
			convertView = ScreenUtil
					.inflate(R.layout.item_list_my_guanzhu_activity);
			holder = new ViewHolder();
			holder.card_iv = (ImageView) convertView.findViewById(R.id.card_iv);
			holder.title = (TextView) convertView.findViewById(R.id.title_tv);
			holder.summary_tv = (TextView) convertView
					.findViewById(R.id.summary_tv);
			holder.app_sum_tv = (TextView) convertView
					.findViewById(R.id.app_sum_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MyGuanZhuInfo myGuanZhuInfo = myGuanZhuInfos.get(position);
		holder.title.setText(myGuanZhuInfos.get(position).getTitle());
		GlideImgUitl.glideLoader(mActivity, myGuanZhuInfos.get(pos).getPhoto(),
				holder.card_iv, 2);
		holder.summary_tv.setText(myGuanZhuInfo.getIntroduction());
		holder.app_sum_tv.setText(myGuanZhuInfo.getAppNum() + "\n作品");
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				MyGuanZhuInfo myGuanZhuInfo = myGuanZhuInfos.get(position);
				JumpActivityManager.getInstance().toActivity(mActivity,
						BusinessAcitivty.class, Constant.Extra.BUSINESS_ID,
						myGuanZhuInfo.getBusinessId());
			}
		});
		return convertView;
	}

	static class ViewHolder {
		ImageView card_iv;
		TextView title;
		TextView summary_tv;
		TextView app_sum_tv;
	}
}
