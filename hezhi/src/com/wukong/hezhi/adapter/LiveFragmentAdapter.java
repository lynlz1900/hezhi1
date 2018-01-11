package com.wukong.hezhi.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.VuforiaInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.manager.ObserveManager.Observer;
import com.wukong.hezhi.manager.UnityManger;
import com.wukong.hezhi.ui.activity.MainActivity;
import com.wukong.hezhi.ui.activity.ProductMainActivity;
import com.wukong.hezhi.ui.activity.UnityPlayerActivity;
import com.wukong.hezhi.ui.activity.WebViewActivity;
import com.wukong.hezhi.ui.fragment.LiveFragment;
import com.wukong.hezhi.ui.view.GuideView;
import com.wukong.hezhi.ui.view.GuideView.OnClickCallback;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.StringUtil;
import com.wukong.hezhi.utils.ThreadUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveFragmentAdapter extends BaseAdapter implements Observer {
	private List<VuforiaInfo> cards;
	private Activity mActivity;

	public LiveFragmentAdapter(List<VuforiaInfo> cards, Activity mActivity) {
		super();
		ObserveManager.getInstance().addObserver(this);// 将fragment加入到观察者内，方便发消息

		if (cards == null) {
			this.cards = new ArrayList<VuforiaInfo>();
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

	ViewHolder holder;

	private ImageView firstImageView;// 第一个item里的图标

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		final int pos = position;
		if (convertView == null) {
			convertView = ScreenUtil.inflate(R.layout.item_list_livefragment);
			if (position == 0) {
				firstImageView = (ImageView) convertView
						.findViewById(R.id.redbag_iv);
			}
			holder = new ViewHolder();
			holder.card_iv = (ImageView) convertView.findViewById(R.id.card_iv);
			holder.ar_iv = (ImageView) convertView.findViewById(R.id.ar_iv);
			holder.redbag_iv = (ImageView) convertView
					.findViewById(R.id.redbag_iv);
			holder.title = (TextView) convertView.findViewById(R.id.title_tv);
			holder.look_tv = (TextView) convertView.findViewById(R.id.look_tv);
			holder.receive_tv = (TextView) convertView
					.findViewById(R.id.receive_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		VuforiaInfo vuforiaInfo = cards.get(position);
		holder.title.setText(vuforiaInfo.getTitle());

		GlideImgUitl.glideLoader(mActivity, vuforiaInfo.getOssUrl(),
				holder.card_iv, 2);// 封面

		if (vuforiaInfo.isHasRedPack()) {// 判断是否有红包
			holder.redbag_iv.setVisibility(View.VISIBLE);

		} else {
			holder.redbag_iv.setVisibility(View.INVISIBLE);
		}
		if (vuforiaInfo.getReceiveMark() == 1) {// 1表示已领取 2 未领取 或者 没有红包
			holder.receive_tv.setVisibility(View.VISIBLE);
		} else {
			holder.receive_tv.setVisibility(View.INVISIBLE);
		}

		if (vuforiaInfo.isAr()) {// 判断是否是ar应用
			holder.ar_iv.setVisibility(View.VISIBLE);
		} else {
			holder.ar_iv.setVisibility(View.INVISIBLE);
		}
		String num = StringUtil.change2W(vuforiaInfo.getMaxNum());
		holder.look_tv.setText(num + "次观看");
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				VuforiaInfo vuforiaInfo = cards.get(position);
				String targetId = vuforiaInfo.getTargetId();
				final String ppid = vuforiaInfo.getPpid();
				String webUrl = vuforiaInfo.getUrlHttp();
				String title = vuforiaInfo.getTitle();
				String reSouceType = vuforiaInfo.getResourceType();
				if ("UNITY3D".equals(reSouceType)) {
					String unityStr = UnityManger.getInstance().toUTFormat(
							UnityManger.LIVE, targetId);
					JumpActivityManager.getInstance().toActivity(mActivity,
							UnityPlayerActivity.class,
							Constant.Extra.UNITY_INFO, unityStr);
				} else if ("MP4".equals(reSouceType)) {
					JumpActivityManager.getInstance().toActivity(mActivity,
							ProductMainActivity.class, Constant.Extra.VUFORIA,
							vuforiaInfo);
				} else if ("URL".equals(reSouceType)
						|| "PICTURE".equals(reSouceType)) {
					scanNum(vuforiaInfo);// 浏览次数
					Intent intent = new Intent(mActivity, WebViewActivity.class);
					intent.putExtra(Constant.Extra.WEB_URL, webUrl);
					intent.putExtra(Constant.Extra.WEBVIEW_TITLE, title);
					mActivity.startActivity(intent);
				}

				ThreadUtil.postDelayed(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						ObserveManager.getInstance().notifyState(
								LiveFragment.class, LiveFragmentAdapter.class,
								ppid);// 通知品牌直播浏览次数变化了.getInstance().notifyState(LiveFragment.class,getClass(),currentFragmentState);//通知品牌直播浏览次数变化了
					}
				}, 2000);

			}

		});
		return convertView;
	}

	/** 浏览次数 */
	private void scanNum(VuforiaInfo vuforiaInfo) {
		String URL = HttpURL.URL1 + HttpURL.GANZ_RESOURCE2;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("targetId", vuforiaInfo.getTargetId());
		map.put("version", HezhiConfig.SERVER_VERSION);
		map.put("ppid", vuforiaInfo.getPpid());
		HttpManager.getInstance().post(URL, map, null);
	}

	static class ViewHolder {
		ImageView card_iv;
		ImageView ar_iv;
		ImageView redbag_iv;
		TextView title;
		TextView look_tv;
		TextView receive_tv;
	}

	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		// TODO Auto-generated method stub
		if (notifyTo.equals(getClass())) {
			if (notifyFrom.equals(MainActivity.class)) {
//				showGuideView();
			}
		}
	}

	private void showGuideView() {

		if (firstImageView != null && firstImageView.isShown()) {
			final GuideView.Builder builder = new GuideView.Builder(mActivity,
					PackageUtil.getVersionName());
			LinearLayout layout_guide_redbag = (LinearLayout) ScreenUtil
					.inflate(R.layout.layout_guide_redbag);
			builder.setTextSize(20).addHintView(firstImageView,
					layout_guide_redbag, GuideView.Direction.LEFT_TOP,
					GuideView.MyShape.CIRCULAR, 0, 0, new OnClickCallback() {

						@Override
						public void onGuideViewClicked() {
							// TODO Auto-generated method stub
							builder.showNext();
						}
					});
			builder.show();
		}

	}

}
