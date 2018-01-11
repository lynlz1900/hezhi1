package com.wukong.hezhi.adapter;

import java.util.ArrayList;
import java.util.List;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.MyGuanZhuInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.ui.activity.BusinessAcitivty;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.ScreenUtil;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * @ClassName: MyAttentionAdapter
 * @Description: TODO(我的---》关注)
 * @author HuangFeiFei
 * @date 2017-10-23 14:25
 * 
 */
public class MyAttentionAdapter extends BaseAdapter {
	
	private List<MyGuanZhuInfo> myGuanZhuInfos;
	private Activity mActivity;

	public MyAttentionAdapter(List<MyGuanZhuInfo> myGuanZhuInfos,
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
		return myGuanZhuInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return myGuanZhuInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = ScreenUtil.inflate(R.layout.item_list_my_attention);
			holder = new ViewHolder();
			holder.image_name = (ImageView) convertView.findViewById(R.id.image_name);
			holder.text_name = (TextView) convertView.findViewById(R.id.text_name);
			holder.text_info = (TextView) convertView.findViewById(R.id.text_info);
			holder.text_volume = (TextView) convertView.findViewById(R.id.text_volume);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(myGuanZhuInfos.get(position) != null){
			holder.text_name.setText(myGuanZhuInfos.get(position).getTitle());
			holder.text_info.setText(myGuanZhuInfos.get(position).getIntroduction());
			holder.text_volume.setText(myGuanZhuInfos.get(position).getAppNum() + "\n"+
					ContextUtil.getString(R.string.works));
			GlideImgUitl.glideLoader(mActivity, myGuanZhuInfos.get(position).getPhoto(),
					holder.image_name, 2);
		}

		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(myGuanZhuInfos.get(position) != null){
					JumpActivityManager.getInstance().toActivity(mActivity,
							BusinessAcitivty.class, Constant.Extra.BUSINESS_ID,
							myGuanZhuInfos.get(position).getBusinessId());
				}
			}
		});
		
		return convertView;
	}

	static class ViewHolder {
		ImageView image_name;
		TextView text_name;
		TextView text_info;
		TextView text_volume;
	}
}
