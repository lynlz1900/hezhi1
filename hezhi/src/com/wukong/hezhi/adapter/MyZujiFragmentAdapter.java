package com.wukong.hezhi.adapter;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.BitmapUtils;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.UnityInfo;
import com.wukong.hezhi.bean.UnityResourceInfo;
import com.wukong.hezhi.bean.VuforiaInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.db.DBManager;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.manager.UnityManger;
import com.wukong.hezhi.ui.activity.ProductMainActivity;
import com.wukong.hezhi.ui.activity.UnityPlayerActivity;
import com.wukong.hezhi.ui.activity.WebViewActivity;
import com.wukong.hezhi.ui.segment.MyZuJiFragment;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyZujiFragmentAdapter extends BaseAdapter {
	private List<UnityInfo> unityInfos;
	private Activity mActivity;
	BitmapUtils bitmapUtils = new BitmapUtils(ContextUtil.getContext());

	public MyZujiFragmentAdapter(List<UnityInfo> unityInfos, Activity mActivity) {
		super();
		if (unityInfos == null) {
			this.unityInfos = new ArrayList<UnityInfo>();
		} else {
			this.unityInfos = unityInfos;
		}
		this.mActivity = mActivity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return unityInfos.size();
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
			convertView = ScreenUtil.inflate(R.layout.item_list_my_zuji_fragment);
			holder = new ViewHolder();
			holder.card_iv = (ImageView) convertView.findViewById(R.id.card_iv);
			holder.ar_iv = (ImageView) convertView.findViewById(R.id.ar_iv);
			holder.redbag_ll = (RelativeLayout) convertView.findViewById(R.id.redbag_ll);
			holder.title = (TextView) convertView.findViewById(R.id.title_tv);
			holder.look_tv = (TextView) convertView.findViewById(R.id.look_tv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		UnityInfo unityInfo = unityInfos.get(pos);
		if (unityInfo != null) {
			holder.title.setText(unityInfo.getName());
			GlideImgUitl.glideLoader(mActivity, unityInfo.getPicUrl(), holder.card_iv, 2);
			if ("UNITY3D".equals(unityInfo.getResourceType())) {// 判断是否是ar应用
				holder.ar_iv.setVisibility(View.VISIBLE);
			} else {
				holder.ar_iv.setVisibility(View.INVISIBLE);
			}
		}

		convertView.setOnClickListener(new OnClickListener() {// 进入unity播放记录
			@Override
			public void onClick(View v) {
				UnityInfo unityInfo = unityInfos.get(pos);
				if (unityInfo == null) {
					return;
				}
				String reSouceType = unityInfo.getResourceType();
				if ("UNITY3D".equals(reSouceType)) {
					String json = DBManager.getInstance().qurreyById(unityInfos.get(position).getPicId());// 通过picId查找数据库
					json = UnityManger.getInstance().toUTFormat(UnityManger.SAO_YI_SAO, json);
					JumpActivityManager.getInstance().toActivity(mActivity, UnityPlayerActivity.class,
							Constant.Extra.UNITY_INFO, json);
				} else if ("MP4".equals(reSouceType)) {
					VuforiaInfo vuforiaInfo = new VuforiaInfo();
					vuforiaInfo.setTitle(unityInfo.getName());
					vuforiaInfo.setBusinessId(unityInfo.getBusinessId());
					vuforiaInfo.setPpid(unityInfo.getPpId());
					vuforiaInfo.setTargetId(unityInfo.getTargetId());
					JumpActivityManager.getInstance().toActivity(mActivity, ProductMainActivity.class,
							Constant.Extra.VUFORIA, vuforiaInfo);
				} else if ("URL".toString().equals(reSouceType) || "PICTURE".equals(reSouceType)) {// 如果是URL类型，直接跳转至WEB
					String webUrl = "";
					UnityResourceInfo unityResourceInfo = null;
					if (unityInfo.getResourceList() != null && unityInfo.getResourceList().size() > 0) {
						unityResourceInfo = unityInfo.getResourceList().get(0);
						if (unityResourceInfo != null) {
							webUrl = unityResourceInfo.getOssUrl();
						}
					}
					JumpActivityManager.getInstance().toActivity(mActivity, WebViewActivity.class,
							Constant.Extra.WEB_URL, webUrl);
				}
			}
		});
		convertView.setOnLongClickListener(new OnLongClickListener() {// 长按删除

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				picId = unityInfos.get(position).getPicId();
				showDeleteDialog(picId);
				return false;
			}
		});
		return convertView;

	}

	public String picId;

	/**
	 * 是否删除
	 */
	protected void showDeleteDialog(String id) {
		new CustomAlterDialog.Builder(mActivity).setMsg(ContextUtil.getString(R.string.delete_tip))
				.setCancelButton(ContextUtil.getString(R.string.cancel), null)
				.setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {
					@Override
					public void onDialogClick(View v, CustomAlterDialog dialog) {
						// TODO Auto-generated method stub
						DBManager.getInstance().deleteById(picId);
						ObserveManager.getInstance().notifyState(MyZuJiFragment.class, MyZujiFragmentAdapter.class,
								null);// 通知观察者数据发生了变化
					}
				}).build().show();

	}

	static class ViewHolder {
		ImageView card_iv;
		ImageView ar_iv;
		RelativeLayout redbag_ll;
		TextView title;
		TextView look_tv;
	}
}
