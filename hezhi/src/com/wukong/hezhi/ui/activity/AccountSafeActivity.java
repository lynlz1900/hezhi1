package com.wukong.hezhi.ui.activity;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.UserInfo;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 
 * @ClassName: AccountSafeActivity
 * @Description: TODO(账户安全页面)
 * @author HuZhiyin
 * @date 2017-1-10 下午3:01:22
 * 
 */
public class AccountSafeActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.account_safe);
	public String pageCode ="account_safe";
	@ViewInject(R.id.pay_ll)
	private LinearLayout pay_ll;

	@ViewInject(R.id.phone_ll)
	private LinearLayout phone_ll;

	@ViewInject(R.id.socialcontact_ll)
	private Button socialcontact_ll;

	@ViewInject(R.id.bind_phone_tv)
	private TextView bind_phone_tv;

	@OnClick(value = { R.id.pay_ll, R.id.phone_ll, R.id.socialcontact_ll })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.pay_ll:
			// toActivity(SetPayPswActivity.class);
			ScreenUtil.showToast("敬请期待");
			break;
		case R.id.phone_ll:
			toActivity(ChangePhoneActivity.class);
			break;
		case R.id.socialcontact_ll:

			userInfo = UserInfoManager.getInstance().getUserInfo();
			if (userInfo == null) {
				return;
			}
			String phone = userInfo.getPhone();
			if (!TextUtils.isEmpty(phone)) {
				toActivity(BindSocialActivity.class);
			} else {
				ScreenUtil.showToast("请先绑定手机");
			}

			break;
		}
	}

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_accoutn_safe;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.account_safe));
	}

	private UserInfo userInfo;

	@Override
	@SuppressLint("NewApi")
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshView();
	}

	private void refreshView() {
		userInfo = UserInfoManager.getInstance().getUserInfo();
		if (userInfo == null) {
			return;
		}
		String phone = userInfo.getPhone();
		if (!TextUtils.isEmpty(phone)) {
			bind_phone_tv.setText("更换手机号");
//			phone_ll.setClickable(false);
		} else {
			bind_phone_tv.setText("请绑定手机");
		}
	}

}
