package com.wukong.hezhi.ui.activity;

import android.view.View;
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
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: SettingActivity
 * @Description: TODO(设置页面)
 * @author HuZhiyin
 * @date 2017-1-10 下午3:01:22
 * 
 */
public class SettingActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.setting);
	public String pageCode = "setting";

	@ViewInject(R.id.logout_ll)
	private LinearLayout logout_ll;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_setting;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.setting));
		if (!UserInfoManager.getInstance().isLogin()) {
			logout_ll.setVisibility(View.GONE);
		} else {
			logout_ll.setVisibility(View.VISIBLE);
		}
	}

	@OnClick(value = { R.id.safe_ll, R.id.about_ll, R.id.feedback_ll,
			R.id.logout_ll, R.id.contact_us_ll,R.id.invite_friend_ll,R.id.address_ll })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.safe_ll:
			toAct(AccountSafeActivity.class);
			break;
		case R.id.about_ll:
			toActivity(AboutActivity.class);
			break;
		case R.id.feedback_ll:
			toActivity(FeedbackActivity.class);
			break;
		case R.id.logout_ll:
			showLogoutDiaglog();
			break;
		case R.id.contact_us_ll:
			toActivity(ContacUsActivity.class);
			break;
		case R.id.invite_friend_ll:
			toActivity(InviteFriendActivity.class);
			break;
		case R.id.address_ll:
			toAct(AddressManageActivity.class);
			break;
		}
	}

	private void showLogoutDiaglog(){
		new CustomAlterDialog.Builder(mActivity).setMsg(ContextUtil.getString(R.string.logout_sure))
				.setCancelButton(ContextUtil.getString(R.string.cancel), null)
				.setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {
					@Override
					public void onDialogClick(View v, CustomAlterDialog dialog) {
						// logout();
						UserInfoManager.getInstance().cleanUserInfo();
						finish();
					}
				}).build().show();
	}

	/** 退出登录 */
	public void logout() {
		showProgressDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", UserInfoManager.getInstance().getUserId());
		HttpManager.getInstance().post(HttpURL.URL1 + HttpURL.LOGOUT, map,
				new RequestCallBack<String>() {
					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// TODO Auto-generated method stub
						dismissProgressDialog();
						ResponseJsonInfo info = JsonUtil.fromJson(arg0.result,
								UserInfo.class);
						if (info != null) {
							if (info.getHttpCode().equals(HttpCode.SUCESS)) {
								UserInfoManager.getInstance().cleanUserInfo();
								finish();
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

	/** 若没有登录，则跳到登录页面 */
	public void toAct(Class clazz) {
		if (!UserInfoManager.getInstance().isLogin()
				|| UserInfoManager.getInstance().getUserInfo() == null) {
			toActivity(LoginActivity.class);
		} else {
			toActivity(clazz);
		}
	}
}
