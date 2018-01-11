package com.wukong.hezhi.ui.activity;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.RedBagInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.UserInfo;
import com.wukong.hezhi.bean.WalletInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.wxapi.WXEntryActivity;
import com.wukong.hezhi.wxapi.WXEntryActivity.ILoginCallBack;
import com.wukong.hezhi.wxapi.WXUserInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @ClassName: GetCrashActivity
 * @Description: TODO(提现界面)
 * @author HuZhiyin
 * @date 2017-3-31 下午3:09:53
 * 
 */
public class GetCrashActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.get_crash);
	public String pageCode = "get_crash";

	@ViewInject(R.id.getcrash_bt)
	private Button getcrash_bt;

	@ViewInject(R.id.balance_tv)
	private TextView balance_tv;

	@ViewInject(R.id.get_allcash_tv)
	private TextView get_allcash_tv;

	@ViewInject(R.id.getcash_et)
	private EditText getcash_et;

	private float cashMoney;

	// private UserInfo userInfo = UserInfoManager.getInstance().getUserInfo();

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_get_crash;
	}

	WalletInfo wallet = new WalletInfo();

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.get_crash));
		// wallet = (Wallet) getIntent().getSerializableExtra(
		// Constant.Extra.WALLET);
		// balance_tv.setText(wallet.getAvailableAmount() + "");
		getcash_et.addTextChangedListener(watcher);
	}

	private TextWatcher watcher = new TextWatcher() {

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
			getcash_et.setSelection(getcash_et.getText().length());

		}
	};

	public void getData() {
		showProgressDialog();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cashMoney", cashMoney);
		map.put("memberId", UserInfoManager.getInstance().getUserId());
		String URL = HttpURL.URL1 + HttpURL.GET_CASH;
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result, RedBagInfo.class);
				if (info != null) {
					if (info.getHttpCode().equals(HttpCode.SUCESS)) {
						RedBagInfo redBagInfo = (RedBagInfo) info.getBody();
						toHandle(redBagInfo);
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

	/** 做相应的处理，提示成功， resultCode：0成功 1 没有绑定微信 resultCode：2 没有关注盒知公众号 200的时候 提现 */
	public void toHandle(RedBagInfo redBagInfo) {
		switch (redBagInfo.getResultCode()) {
		case 0:// 成功
			getBalanceData();// 提现成功，刷新余额
			showSucessDialog("你的提款已由“盒知”公众号发送至你的微信，请在微信中查看");
			break;
		case 1:// 没有绑定微信
			showBindDialog("绑定微信才能提现，是否绑定微信？");
			break;
		case 2:// 没有关注盒知公众号
			toActivity(TipActivity.class, Constant.Extra.MONEY,cashMoney);
			break;
		}
	}

	/** 余额 */
	public void getBalanceData() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("memberId", UserInfoManager.getInstance().getUserId());
		String URL = HttpURL.URL1 + HttpURL.BALANCE;
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result, WalletInfo.class);
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
				// ScreenUtil.showToast(arg1);
			}
		});
	}

	@Override
	@SuppressLint("NewApi")
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getBalanceData();
	}

	/**
	 * 绑定
	 */
	protected void showBindDialog(String str) {
		new CustomAlterDialog.Builder(mActivity).setTitle(ContextUtil.getString(R.string.wenxin_tip)).setMsg(str)
				.setCancelButton(ContextUtil.getString(R.string.cancel), null)
				.setSureButton(ContextUtil.getString(R.string.sure), new BindListener()).build().show();

	}

	/**
	 * 提款成功提示
	 */
	protected void showSucessDialog(String str) {
		new CustomAlterDialog.Builder(mActivity).setTitle(ContextUtil.getString(R.string.get_cash_sucess)).setMsg(str)
				.setSureButton(ContextUtil.getString(R.string.i_know), new OnDialogClickListener() {
					@Override
					public void onDialogClick(View v, CustomAlterDialog dialog) {
						// TODO Auto-generated method stub
						finish();
					}
				}).build().show();
	}

	/** 绑定监听 */
	private class BindListener implements OnDialogClickListener {

		@Override
		public void onDialogClick(View v, CustomAlterDialog dialog) {
			// TODO Auto-generated method stub
			WXEntryActivity.login(new WXCallBack());// 微信
		}

	}

	/** 微信回调 */
	public class WXCallBack implements ILoginCallBack {

		@Override
		public void sucess(WXUserInfo wxUserInfo) {
			// TODO Auto-generated method stub
			if (wxUserInfo == null) {
				return;
			}

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", UserInfoManager.getInstance().getUserId());
			map.put("showImageURL", wxUserInfo.getHeadimgurl());
			map.put("phone", UserInfoManager.getInstance().getPhone());
			map.put("wxOpenId", wxUserInfo.getUnionid());
			String URL = HttpURL.URL1 + HttpURL.BIND_WECHAT;
			HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

				@Override
				public void onSuccess(ResponseInfo<String> arg0) {
					// TODO Auto-generated method stub
					dismissProgressDialog();
					ResponseJsonInfo info = JsonUtil.fromJson(arg0.result, UserInfo.class);
					if (info != null) {
						ScreenUtil.showToast(info.getPromptMessage());
						if (info.getHttpCode().equals(HttpCode.SUCESS)) {
							UserInfoManager.USERINFO_CHANGE = true;// 刷新个人中心页面
						} else if (info.getHttpCode().equals(HttpCode.FAIL)) {

						}
					}
				}

				@Override
				public void onFailure(HttpException arg0, String arg1) {
					// TODO Auto-generated method stub
					ScreenUtil.showToast(arg1);
					dismissProgressDialog();
				}
			});
		}

		@Override
		public void fail() {
			// TODO Auto-generated method stub

		}

	}

	@OnClick(value = { R.id.getcrash_bt, R.id.get_allcash_tv })
	@Override
	public void onClick(View arg0) {
		super.onClick(arg0);
		switch (arg0.getId()) {
		case R.id.getcrash_bt:
			String cashStr = getcash_et.getText().toString().trim();
			if (TextUtils.isEmpty(cashStr)) {
				ScreenUtil.showToast("请输入提现金额");
				return;
			}
			if (wallet == null) {
				ScreenUtil.showToast("余额为空，不能提现");
				return;
			}
			float balance = wallet.getAvailableAmount();
			cashMoney = Float.parseFloat(cashStr);

			if (cashMoney > 200) {
				ScreenUtil.showToast("每次提现金额不能高于200元");
				return;
			}

			if (cashMoney < 1) {
				ScreenUtil.showToast("每次提现金额不能低于1元");
				return;
			}
			if (cashMoney > balance) {// 提现大于余额
				ScreenUtil.showToast("提现金额不能大于余额");
				return;
			} else {
				getData();
			}
			break;
		case R.id.get_allcash_tv:
			getcash_et.setText(wallet.getAvailableAmount() + "");
			break;
		}
	}
}
