package com.wukong.hezhi.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import java.util.HashMap;
import java.util.Map;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.BLELockManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.CountDownUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 
 * @ClassName: LockSMSActivity
 * @Description: TODO(开锁短信验证界面)
 * @author HuangFeiFei
 * @date 2017-12-7 
 * 
 */
public class LockSMSActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.lock_sms);
	public String pageCode = "lock_sms";

	@ViewInject(R.id.phone_et)
	private EditText phone_et;

	@ViewInject(R.id.btn_smscode)
	private Button btn_smscode;
	
	@ViewInject(R.id.text_getsms)
	private CheckBox text_getsms;
	
	@ViewInject(R.id.text_phone)
	private TextView text_phone;
	
	private String smsCode;
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_lock_smscode;
	}

	@Override
	protected void init() {
		headView.setTitleStr(ContextUtil.getString(R.string.lock_sms));
		text_phone.setText(UserInfoManager.getInstance().getPhone());
		text_getsms.setChecked(true);
		new CountDownUtil(60, text_getsms);// 倒计时
		
		text_getsms.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					text_getsms.requestFocus();
					getSms();
				}
			}
		});
	}

	@OnClick(value = {R.id.btn_smscode ,R.id.text_getsms})
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_smscode:
			sendLockCode();
			break;
		}
	}

	/** 验证短信验证码 **/
	private void sendLockCode(){
		smsCode = phone_et.getText().toString();
		if(TextUtils.isEmpty(smsCode)){
			ScreenUtil.showToast(ContextUtil.getString(R.string.lock_sms_isnull));
			return;
		}
		
		LogUtil.i("Mac:"+BLELockManager.mac+"  authCode:"+BLELockManager.authCode);
//		String url = "http://172.17.10.197:80/remoteAuthenticLock/smsOpenLock";
		String url = HttpURL.URL + HttpURL.SMSOPENLOCK;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mac", BLELockManager.mac);
		map.put("authCode", BLELockManager.authCode);
		map.put("smsCode", smsCode);
		map.put("phone", UserInfoManager.getInstance().getPhone());
		String URL = url + "?mac=" + BLELockManager.mac + "&authCode=" 
				+ BLELockManager.authCode + "&smsCode=" + smsCode + "&phone=" + UserInfoManager.getInstance().getPhone();
		LogUtil.i(URL);
		showProgressDialog();
		HttpManager.getInstance().get(URL, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
				dismissProgressDialog();
			}

			@SuppressWarnings({"rawtypes" })
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				dismissProgressDialog();
				ResponseJsonInfo info = JsonUtil.fromJson(
						arg0.result, ResponseJsonInfo.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					toAct(LockBLEActivity.class);
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
				}else{
					ScreenUtil.showToast(ContextUtil.getString(R.string.lock_sms_error));
				}
			}
		});
	}
	
	/** 请求短信验证码 **/
	private void getSms(){
		String url = HttpURL.URL1 + HttpURL.GETVERIFICATIONCODEBYPHONE;
		 RequestParams params = new RequestParams();
		 params.addBodyParameter("phone", UserInfoManager.getInstance().getPhone());
		LogUtil.i(url);
		LogUtil.i(params.getEntity().toString());
		showProgressDialog();
		 new HttpUtils().send(HttpRequest.HttpMethod.POST,url,params, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
				text_getsms.setChecked(false);
				dismissProgressDialog();
			}

			@SuppressWarnings({"rawtypes" })
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				dismissProgressDialog();
				ResponseJsonInfo info = JsonUtil.fromJson(
						arg0.result, ResponseJsonInfo.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					new CountDownUtil(60, text_getsms);// 倒计时
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
					text_getsms.setChecked(false);
				}else{
					ScreenUtil.showToast(ContextUtil.getString(R.string.lock_sms_isget));
					text_getsms.setChecked(false);
				}
			}
		});
	}
	
	/** 若没有登录，则跳到登录页面 */
	@SuppressWarnings("rawtypes")
	public void toAct(Class clazz) {
		if (!UserInfoManager.getInstance().isLogin()
				|| UserInfoManager.getInstance().getUserInfo() == null) {
			toActivity(LoginActivity.class);
		} else {
			toActivity(clazz);
		}
	}
}
