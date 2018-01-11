package com.wukong.hezhi.ui.activity;

import java.util.HashMap;
import java.util.Map;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.AddressReceiptInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.AddressManager;
import com.wukong.hezhi.manager.KeyboardManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.AddrSelectWindows;
import com.wukong.hezhi.ui.view.AddrSelectWindows.IAddrCallBack;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import android.text.InputFilter;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

/***
 * 
 * @ClassName: AddressActivity
 * @Description: TODO(收货地址编辑界面)
 * @author HuangFeiFei
 * @date 2017-8-10 上午9：35
 * 
 */
public class AddressEditActivity extends BaseActivity {

	public String pageName = ContextUtil.getString(R.string.address_edit);
	public String pageCode = "address_receipt_edit";

	@ViewInject(R.id.edit_addr_name)
	private EditText edit_addr_name;

	@ViewInject(R.id.edit_addr_phone)
	private EditText edit_addr_phone;

	@ViewInject(R.id.edit_addr_simple)
	private TextView edit_addr_simple;

	@ViewInject(R.id.edit_addr_detail)
	private EditText edit_addr_detail;

	@ViewInject(R.id.check_addr_defaut)
	private CheckBox check_addr_defaut;
	
	@ViewInject(R.id.text_addr_save)
	private TextView text_addr_save;
	
	/** 是否设为默认地址 **/
	private boolean isAddrDefaut = false;
	/** 是否删除地址 **/
	@SuppressWarnings("unused")
	private boolean isDelete = false;
	/** 地址信息 */
	private AddressReceiptInfo addressReceiptInfo;
	/**是否来之收货地址管理界面**/
	private boolean isFromAddressManageActivity = false;
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_address_edit;
	}

	@Override
	protected void init() {
		headView.setTitleStr(ContextUtil.getString(R.string.address_edit));
		headView.setRightTitleText(ContextUtil.getString(R.string.address_delete));
		edit_addr_name.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
		edit_addr_phone.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
		edit_addr_detail.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
		
		isFromAddressManageActivity = getIntent().getBooleanExtra(Constant.Extra.ISFROM_ADDRESSMANAGE,false);
		if(isFromAddressManageActivity){
			text_addr_save.setText(ContextUtil.getString(R.string.save));
		}else{
			text_addr_save.setText(ContextUtil.getString(R.string.address_saveAndUsed));
		}
		
		initView();
	}

	/** 初始化控件 **/
	private void initView() {

		addressReceiptInfo = (AddressReceiptInfo) getIntent().getSerializableExtra(Constant.Extra.ADDR_INFO);

		if (addressReceiptInfo != null) {
			edit_addr_name.setText(addressReceiptInfo.getReceiver());
			edit_addr_phone.setText(addressReceiptInfo.getReceiverPhonePhone());
			edit_addr_simple.setText(addressReceiptInfo.getDistrict());
			edit_addr_detail.setText(addressReceiptInfo.getDetailAddr());

			if (addressReceiptInfo.getIsDefault() == 1) {
				check_addr_defaut.setChecked(true);
				isAddrDefaut = true;
			} else {
				check_addr_defaut.setChecked(false);
				isAddrDefaut = false;
			}
		}

		check_addr_defaut.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isAddrDefaut = isChecked;
			}
		});
	}

	@OnClick(value = { R.id.text_addr_save, R.id.header_right, R.id.edit_addr_simple })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.text_addr_save:

			saveAddr();

			break;

		case R.id.header_right:

			delete();

			break;
		case R.id.edit_addr_simple:

			KeyboardManager.getInstance().hideKeyboard(AddressEditActivity.this);// 隐藏键盘
			selectAddr();

			break;
		}
	}

	/** 选择地址弹出省市区对话框 **/
	private void selectAddr() {
		AddrSelectWindows.getInstance().show(new IAddrCallBack() {

			@Override
			public void reponse(String reponse) {

				try {

					String addr = reponse.split("\t")[0] + ContextUtil.getString(R.string.province) + 
						reponse.split("\t")[1] + ContextUtil.getString(R.string.city) + reponse.split("\t")[2];
					edit_addr_simple.setText(addr);
				} catch (Exception e) {
					edit_addr_simple.setText("");
				}
			}
		});
	}

	/** 弹出是否删除地址对话框 **/
	private void delete() {
		new CustomAlterDialog.Builder(mActivity).setMsg(ContextUtil.getString(R.string.address_delete_istrue))
				.setCancelButton(ContextUtil.getString(R.string.cancel), null)
				.setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {
					@Override
					public void onDialogClick(View v, CustomAlterDialog dialog) {
						// TODO Auto-generated method stub
						delAddr();
					}
				}).build().show();

	}

	/** 删除地址 **/
	private void delAddr() {
		if (addressReceiptInfo == null) {
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_isnull));
			return;
		}

		addrDelHttp();
	}

	/** 编辑保存地址 **/
	private void saveAddr() {

		if (addressReceiptInfo == null) {
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_isnull));
			return;
		}

		if (edit_addr_name.getText().toString().trim() == null
				|| edit_addr_name.getText().toString().trim().length() <= 0) {// 姓名不能为空
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_user_input));
			return;
		}

		if (edit_addr_name.getText().toString().trim().length() > 10) {
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_user_input_long));
			return;
		}

		if (edit_addr_phone.getText().toString().trim() == null
				|| edit_addr_phone.getText().toString().trim().length() <= 0) {// 手机号不能为空
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_phone_input));
			return;
		}

		if (edit_addr_phone.getText().toString().trim().length() != 11) {// 手机号格式错误
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_phone_input_iserror));
			return;
		}

		if (edit_addr_simple.getText().toString().trim() == null
				|| edit_addr_simple.getText().toString().trim().length() <= 0) {// 地区不能为空
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_simple_input_isnull));
			return;
		}

		if (edit_addr_detail.getText().toString().trim() == null
				|| edit_addr_detail.getText().toString().trim().length() <= 0) {// 详细地址不能为空
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_detail_input_isnull));
			return;
		}

		if (edit_addr_detail.getText().toString().trim().length() > 50) {
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_detail_input_long));
			return;
		}

		addrEditHttp();
	}

	/** 编辑保存地址成功 **/
	private void addrEditSuccess(AddressReceiptInfo addressReceiptInfo) {

		AddressManager.getInstance().saveAddr(addressReceiptInfo);
		if(isFromAddressManageActivity){
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_edit_success));
		}else{
			if(AddressActivity.addressActivity != null)
				AddressActivity.addressActivity.finish();
		}
		finish();
	}

	/** 编辑保存地址http接口 **/
	public void addrEditHttp() {
		String url = HttpURL.URL1 + HttpURL.ADDRESS_EDIT;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", addressReceiptInfo.getAddId());
		map.put("userId", UserInfoManager.getInstance().getUserId());
		map.put("receiver", edit_addr_name.getText().toString().trim());
		map.put("phone", edit_addr_phone.getText().toString().trim());
		map.put("district", edit_addr_simple.getText().toString().trim());
		map.put("address", edit_addr_detail.getText().toString().trim());
		map.put("isDefault", isAddrDefaut ? 1 : 0);
		LogUtil.i(url);
		LogUtil.i(map.toString());
		showProgressDialog();
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
				dismissProgressDialog();
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogUtil.i(arg0.result);
				dismissProgressDialog();
				ResponseJsonInfo info = JsonUtil.fromJson(arg0.result, AddressReceiptInfo.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					try {
						AddressReceiptInfo addressReceiptInfo = (AddressReceiptInfo) info.getBody();
						addrEditSuccess(addressReceiptInfo);
					} catch (Exception e) {
						ScreenUtil.showToast(ContextUtil.getString(R.string.address_edit_fail));
					}

				} else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)) {
					if (info.getPromptMessage() != null || info.getPromptMessage().length() > 0)
						ScreenUtil.showToast(info.getPromptMessage());
				} else {
					ScreenUtil.showToast(ContextUtil.getString(R.string.address_edit_fail));
				}
			}
		});
	}

	/** 删除地址http接口 **/
	public void addrDelHttp() {
		String url = HttpURL.URL1 + HttpURL.ADDRESS_DELETE;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", addressReceiptInfo.getAddId());
		LogUtil.i(url);
		LogUtil.i(map.toString());
		showProgressDialog();
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
				dismissProgressDialog();
			}

			@SuppressWarnings({ "rawtypes" })
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogUtil.i(arg0.result);
				dismissProgressDialog();
				ResponseJsonInfo info = JsonUtil.fromJson(arg0.result, ResponseJsonInfo.class);

				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					isDelete = true;
					AddressManager.getInstance().deleteAddr(addressReceiptInfo.getAddId());
					ScreenUtil.showToast(ContextUtil.getString(R.string.address_delete_success));
					finish();
				} else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)) {
					if (info.getPromptMessage() != null || info.getPromptMessage().length() > 0)
						ScreenUtil.showToast(info.getPromptMessage());
				} else {
					ScreenUtil.showToast(ContextUtil.getString(R.string.address_delete_fail));
				}
			}
		});
	}
}
