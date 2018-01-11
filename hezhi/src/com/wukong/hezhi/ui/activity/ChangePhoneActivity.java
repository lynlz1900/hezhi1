package com.wukong.hezhi.ui.activity;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.utils.ContextUtil;

/**
 * 
 * @ClassName: ChangePhoneActivity
 * @Description: TODO(更换手机号码)
 * @author HuZhiyin
 * @date 2017-7-5 下午3:23:22
 * 
 */
public class ChangePhoneActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.phone);
	public String pageCode ="phone";
	
	@ViewInject(R.id.phone_tv)
	private TextView phone_tv;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_change_phone;
	}

	@Override
	protected void init() {
		headView.setTitleStr(ContextUtil.getString(R.string.phone));

	}

	@Override
	@SuppressLint("NewApi")
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		phone_tv.setText(UserInfoManager.getInstance().getPhone());
	}

	@OnClick(value = { R.id.change_phone_bt })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.change_phone_bt:
			toActivity(ChangePhoneActivity2.class);
			break;
		}
	}

}
