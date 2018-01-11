package com.wukong.hezhi.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.WalletInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: MyWalletActivity
 * @Description: TODO(我的钱包页面)
 * @author HuZhiyin
 * @date 2017-3-31 下午2:45:50
 * 
 */
public class MyWalletActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.my_wallet);
	public String pageCode ="my_wallet";
	@ViewInject(R.id.getcrash_bt)
	private Button getcrash_bt;

	@ViewInject(R.id.balance_tv)
	private TextView balance_tv;

	/** 钱包 */
	private WalletInfo wallet = new WalletInfo();
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_my_wallet;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.wallet));
		headView.setRightTitleText(ContextUtil
				.getString(R.string.change_detail));
	}

	@Override
	@SuppressLint("NewApi")
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getData();
	}

	/** 余额 */
	public void getData() {
		showProgressDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("memberId", UserInfoManager.getInstance().getUserId());
		String URL = HttpURL.URL1 + HttpURL.BALANCE;
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result,
						WalletInfo.class);
				if (info != null) {
					if (info.getHttpCode().equals(HttpCode.SUCESS)) {
						wallet = (WalletInfo) info.getBody();
						balance_tv.setText(wallet.getAvailableAmount() + "");
					} else if (info.getHttpCode().equals(HttpCode.FAIL)) {
						// ScreenUtil.showToast(info.getPromptMessage());
					}
				}

			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				// ScreenUtil.showToast(arg1);
			}
		});
	}

	@OnClick(value = { R.id.getcrash_bt, R.id.header_right })
	@Override
	public void onClick(View arg0) {
		super.onClick(arg0);
		switch (arg0.getId()) {
		case R.id.getcrash_bt:
			if (wallet.getAvailableAmount() >= 1) {
				toActivity(GetCrashActivity.class, Constant.Extra.WALLET,
						wallet);
			} else {
				ScreenUtil.showToast("余额必须1元及以上才能提现");
			}

			break;
		case R.id.header_right:
			toActivity(CashDetailActivity.class);
			break;
		}
	}
}
