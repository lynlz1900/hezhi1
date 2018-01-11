package com.wukong.hezhi.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommentInfo;
import com.wukong.hezhi.utils.DateUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;

public class ArticleCommentFragmentAdapter extends BaseAdapter {
	private List<CommentInfo> comments;
	private Activity mActivity;

	public ArticleCommentFragmentAdapter(List<CommentInfo> comments,
			Activity mActivity) {
		super();
		if (comments == null) {
			this.comments = new ArrayList<CommentInfo>();
		} else {
			this.comments = comments;
		}

		this.mActivity = mActivity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return comments.size();
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
			convertView = ScreenUtil.inflate(R.layout.item_comment);
			holder = new ViewHolder();
			holder.header_iv = (ImageView) convertView
					.findViewById(R.id.header_iv);
			holder.nickname_tv = (TextView) convertView
					.findViewById(R.id.nickname_tv);
			holder.comment_tv = (TextView) convertView
					.findViewById(R.id.comment_tv);
			holder.time_tv = (TextView) convertView.findViewById(R.id.time_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CommentInfo commentInfo = comments.get(position);
		GlideImgUitl.glideLoader(mActivity, commentInfo.getShowImageURL(),
				holder.header_iv, 0);

		holder.nickname_tv.setText(commentInfo.getNickName());
		holder.comment_tv.setText(commentInfo.getContent());
		String time = DateUtil.getFormatTime(Long.parseLong(commentInfo
				.getCreateDate()));// 仿朋友圈的时间显示
		holder.time_tv.setText(time);
		return convertView;
	}

	static class ViewHolder {
		ImageView header_iv;
		TextView nickname_tv;
		TextView comment_tv;
		TextView time_tv;
	}
}
