package com.wukong.hezhi.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.ui.activity.MyCustomizationActivity;
import com.wukong.hezhi.ui.activity.MyCustomizationTobuyActivity;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.DataUtil;
import com.wukong.hezhi.utils.GlideImgUitl;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的定制2列ListView的适配器
 * 
 * @ClassName: MyCustomizationAdapter
 * @Description: TODO(我的定制2列ListView的适配器类)
 * @author HuangFeiFei
 * @date 2017-8-4 下午16：12
 * 
 */
public class MyCustomizationAdapter extends BaseAdapter {

	private List<CommodityInfo> list;
	private Activity mActivity;
	private int position;
	
	public MyCustomizationAdapter(List<CommodityInfo> list, Activity mActivity
			,int position) {
		super();
		if (list == null) {
			this.list = new ArrayList<CommodityInfo>();
		} else {
			this.list = list;
		}
		this.mActivity = mActivity;
		this.position = position;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = ScreenUtil
					.inflate(R.layout.item_list_my_customization);
			vh = new ViewHolder();
			vh.customization_name = (TextView) convertView
					.findViewById(R.id.customization_name);
			vh.customization_price = (TextView) convertView
					.findViewById(R.id.customization_price);
			vh.customization_ly = (LinearLayout) convertView
					.findViewById(R.id.customization_ly);
			vh.customization_ry = (RelativeLayout) convertView
					.findViewById(R.id.customization_ry);
			vh.customization_image = (ImageView) convertView
					.findViewById(R.id.customization_image);
			vh.customization_delete = (ImageView) convertView
					.findViewById(R.id.customization_delete);
			vh.image_custom_type = (ImageView) convertView
					.findViewById(R.id.image_custom_type);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		vh.customization_name.setText(list.get(position).getName());
		vh.customization_price.setText(ContextUtil
				.getString(R.string.money_type)
				+ DataUtil.double2pointString(list.get(position).getPrice()));
		GlideImgUitl.glideLoader(mActivity, list.get(position).getImageUrl(),
				vh.customization_image, 2);

		if (MyCustomizationActivity.isEdit) {
			vh.customization_delete.setVisibility(View.VISIBLE);
		} else {
			vh.customization_delete.setVisibility(View.GONE);
		}

		if(list.get(position).getCustomizationType() == 1){
			vh.image_custom_type.setVisibility(View.VISIBLE);
			vh.image_custom_type.setImageResource(R.drawable.icon_customsurface);
		}
		else if(list.get(position).getCustomizationType() == 2){
			vh.image_custom_type.setVisibility(View.VISIBLE);
			vh.image_custom_type.setImageResource(R.drawable.icon_custommedio);
		}
		else{
			vh.image_custom_type.setVisibility(View.INVISIBLE);
		}
		
		vh.customization_ry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(MyCustomizationActivity.isEdit)
					return;
				JumpActivityManager.getInstance().toActivity(mActivity,
						MyCustomizationTobuyActivity.class,
						Constant.Extra.CUSTOMIZATION_INFO, list.get(position));
			}
		});

		vh.customization_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				delete(position);
			}
		});

		return convertView;
	}

	/**
	 * 封装ListView中item控件以优化ListView
	 * 
	 * @author HuangFeiFei
	 * 
	 */
	public static class ViewHolder {
		TextView customization_name;
		TextView customization_price;
		LinearLayout customization_ly;
		RelativeLayout customization_ry;
		ImageView customization_image;
		ImageView customization_delete;
		ImageView image_custom_type;
	}

	/** 弹出是否删除该定制对话框 **/
	private void delete(final int position) {
		new CustomAlterDialog.Builder(mActivity).setMsg(ContextUtil.getString(R.string.customization_isdelete_true))
		.setCancelButton(ContextUtil.getString(R.string.cancel), null)
		.setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {
			@Override
			public void onDialogClick(View v, CustomAlterDialog dialog) {
				// TODO Auto-generated method stub
				deleteHttp(position);
			}
		}).build().show();
		
		
	}
	
	/** 删除该定制http接口 **/
	private void deleteHttp(final int position) {
		String url = HttpURL.URL1 + HttpURL.MY_CUST_DEL;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", list.get(position).getCustomId());
		LogUtil.i(url);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogUtil.i(arg0.result);
				ResponseJsonInfo info = JsonUtil.fromJson(
						arg0.result, ResponseJsonInfo.class);
				
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					list.remove(position);
					notifyDataSetChanged();

					ScreenUtil.showToast(ContextUtil
							.getString(R.string.customiation_delete_success));
					
					checkListView();
					
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
				}else{
					ScreenUtil.showToast(ContextUtil.getString(R.string.customiation_delete_fail));
				}
			}
		});
	}
	
	/** 判断 **/
	private void checkListView(){
		if(list == null || list.size() <1){
			ObserveManager.getInstance().notifyState(MyCustomizationActivity.class, MyCustomizationAdapter.class, position);
		}
	}
}