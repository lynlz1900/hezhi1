package com.wukong.hezhi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CashInfo;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;

public class CashDetailActivityAdapter extends BaseAdapter {
	private List<CashInfo> cards;
	private Activity mActivity;

	public CashDetailActivityAdapter(List<CashInfo> cards, Activity mActivity) {
		super();
		if (cards == null) {
			this.cards = new ArrayList<CashInfo>();
		} else {
			this.cards = cards;
		}

		this.mActivity = mActivity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return cards.size();
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

		final int pos = position;
		ViewHolder holder;
		if (convertView == null) {
			convertView = ScreenUtil.inflate(R.layout.item_list_cash);
			holder = new ViewHolder();
			holder.card_iv = (ImageView) convertView.findViewById(R.id.icon_iv);
			holder.title = (TextView) convertView.findViewById(R.id.redbag_tip);
			holder.title2 = (TextView) convertView
					.findViewById(R.id.redbag_tip2);
			holder.time = (TextView) convertView.findViewById(R.id.time_tv);
			holder.money = (TextView) convertView.findViewById(R.id.money_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setText(cards.get(position).getTitle());
		holder.title2.setText(cards.get(position).getStatus());
		holder.time.setText(cards.get(position).getDate());
		holder.money.setText(cards.get(position).getMoney());
		GlideImgUitl.glideLoader(mActivity, cards.get(pos).getLogoUrl(),
				holder.card_iv, 1);
		return convertView;
	}

	static class ViewHolder {
		ImageView card_iv;
		TextView title;
		TextView title2;
		TextView time;
		TextView money;
	}
}
