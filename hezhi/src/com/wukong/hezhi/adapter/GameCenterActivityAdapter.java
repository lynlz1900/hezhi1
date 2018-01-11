package com.wukong.hezhi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.GameInfo;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.InstallUtil;
import com.wukong.hezhi.utils.ScreenUtil;

public class GameCenterActivityAdapter extends BaseAdapter {
	private List<GameInfo> cards;
	private Activity mActivity;

	public GameCenterActivityAdapter(List<GameInfo> cards, Activity mActivity) {
		super();
		if (cards == null) {
			this.cards = new ArrayList<GameInfo>();
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
		final ViewHolder holder;
		if (convertView == null) {
			convertView = ScreenUtil.inflate(R.layout.item_list_game);
			holder = new ViewHolder();
			holder.card_iv = (ImageView) convertView.findViewById(R.id.icon_iv);
			holder.title = (TextView) convertView.findViewById(R.id.redbag_tip);
			holder.title2 = (TextView) convertView
					.findViewById(R.id.redbag_tip2);
			holder.content_tv = (TextView) convertView
					.findViewById(R.id.content_tv);
			holder.open_tv = (TextView) convertView.findViewById(R.id.open_tv);
			holder.myProgressBar = (ProgressBar) convertView
					.findViewById(R.id.myProgressBar);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setText(cards.get(position).getTitle());
		holder.content_tv.setText(cards.get(position).getContent());

		GlideImgUitl.glideLoader(mActivity, cards.get(pos).getIconUrl(),
				holder.card_iv, 1);

		if (checkHashInstall(cards.get(pos).getAppName())) {// 如果已经安装，这打开，
			holder.open_tv.setText("打开");
		} else {// 如果已经安装，则下载，
			holder.open_tv.setText("下载");
		}

		holder.open_tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				InstallUtil.doStartApplicationWithPackageName(cards.get(pos)
						.getAppName(), cards.get(pos).getApkUrl());
				// final String packagename = cards.get(pos).getAppName();
				// String apkUrl = cards.get(pos).getApkUrl();
				// InstallUtil.openByAppName(packagename, apkUrl,
				// new InstallCallCack() {
				//
				// @Override
				// public void onSuccess() {
				// // TODO Auto-generated method stub
				//
				// String filePath = HezhiConfig.DOWNLOAD_PATH
				// + packagename + ".apk";
				// InstallUtil.installApkFromSD(filePath);// 安装apk
				// holder.open_tv.setText("打开");
				// holder.open_tv.setClickable(true);
				// holder.myProgressBar
				// .setVisibility(View.INVISIBLE);
				// }
				//
				// @Override
				// public void onLoading(long total, long current) {
				// // TODO Auto-generated method stub
				// holder.open_tv.setText("下载中...");
				// holder.open_tv.setClickable(false);
				// holder.myProgressBar
				// .setVisibility(View.VISIBLE);
				// holder.myProgressBar.setMax((int) total);
				// holder.myProgressBar.setProgress((int) current);
				//
				// }
				//
				// @Override
				// public void onFail() {
				// // TODO Auto-generated method stub
				// holder.open_tv.setText("下载失败");
				// holder.open_tv.setClickable(true);
				// holder.myProgressBar
				// .setVisibility(View.INVISIBLE);
				// }
				// });
			}
		});

		return convertView;
	}

	static class ViewHolder {
		ImageView card_iv;
		TextView title;
		TextView title2;
		TextView content_tv;
		TextView open_tv;
		ProgressBar myProgressBar;
	}

	/** 检查是否安装 */
	public boolean checkHashInstall(String packagename) {
		PackageInfo packageinfo = null;
		try {
			packageinfo = mActivity.getPackageManager().getPackageInfo(
					packagename, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			packageinfo = null;
		}
		if (packageinfo == null) {
			return false;
		} else {
			return true;
		}
	}
}
