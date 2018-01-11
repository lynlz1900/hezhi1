package com.wukong.hezhi.ui.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.UserInfo;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.CountDownUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: BindingPhoneActivity
 * @Description: TODO(绑定手机)
 * @author HuZhiyin
 * @date 2017-1-10 下午3:01:22
 * 
 */
public class BindPhoneActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.bind_phone);
	public String pageCode = "bind_phone";

	@ViewInject(R.id.login_bt)
	private Button login_bt;

	@ViewInject(R.id.identifying_code_tv)
	private CheckBox identifying_code_tv;

	@ViewInject(R.id.phone_et)
	private EditText phone_et;

	@ViewInject(R.id.identifying_code_et)
	private EditText identifying_code_et;

	@ViewInject(R.id.login_ll)
	private LinearLayout login_ll;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_bind_phone;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		setSwipeBackEnable(false);// 闪屏页禁止侧滑消除
		overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
		headView.setTitleStr(ContextUtil.getString(R.string.bind_phone));
		headView.setLeftTitleText("", R.drawable.icon_close1);
		headView.setLeftLis(this);
		userInfo = UserInfoManager.getInstance().getUserInfo();
		setListener();
	}

	public void setListener() {
		phone_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.length() == 11) {
					identifying_code_tv.setEnabled(true);
				} else {
					identifying_code_tv.setEnabled(false);
				}
			}
		});
		identifying_code_tv.setEnabled(false);

		identifying_code_tv.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					String phoneNum = phone_et.getText().toString().trim();
					if (!TextUtils.isEmpty(phoneNum) && phoneNum.length() == 11) {
						identifying_code_et.requestFocus();
						getIdentifyingCode();
					} else {
						ScreenUtil.showToast("请输入正确的手机号码");
						identifying_code_tv.setChecked(false);
					}
				}
			}
		});
	}

	@OnClick(value = { R.id.login_bt, R.id.header_left })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.login_bt:
			bindPhone();
			break;
		case R.id.header_left:
			cleanUserInfo();
			finish();
			break;
		}
	}

	private UserInfo userInfo;

	/** 获取验证码 */
	public void getIdentifyingCode() {
		if (userInfo == null) {
			return;
		}
		String wechatId = userInfo.getWxOpenId();
		String qqId = userInfo.getQqOpenId();
		String weiboId = userInfo.getWbOpenId();
		String loginWayType = "";
		String acctountOpenIdKey = "";
		String openId = "";
		if (!TextUtils.isEmpty(wechatId)) {
			loginWayType = "WX";
			acctountOpenIdKey = "wxOpenId";
			openId = userInfo.getWxOpenId();
		}
		if (!TextUtils.isEmpty(qqId)) {
			loginWayType = "QQ";
			acctountOpenIdKey = "qqOpenId";
			openId = userInfo.getQqOpenId();
		}
		if (!TextUtils.isEmpty(weiboId)) {
			loginWayType = "WB";
			acctountOpenIdKey = "wbOpenId";
			openId = userInfo.getWbOpenId();
		}

		String phone = phone_et.getText().toString().trim();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", UserInfoManager.getInstance().getUserId());
		map.put("phone", phone);
		map.put("loginWayType", loginWayType);
		map.put(acctountOpenIdKey, openId);
		HttpManager.getInstance().post(HttpURL.URL1 + HttpURL.CHECK_CODE_BINDPHONE, map, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result, UserInfo.class);
				if (info != null && info.getHttpCode().equals(HttpCode.FAIL)) {
					ScreenUtil.showToast(info.getPromptMessage());
					identifying_code_tv.setChecked(false);
				} else if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					new CountDownUtil(60, identifying_code_tv);// 倒计时
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				ScreenUtil.showToast(arg1);
			}
		});
	}

	/** 绑定手机 */
	public void bindPhone() {

		String phone = phone_et.getText().toString().trim();
		String code = identifying_code_et.getText().toString().trim();
		if (TextUtils.isEmpty(phone) || phone.length() != 11 || TextUtils.isEmpty(code)) {
			ScreenUtil.showToast("请检查手机号或验证码");
			return;
		}
		if (userInfo == null) {
			return;
		}
		String wechatId = userInfo.getWxOpenId();
		String qqId = userInfo.getQqOpenId();
		String weiboId = userInfo.getWbOpenId();
		String loginWayType = "";
		String acctountOpenIdKey = "";
		String openId = "";
		if (!TextUtils.isEmpty(wechatId)) {
			loginWayType = "WX";
			acctountOpenIdKey = "wxOpenId";
			openId = userInfo.getWxOpenId();
		}
		if (!TextUtils.isEmpty(qqId)) {
			loginWayType = "QQ";
			acctountOpenIdKey = "qqOpenId";
			openId = userInfo.getQqOpenId();
		}
		if (!TextUtils.isEmpty(weiboId)) {
			loginWayType = "WB";
			acctountOpenIdKey = "wbOpenId";
			openId = userInfo.getWbOpenId();
		}
		showProgressDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", UserInfoManager.getInstance().getUserId());
		map.put("phone", phone);
		map.put("checkCode", code);
		map.put("loginWayType", loginWayType);
		String URL = HttpURL.URL1 + HttpURL.BIND_PHONE;
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				dismissProgressDialog();
				// TODO Auto-generated method stub
				ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result, UserInfo.class);
				if (info != null) {
					if (info.getHttpCode().equals(HttpCode.SUCESS)) {
						saveUserInfo(info);

					} else if (info.getHttpCode().equals(HttpCode.FAIL)) {
						ScreenUtil.showToast(info.getPromptMessage());
					}
				}

			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				ScreenUtil.showToast(arg1);
			}
		});
	}

	private boolean bindPhone = false;// 是否绑定

	/** 删除用户信息 */
	private void cleanUserInfo() {
		if (!bindPhone) {// 如果用户不绑定手机，则删除用户数据
			UserInfoManager.getInstance().cleanUserInfo();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		cleanUserInfo();
		LogUtil.d("onBackPressed");
		super.onBackPressed();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
	}

	private void saveUserInfo(ResponseJsonInfo info) {
		bindPhone = true;// 标识绑定手机
		UserInfoManager.getInstance().setUserInfo((UserInfo) info.getBody());// 保存用户信息
		finish();// 登录成功关闭当前页面
	}
}
