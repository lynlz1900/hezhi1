package com.wukong.hezhi.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.AddressReceiptInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.activity.AddressEditActivity;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 收货地址管理ListView的适配器
 * 
 * @ClassName: AddressReceiptManageAdapter
 * @Description: TODO(收货地址管理ListView的适配器类)
 * @author HuangFeiFei
 * @date 2017-8-9 下午17：35
 * 
 */
public class AddressReceiptManageAdapter extends BaseAdapter {

	private List<AddressReceiptInfo> list;
	private Activity mActivity;
	ViewHolder vh = null;
	
	public AddressReceiptManageAdapter(List<AddressReceiptInfo> list, Activity mActivity) {
		super();

		if (list == null) {
			this.list = new ArrayList<AddressReceiptInfo>();
		} else {
			this.list = list;
		}
		this.mActivity = mActivity;
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
		if (convertView == null) {
			convertView = ScreenUtil.inflate(R.layout.item_list_address_manage);
			vh = new ViewHolder();
			vh.text_user_name = (TextView) convertView.findViewById(R.id.text_user_name);
			vh.text_user_phone = (TextView) convertView.findViewById(R.id.text_user_phone);
			vh.text_user_addr = (TextView) convertView.findViewById(R.id.text_user_addr);
			vh.text_default = (TextView) convertView.findViewById(R.id.text_default);
			vh.address_ll = (RelativeLayout) convertView.findViewById(R.id.address_ll);
			vh.text_edit = (TextView) convertView.findViewById(R.id.text_edit);
			vh.text_delete = (TextView) convertView.findViewById(R.id.text_delete);
			vh.check_default = (CheckBox) convertView.findViewById(R.id.check_default);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		vh.text_user_name.setText(list.get(position).getReceiver());
		vh.text_user_phone.setText(list.get(position).getReceiverPhonePhone());
		vh.text_user_addr.setText(list.get(position).getDistrict() + list.get(position).getDetailAddr());

		if (list.get(position).getIsDefault() == 1){
			vh.text_default.setVisibility(View.VISIBLE);
			vh.check_default.setChecked(true);
		}
		else{
			vh.text_default.setVisibility(View.GONE);
			vh.check_default.setChecked(false);
		}

		vh.text_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mActivity != null){
					gotoAddrEdit(position);
				}
			}
		});
		
		vh.text_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addrdelete(position);
			}
		});
		
		vh.check_default.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean isChecked = list.get(position).getIsDefault() == 1 ? true : false;
				addrSetDefault(position,!isChecked);
			}
		});

		return convertView;
	}

	/**
	 * 跳转到收货地址编辑界面
	 * 
	 * @author HuangFeiFei
	 * 
	 */
	private void gotoAddrEdit(int position){
		Intent intent = new Intent();
		intent.putExtra(Constant.Extra.ADDR_INFO, list.get(position));
		intent.putExtra(Constant.Extra.ISFROM_ADDRESSMANAGE, true);
		intent.setClass(mActivity, AddressEditActivity.class);
		mActivity.startActivity(intent);
	}
	
	/**
	 * 删除地址
	 * 
	 * @author HuangFeiFei
	 * 
	 */
	private void addrdelete(final int position){
		new CustomAlterDialog.Builder(mActivity).setMsg(ContextUtil.getString(R.string.address_delete_istrue))
		.setCancelButton(ContextUtil.getString(R.string.cancel), null)
		.setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {
			@Override
			public void onDialogClick(View v, CustomAlterDialog dialog) {
				// TODO Auto-generated method stub
				addrDelHttp(position);
			}
		}).build().show();
	}
	
	/**
	 * 设置默认地址
	 * 
	 * @author HuangFeiFei
	 * 
	 */
	private void addrSetDefault(final int position,boolean isChecked){
		addrEditHttp(position, isChecked);
	}
	
	/** 删除地址http接口 **/
	public void addrDelHttp(final int position) {
		String url = HttpURL.URL1 + HttpURL.ADDRESS_DELETE;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", list.get(position).getAddId());
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
				ResponseJsonInfo info = JsonUtil.fromJson(arg0.result, ResponseJsonInfo.class);

				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					ScreenUtil.showToast(ContextUtil.getString(R.string.address_delete_success));
					
					list.remove(position);
					notifyDataSetChanged();
				} else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)) {
					if (info.getPromptMessage() != null || info.getPromptMessage().length() > 0)
						ScreenUtil.showToast(info.getPromptMessage());
				} else {
					ScreenUtil.showToast(ContextUtil.getString(R.string.address_delete_fail));
				}
			}
		});
	}
	
	/** 编辑保存地址http接口 **/
	public void addrEditHttp(final int position,boolean isChecked) {
		String url = HttpURL.URL1 + HttpURL.ADDRESS_EDIT;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", list.get(position).getAddId());
		map.put("userId", UserInfoManager.getInstance().getUserId());
		map.put("receiver", list.get(position).getReceiver());
		map.put("phone", list.get(position).getReceiverPhonePhone());
		map.put("district", list.get(position).getDistrict());
		map.put("address",list.get(position).getDetailAddr());
		map.put("isDefault", isChecked ? 1 : 0);
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
				ResponseJsonInfo info = JsonUtil.fromJson(arg0.result, AddressReceiptInfo.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					try {
						AddressReceiptInfo addressReceiptInfo = (AddressReceiptInfo) info.getBody();
						addrEditSuccess(position,addressReceiptInfo.getIsDefault());
					} catch (Exception e) {
					}

				} else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)) {
					if (info.getPromptMessage() != null || info.getPromptMessage().length() > 0)
						ScreenUtil.showToast(info.getPromptMessage());
				} else {
				}
			}
		});
	}
	
	/**
	 * 设置默认地址更新UI
	 * 
	 * @author HuangFeiFei
	 * 
	 */
	private void addrEditSuccess(final int position,int isDefault){
		for(int i=0;i<list.size();i++){
			if(isDefault == 1){
				if(i == position)
					list.get(i).setIsDefault(1);
				else
					list.get(i).setIsDefault(0);
			}else{
				list.get(i).setIsDefault(0);
			}
		}
		
		notifyDataSetChanged();
	}
	
	/**
	 * 封装ListView中item控件以优化ListView
	 * 
	 * @author HuangFeiFei
	 * 
	 */
	public static class ViewHolder {
		TextView text_user_name;
		TextView text_user_phone;
		TextView text_user_addr;
		TextView text_default;
		RelativeLayout address_ll;
		TextView text_edit;
		TextView text_delete;
		CheckBox check_default;
	}
}