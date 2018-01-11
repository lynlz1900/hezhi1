package com.wukong.hezhi.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.SPUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 
 * @ClassName: DebugModelActivity
 * @Description: TODO(隐藏功能)
 * @author HuZhiyin
 * @date 2016-11-3 下午4:21:32
 * 
 */
public class DebugModelActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.godhide_model);
	public String pageCode = "godhide_model";
	@ViewInject(R.id.url_tv)
	private EditText url_tv;

	@ViewInject(R.id.port_tv)
	private EditText port_tv;

	@ViewInject(R.id.url_tv1)
	private EditText url_tv1;

	@ViewInject(R.id.port_tv1)
	private EditText port_tv1;

	@ViewInject(R.id.device_id)
	private EditText device_id;

	@ViewInject(R.id.desteny_id)
	private EditText desteny_id;

	@ViewInject(R.id.user_id)
	private EditText user_id;

	public void saveConfig() {
		/** 导航 */
		saveNavigation();
		/** 传媒 */
		saveMedia();

		ScreenUtil.showToast(ContextUtil.getString(R.string.save_sucess));
		finish();
	}

	/** 保存导航 */
	private void saveNavigation() {
		String url = url_tv.getText().toString().trim();
		String port = port_tv.getText().toString().trim();
		String service_url = "https://" + url + ":" + port;
		SPUtil.savaToShared(DebugModelActivity.this, HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.URL, url);
		SPUtil.savaToShared(DebugModelActivity.this, HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.PORT, port);
		SPUtil.savaToShared(DebugModelActivity.this, HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.SERVICE_URL,
				service_url);
	}

	/** 保存传媒 */
	private void saveMedia() {
		String url1 = url_tv1.getText().toString().trim();
		String port1 = port_tv1.getText().toString().trim();
		String service_url1 = "https://" + url1 + ":" + port1;
		SPUtil.savaToShared(DebugModelActivity.this, HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.URL1, url1);
		SPUtil.savaToShared(DebugModelActivity.this, HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.PORT1, port1);
		SPUtil.savaToShared(DebugModelActivity.this, HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.SERVICE_URL1,
				service_url1);
	}

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_godhide_model;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		device_id.setText(PackageUtil.getPhoneId());
		desteny_id.setText(ScreenUtil.getScreenHeight() + "*" + ScreenUtil.getScreenWidth());
		user_id.setText(UserInfoManager.getInstance().getUserId() + "");
		headView.setTitleStr(ContextUtil.getString(R.string.godhide_model));
		headView.setRightTitleText(ContextUtil.getString(R.string.save));
		String oldUlr = SPUtil.getShareStr(this, HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.URL);
		String oldPort = SPUtil.getShareStr(this, HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.PORT);
		if (!TextUtils.isEmpty(oldUlr)) {
			url_tv.setText(oldUlr);
		}
		if (!TextUtils.isEmpty(oldPort)) {
			port_tv.setText(oldPort);
		}

		String oldUlr1 = SPUtil.getShareStr(this, HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.URL1);
		String oldPort1 = SPUtil.getShareStr(this, HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.PORT1);
		if (!TextUtils.isEmpty(oldUlr1)) {
			url_tv1.setText(oldUlr1);
		}
		if (!TextUtils.isEmpty(oldPort1)) {
			port_tv1.setText(oldPort1);
		}

		headView.setRightLis(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				saveConfig();
			}
		});
	}

}
