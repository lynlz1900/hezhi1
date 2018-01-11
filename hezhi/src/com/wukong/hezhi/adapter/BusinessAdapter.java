package com.wukong.hezhi.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.BusinessInfo;
import com.wukong.hezhi.bean.VuforiaInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.UnityManger;
import com.wukong.hezhi.ui.activity.ProductMainActivity;
import com.wukong.hezhi.ui.activity.UnityPlayerActivity;
import com.wukong.hezhi.ui.activity.WebViewActivity;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;

public class BusinessAdapter extends BaseAdapter {
	private List<BusinessInfo> businessInfos;
	private Activity mActivity;

	public BusinessAdapter(List<BusinessInfo> businessInfos,
			Activity mActivity) {
		super();
		if (businessInfos == null) {
			this.businessInfos = new ArrayList<BusinessInfo>();
		} else {
			this.businessInfos = businessInfos;
		}

		this.mActivity = mActivity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return businessInfos.size();
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
			convertView = ScreenUtil.inflate(R.layout.item_list_business);
			holder = new ViewHolder();
			holder.card_iv = (ImageView) convertView.findViewById(R.id.card_iv);
			holder.ar_iv = (ImageView) convertView.findViewById(R.id.ar_iv);
			holder.title = (TextView) convertView.findViewById(R.id.title_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setText(businessInfos.get(position).getTitle());

		GlideImgUitl.glideLoader(mActivity, businessInfos.get(pos).getOssUrl(),
				holder.card_iv, 2);

		if (businessInfos.get(pos).isAr()) {// 判断是否是ar应用
			holder.ar_iv.setVisibility(View.VISIBLE);
		} else {
			holder.ar_iv.setVisibility(View.INVISIBLE);
		}

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BusinessInfo businessInfo = businessInfos.get(position);
				String webUrl = businessInfo.getUrlHttp();
				String title = businessInfo.getTitle();
				String reSouceType = businessInfo.getResourceType();
				String targetId = businessInfo.getTargetId();
				if ("UNITY3D".equals(reSouceType)) {
					String unityStr = UnityManger.getInstance().toUTFormat(
							UnityManger.LIVE, targetId);
					JumpActivityManager.getInstance().toActivity(mActivity,
							UnityPlayerActivity.class,
							Constant.Extra.UNITY_INFO, unityStr);
				} else if ("MP4".equals(reSouceType)) {
					VuforiaInfo vuforiaInfo = new VuforiaInfo();
					vuforiaInfo.setAr(businessInfo.isAr());
					vuforiaInfo.setBusinessId(businessInfo.getBusinessId());
					vuforiaInfo.setHasRedPack(businessInfo.isHasRedPack());
					vuforiaInfo.setMaxNum(businessInfo.getMaxNum());
					vuforiaInfo.setOssUrl(businessInfo.getOssUrl());
					vuforiaInfo.setPpid(businessInfo.getPpid() + "");
					vuforiaInfo.setTargetId(businessInfo.getTargetId());
					vuforiaInfo.setTitle(businessInfo.getTitle());

					JumpActivityManager.getInstance().toActivity(mActivity,
							ProductMainActivity.class, Constant.Extra.VUFORIA,
							vuforiaInfo);
				} else if ("URL".equals(reSouceType)
						|| "PICTURE".equals(reSouceType)) {
					scanNum(businessInfo);// 浏览次数
					Intent intent = new Intent(mActivity, WebViewActivity.class);
					intent.putExtra(Constant.Extra.WEB_URL, webUrl);
					intent.putExtra(Constant.Extra.WEBVIEW_TITLE, title);
					mActivity.startActivity(intent);
				}

			}
		});
		return convertView;
	}

	/** 浏览次数 */
	private void scanNum(BusinessInfo businessInfo) {
		String URL = HttpURL.URL1 + HttpURL.GANZ_RESOURCE2;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("targetId", businessInfo.getTargetId());
		map.put("version", HezhiConfig.SERVER_VERSION);
		map.put("ppid", businessInfo.getPpid());
		HttpManager.getInstance().post(URL, map, null);
	}

	static class ViewHolder {
		ImageView card_iv;
		ImageView ar_iv;
		TextView title;
	}
}
