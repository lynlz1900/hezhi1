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

public class MyGuanZhuFragmentAdapter extends BaseAdapter {
	private List<MyGuanZhuInfo> myGuanZhuInfos;
	private Activity mActivity;

	public MyGuanZhuFragmentAdapter(List<MyGuanZhuInfo> myGuanZhuInfos,
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

		if (myGuanZhuInfos.size() < 3) {
			return myGuanZhuInfos.size();
		} else {
			return 3;
		}

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
					.inflate(R.layout.item_list_my_guanzhu_fragment);
			holder = new ViewHolder();
			holder.card_iv = (ImageView) convertView.findViewById(R.id.card_iv);
			holder.title = (TextView) convertView.findViewById(R.id.title_tv);
			holder.look_tv = (TextView) convertView.findViewById(R.id.look_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		MyGuanZhuInfo guanZhuInfo = myGuanZhuInfos.get(position);
		holder.title.setText(guanZhuInfo.getTitle());
		holder.look_tv.setText(guanZhuInfo.getAppNum() + "作品");
		/**
		 * 设置默认的图片展现、加载失败的图片展现
		 */
		GlideImgUitl.glideLoader(mActivity, guanZhuInfo.getPhoto(),
				holder.card_iv, 2);

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
		TextView look_tv;
	}
}
