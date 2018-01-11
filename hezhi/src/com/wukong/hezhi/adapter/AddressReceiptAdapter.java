package com.wukong.hezhi.adapter;

import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.AddressReceiptInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.AddressManager;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.ui.activity.AddressActivity;
import com.wukong.hezhi.ui.activity.AddressEditActivity;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 收货地址ListView的适配器
 * 
 * @ClassName: MyCustomizationAdapter
 * @Description: TODO(收货地址ListView的适配器类)
 * @author HuangFeiFei
 * @date 2017-8-9 下午17：35
 * 
 */
public class AddressReceiptAdapter extends BaseAdapter {

	private List<AddressReceiptInfo> list;
	private Activity mActivity;

	public AddressReceiptAdapter(List<AddressReceiptInfo> list, Activity mActivity) {
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
		ViewHolder vh = null;
		if (convertView == null) {
			convertView = ScreenUtil.inflate(R.layout.item_list_address);
			vh = new ViewHolder();
			vh.text_user_name = (TextView) convertView.findViewById(R.id.text_user_name);
			vh.text_user_phone = (TextView) convertView.findViewById(R.id.text_user_phone);
			vh.text_user_addr = (TextView) convertView.findViewById(R.id.text_user_addr);
			vh.text_default = (TextView) convertView.findViewById(R.id.text_default);
			vh.image_default = (ImageView) convertView.findViewById(R.id.image_default);
			vh.image_edit = (LinearLayout) convertView.findViewById(R.id.image_edit);
			vh.address_ll = (LinearLayout) convertView.findViewById(R.id.address_ll);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder) convertView.getTag();
		}

		vh.text_user_name.setText(list.get(position).getReceiver());
		vh.text_user_phone.setText(list.get(position).getReceiverPhonePhone());
		vh.text_user_addr.setText(list.get(position).getDistrict() + list.get(position).getDetailAddr());

		if (list.get(position).getIsDefault() == 1)
			vh.text_default.setVisibility(View.VISIBLE);
		else
			vh.text_default.setVisibility(View.GONE);

		if (AddressActivity.addressReceiptInfoDefault == null)
			vh.image_default.setVisibility(View.GONE);
		else {
			if (AddressActivity.addressReceiptInfoDefault.getAddId() == list.get(position).getAddId())
				vh.image_default.setVisibility(View.VISIBLE);
			else
				vh.image_default.setVisibility(View.GONE);
		}

		vh.image_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				JumpActivityManager.getInstance().toActivity(mActivity, AddressEditActivity.class,
						Constant.Extra.ADDR_INFO, list.get(position));
			}
		});

		vh.address_ll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mActivity != null) {
					AddressManager.getInstance().saveAddr(list.get(position));
					mActivity.finish();
				}
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
		TextView text_user_name;
		TextView text_user_phone;
		TextView text_user_addr;
		TextView text_default;
		ImageView image_default;
		LinearLayout image_edit;
		LinearLayout address_ll;
	}
}