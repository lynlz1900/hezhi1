package com.wukong.hezhi.adapter;

import java.util.ArrayList;
import java.util.List;

import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.JpushInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.db.DBManager;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.ui.activity.MsgCenterActivity;
import com.wukong.hezhi.ui.activity.MsgDetaiActivity;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.DateUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MsgCenterActivityAdapter extends BaseAdapter {
	private List<JpushInfo> jpushInfos;
	private Activity mActivity;

	public MsgCenterActivityAdapter(List<JpushInfo> jpushInfos, Activity mActivity) {
		super();
		if (jpushInfos == null) {
			this.jpushInfos = new ArrayList<JpushInfo>();
		} else {
			this.jpushInfos = jpushInfos;
		}
		this.mActivity = mActivity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return jpushInfos.size();
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
			convertView = ScreenUtil.inflate(R.layout.item_list_msg);
			holder = new ViewHolder();
			holder.card_iv = (ImageView) convertView.findViewById(R.id.icon_iv);
			holder.red_point = (ImageView) convertView.findViewById(R.id.red_point);
			holder.title = (TextView) convertView.findViewById(R.id.redbag_tip);
			holder.title2 = (TextView) convertView.findViewById(R.id.redbag_tip2);
			holder.time = (TextView) convertView.findViewById(R.id.time_tv);
			holder.money = (TextView) convertView.findViewById(R.id.money_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final JpushInfo jpushInfo = jpushInfos.get(position);
		if (jpushInfo.isChecked()) {// 如果查看过，则隐藏红点
			holder.red_point.setVisibility(View.INVISIBLE);
		} else {
			holder.red_point.setVisibility(View.VISIBLE);
		}

		String time = DateUtil.getFormatTime(Long.parseLong(jpushInfo.getLocalTime()));// 仿朋友圈的时间显示

		holder.title.setText(jpushInfo.getTitle());
		holder.title2.setText(jpushInfo.getStatus());
		holder.time.setText(time);
		holder.money.setText(jpushInfo.getContent());

		GlideImgUitl.glideLoader(mActivity, jpushInfos.get(pos).getLogoUrl(), holder.card_iv, 1);
		convertView.setOnClickListener(new MyClickListener(jpushInfos.get(position), holder.red_point));
		convertView.setOnLongClickListener(new OnLongClickListener() {// 长按删除

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				picId = jpushInfos.get(position).getLocalTime();
				showDeleteDialog();
				return false;
			}
		});
		return convertView;
	}

	private class MyClickListener implements OnClickListener {
		private JpushInfo jpushInfo;
		private String picId;
		private ImageView point;

		public MyClickListener(JpushInfo jpushInfo, ImageView point) {
			super();
			this.jpushInfo = jpushInfo;
			this.picId = jpushInfo.getLocalTime();
			this.point = point;
		}

		@Override
		public void onClick(View v) {
			this.jpushInfo.setChecked(true);// 更改小红点的状态
			String jsonStr = JsonUtil.setJson(jpushInfo);// 更改后的json
			DBManager.getInstance().update(picId, DBManager.JGPUSH, jsonStr);// 更新数据库
			JumpActivityManager.getInstance().toActivity(mActivity, MsgDetaiActivity.class, Constant.Extra.MSG_DETAIL,
					jpushInfo);
			if (jpushInfo.isChecked()) {// 如果查看过，则隐藏红点
				point.setVisibility(View.INVISIBLE);
			} else {
				point.setVisibility(View.VISIBLE);
			}

		}
	}

	public String picId;

	/**
	 * 是否删除
	 */
	protected void showDeleteDialog() {
		new CustomAlterDialog.Builder(mActivity).setMsg(ContextUtil.getString(R.string.delete_tip))
				.setCancelButton(ContextUtil.getString(R.string.cancel), null)
				.setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {
					@Override
					public void onDialogClick(View v, CustomAlterDialog dialog) {
						// TODO Auto-generated method stub
						DBManager.getInstance().deleteById(picId);
						ObserveManager.getInstance().notifyState(MsgCenterActivity.class,
								MsgCenterActivityAdapter.class, null);// 通知观察者数据发生了变化
					}
				}).build().show();
	}

	static class ViewHolder {
		ImageView card_iv;
		ImageView red_point;
		TextView title;
		TextView title2;
		TextView time;
		TextView money;
	}
}
