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
import com.wukong.hezhi.bean.ArticleInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.KeyboardManager;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.ui.activity.ArticleActivity;
import com.wukong.hezhi.ui.fragment.FocusFragment;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.StringUtil;

public class ArticleAdapter extends BaseAdapter {
	private List<ArticleInfo> myCollecionInfos;
	private Activity mActivity;

	public ArticleAdapter(List<ArticleInfo> myCollecionInfos,
			Activity mActivity) {
		super();
		if (myCollecionInfos == null) {
			this.myCollecionInfos = new ArrayList<ArticleInfo>();
		} else {
			this.myCollecionInfos = myCollecionInfos;
		}

		this.mActivity = mActivity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub

		return myCollecionInfos.size();

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
			convertView = ScreenUtil.inflate(R.layout.item_list_my_collection);
			holder = new ViewHolder();
			holder.article_iv = (ImageView) convertView
					.findViewById(R.id.article_iv);
			holder.title_tv = (TextView) convertView
					.findViewById(R.id.title_tv);
			holder.label_tv = (TextView) convertView
					.findViewById(R.id.label_tv);
			holder.comment_tv = (TextView) convertView
					.findViewById(R.id.comment_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final ArticleInfo myCollecionInfo = myCollecionInfos.get(position);
		final String id = myCollecionInfo.getId();// 文章id
		GlideImgUitl.glideLoader(mActivity, myCollecionInfo.getThumbnail(),
				holder.article_iv, 2);
		holder.title_tv.setText(myCollecionInfo.getTitle());
		holder.label_tv.setText(myCollecionInfo.getLabel());
		int num = StringUtil.str2Int(myCollecionInfo.getCommentCount());
		holder.comment_tv.setText(StringUtil.change2W(num) + "条评论");

		if (myCollecionInfo.isHavaToSee()) {// 如果阅读过，则标题置为灰色
			holder.title_tv.setTextColor(ContextUtil.getColor(R.color.hint));
		} else {
			holder.title_tv.setTextColor(ContextUtil
					.getColor(R.color.text_main));
		}

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				KeyboardManager.getInstance().hideKeyboard(mActivity);// 隐藏键盘，如不隐藏键盘，会将键盘带入到下一个界面，文章界面在判断弹出和收缩键盘时就会出bug。
				JumpActivityManager.getInstance().toActivity(mActivity,
						ArticleActivity.class, Constant.Extra.ARTICLE,
						myCollecionInfo);
				ObserveManager.getInstance().notifyState(FocusFragment.class,
						ArticleAdapter.class, id);// 点击后，通知文章列表，阅读状态改变了。
			}
		});
		return convertView;
	}

	static class ViewHolder {
		ImageView article_iv;
		TextView title_tv;
		TextView label_tv;
		TextView comment_tv;
	}
}
