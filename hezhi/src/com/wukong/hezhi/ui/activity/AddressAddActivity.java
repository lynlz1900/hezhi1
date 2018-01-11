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
 * @Description: TODO(收货地址添加界面)
 * @author HuangFeiFei
 * @date 2017-8-10 上午9：35
 * 
 */
public class AddressAddActivity extends BaseActivity{

	public String pageName = ContextUtil.getString(R.string.address_add);
	public String pageCode ="address_receipt_add";
	
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
	
	/**是否设为默认地址**/
	private boolean isAddrDefaut = false;
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
		return R.layout.activity_address_add;
	}

	@Override
	protected void init() {
		headView.setTitleStr(ContextUtil.getString(R.string.address_add));
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
	
	/**初始化控件**/
	private void initView() {
		
		check_addr_defaut.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isAddrDefaut = isChecked;
			}
		});
	}
	
	@OnClick(value = { R.id.text_addr_save,R.id.edit_addr_simple })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.text_addr_save:
			
			saveAddr();
			
			break;
			
		case R.id.edit_addr_simple:
			
			KeyboardManager.getInstance().hideKeyboard(AddressAddActivity.this);// 隐藏键盘
			selectAddr();
			
			break;
		}
	}
	
	/**选择地址弹出省市区对话框**/
	private void selectAddr() {
		AddrSelectWindows.getInstance().show(new com.wukong.hezhi.ui.view.AddrSelectWindows.IAddrCallBack() {
			
			@Override
			public void reponse(String reponse) {
				try {
					
					String addr = reponse.split("\t")[0]+ContextUtil.getString(R.string.province)+
							reponse.split("\t")[1]+ContextUtil.getString(R.string.city)+reponse.split("\t")[2];
					edit_addr_simple.setText(addr);
				} catch (Exception e) {
					edit_addr_simple.setText("");
				}
			}
		});
	}
	
	/**新建保存地址**/
	private void saveAddr(){
		if(edit_addr_name.getText().toString().trim() == null || 
				edit_addr_name.getText().toString().trim().length() <=0){//姓名不能为空
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_user_input));
			return;
		}
		
		if(edit_addr_name.getText().toString().trim().length() >10){
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_user_input_long));
			return;
		}
		
		if(edit_addr_phone.getText().toString().trim() == null || 
				edit_addr_phone.getText().toString().trim().length() <=0){//手机号不能为空
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_phone_input));
			return;
		}
		
		if(edit_addr_phone.getText().toString().trim().length() != 11){//手机号格式错误
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_phone_input_iserror));
			return;
		}
		
		if(edit_addr_simple.getText().toString().trim() == null || 
				edit_addr_simple.getText().toString().trim().length() <=0){//地区不能为空
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_simple_input_isnull));
			return;
		}
		
		if(edit_addr_detail.getText().toString().trim() == null || 
				edit_addr_detail.getText().toString().trim().length() <=0){//详细地址不能为空
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_detail_input_isnull));
			return;
		}
		
		if(edit_addr_detail.getText().toString().trim().length() >50){
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_detail_input_long));
			return;
		}
		
		addrAddHttp();
	}
	
	/** 添加保存地址成功 **/
	private void addrAddSuccess(AddressReceiptInfo addressReceiptInfo){
		
		AddressManager.getInstance().saveAddr(addressReceiptInfo);
		if(isFromAddressManageActivity){
			ScreenUtil.showToast(ContextUtil.getString(R.string.address_add_success));
		}else{
			if(AddressActivity.addressActivity != null)
				AddressActivity.addressActivity.finish();
		}
		finish();
	}
	
	/** 收货地址新增http接口 **/
	public void addrAddHttp(){
		String url = HttpURL.URL1 + HttpURL.ADDRESS_ADD;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", UserInfoManager.getInstance().getUserId());
		map.put("receiver", edit_addr_name.getText().toString().trim());
		map.put("phone", edit_addr_phone.getText().toString().trim());
		map.put("district", edit_addr_simple.getText().toString().trim());
		map.put("address", edit_addr_detail.getText().toString().trim());
		map.put("isDefault", isAddrDefaut?1:0);
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
				ResponseJsonInfo info = JsonUtil.fromJson(
						arg0.result, AddressReceiptInfo.class);
				
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					try {
						AddressReceiptInfo addressReceiptInfo = (AddressReceiptInfo)info.getBody();
						addrAddSuccess(addressReceiptInfo);
					} catch (Exception e) {
						ScreenUtil.showToast(ContextUtil.getString(R.string.address_add_fail));
					}
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
				}else{
					ScreenUtil.showToast(ContextUtil.getString(R.string.address_add_fail));
				}
			}
		});
	}
}
