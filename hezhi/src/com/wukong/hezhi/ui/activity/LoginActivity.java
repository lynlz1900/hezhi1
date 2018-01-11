package com.wukong.hezhi.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.QQUserInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.UserInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.CountDownUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.wxapi.WXEntryActivity;
import com.wukong.hezhi.wxapi.WXEntryActivity.ILoginCallBack;
import com.wukong.hezhi.wxapi.WXUserInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: LoginActivity
 * @Description: TODO(登录界面 )
 * @author HuZhiyin
 * @date 2017-1-10 上午11:51:21
 * 
 */
public class LoginActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.login);
	public String pageCode = "login";

	@ViewInject(R.id.identifying_code_tv)
	private CheckBox identifying_code_tv;

	@ViewInject(R.id.login_wechat_tv)
	private TextView login_wechat_tv;

	@ViewInject(R.id.login_qq_tv)
	private TextView login_qq_tv;

	@ViewInject(R.id.phone_et)
	private EditText phone_et;

	@ViewInject(R.id.identifying_code_et)
	private EditText identifying_code_et;

	private static final int REQUESTCODE_QQ = 1;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_login;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
		setSwipeBackEnable(false);// 禁止侧滑消除
		headView.setTitleStr(ContextUtil.getString(R.string.login));
		headView.setLeftTitleText("", R.drawable.icon_close1);
		setListener();
	}

	public void setListener() {
		phone_et.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
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

	@OnClick(value = { R.id.login_bt, R.id.login_wechat_tv, R.id.login_qq_tv, R.id.protocol_tv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.login_bt:// 手机登录
			loginByPhone();
			break;
		case R.id.login_wechat_tv:// 微信登录
			login_wechat_tv.setClickable(false);// 防止用户狂点出bug
			WXEntryActivity.login(new WXCallBack());
			break;
		case R.id.login_qq_tv:// qq登录
			login_qq_tv.setClickable(false);// 防止用户狂点出bug
			Intent qqIntent = new Intent(this, QQActivity.class);
			startActivityForResult(qqIntent, REQUESTCODE_QQ);
			break;
		case R.id.protocol_tv:
			Intent intent = new Intent(this, WebViewActivity.class);
			intent.putExtra(Constant.Extra.WEB_URL, HttpURL.URL1 + HttpURL.PROTOCAL);
			intent.putExtra(Constant.Extra.WEBVIEW_TITLE, getString(R.string.protocol));
			startActivity(intent);
			break;
		}
	}


	@Override
	@SuppressLint("NewApi")
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		login_wechat_tv.setClickable(true);
		login_qq_tv.setClickable(true);
	}

	/** 微信回调 */
	public class WXCallBack implements ILoginCallBack {

		@Override
		public void sucess(WXUserInfo wxUserInfo) {
			// TODO Auto-generated method stub
			loginByWX(wxUserInfo);// 登录成功，将微信用户信息提交到后台服务器
		}

		@Override
		public void fail() {
			// TODO Auto-generated method stub

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUESTCODE_QQ:
				QQUserInfo qqUserInfo = (QQUserInfo) data.getSerializableExtra(Constant.Extra.QQ_USER_INFO);
				if (qqUserInfo != null) {
					loginByQQ(qqUserInfo);
				}
				break;
			}
		}
	}

	/** 获取验证码 */
	public void getIdentifyingCode() {
		String phone = phone_et.getText().toString().trim();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("phone", phone);
		HttpManager.getInstance().post(HttpURL.URL1 + HttpURL.CHECK_CODE, map, new RequestCallBack<String>() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result, UserInfo.class);
				if (info != null) {
					if (info.getHttpCode().equals(HttpCode.SUCESS)) {
						new CountDownUtil(60, identifying_code_tv);// 倒计时
					} else if (info.getHttpCode().equals(HttpCode.FAIL)) {
						ScreenUtil.showToast(info.getPromptMessage());
					}
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				ScreenUtil.showToast(arg1);
			}
		});
	}

	/** 手机号登录 */
	public void loginByPhone() {
		String phone = phone_et.getText().toString().trim();
		String code = identifying_code_et.getText().toString().trim();
		if (TextUtils.isEmpty(phone) || phone.length() != 11 || TextUtils.isEmpty(code)) {
			ScreenUtil.showToast("请检查手机号或验证码");
			return;
		}
		showProgressDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("phone", phone);
		map.put("checkCode", code);
		String URL = HttpURL.URL1 + HttpURL.LOGIN;
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@SuppressWarnings("rawtypes")
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

	/** 提交QQ信息到服务器 */
	public void loginByQQ(QQUserInfo qqUserInfo) {
		showProgressDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("qqOpenId", qqUserInfo.getOpenId());
		map.put("nickName", qqUserInfo.getNickname());
		map.put("sex", qqUserInfo.getGender());
		map.put("showImageURL", qqUserInfo.getFigureurl_qq_2());
		map.put("loginWayType", "QQ");

		HttpManager.getInstance().post(HttpURL.URL1 + HttpURL.THIRDPARTYLOGIN, map, new RequestCallBack<String>() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				dismissProgressDialog();
				// TODO Auto-generated method stub
				ResponseJsonInfo info = JsonUtil.fromJson(arg0.result, UserInfo.class);
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
			}
		});

	}

	/** 提交微信用户信息到服务器 */
	public void loginByWX(final WXUserInfo wxUserInfo) {
		showProgressDialog();
		String sex = "女";
		switch (wxUserInfo.getSex()) {
		case 0:
			sex = "女";
			break;
		case 1:
			sex = "男";
			break;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("wxOpenId", wxUserInfo.getUnionid());
		map.put("nickName", wxUserInfo.getNickname());
		map.put("sex", sex);
		map.put("showImageURL", wxUserInfo.getHeadimgurl());
		map.put("loginWayType", "WX");
		HttpManager.getInstance().post(HttpURL.URL1 + HttpURL.THIRDPARTYLOGIN, map, new RequestCallBack<String>() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				dismissProgressDialog();
				// TODO Auto-generated method stub
				ResponseJsonInfo info = JsonUtil.fromJson(arg0.result, UserInfo.class);
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
			}
		});
	}
	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
		overridePendingTransition(R.anim.timepicker_anim_enter_bottom, R.anim.timepicker_anim_exit_bottom);
	}


	@SuppressWarnings("rawtypes")
	private void saveUserInfo(ResponseJsonInfo info) {
		UserInfo userInfo = (UserInfo) info.getBody();
		UserInfoManager.getInstance().setUserInfo(userInfo);
		if (!TextUtils.isEmpty(userInfo.getPhone())) {// 如果绑定了手机，
			finish();// 登录成功关闭当前页面
		} else {// 如果未绑定手机
			toActivity(BindPhoneActivity.class);
			finish();// 登录成功关闭当前页面
		}
	}

}
