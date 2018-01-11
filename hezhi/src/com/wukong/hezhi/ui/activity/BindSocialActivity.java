package com.wukong.hezhi.ui.activity;

import java.util.HashMap;
import java.util.Map;

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
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.wxapi.WXEntryActivity;
import com.wukong.hezhi.wxapi.WXEntryActivity.ILoginCallBack;
import com.wukong.hezhi.wxapi.WXUserInfo;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @ClassName: BindSocialActivity
 * @Description: TODO(社交号绑定)
 * @author HuZhiyin
 * @date 2017-1-11 上午10:49:55
 * 
 */
public class BindSocialActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.bind_social);
	public String pageCode = "bind_social";

	@ViewInject(R.id.wechat_ll)
	private LinearLayout wechat_ll;

	@ViewInject(R.id.qq_ll)
	private LinearLayout qq_ll;

	@ViewInject(R.id.bind_wechat_tv)
	private TextView bind_wechat_tv;

	@ViewInject(R.id.bind_qq_tv)
	private TextView bind_qq_tv;

	private int acctountType;// 社交类型(0,微信，1,qq，2,微博)
	private boolean isBindWechat = false;// 绑定微信
	private boolean isBindQQ = false;// 绑定qq
	private boolean isBindWeibo = false;// 绑定微博
	private UserInfo userInfo;// 用户信息,通过userInfo里的wxOpenId，wbOpenId，qqOpenId判断是否绑定
	private static final int REQUESTCODE_QQ = 1;
	private String id;
	private String phone;
	private String showImageURL;
	private String openId;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_bind_social;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.bind_social));
		getUserInfo();
	}

	/** 获取用户信息 */
	public void getUserInfo() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", UserInfoManager.getInstance().getUserId());
		HttpManager.getInstance().post(HttpURL.URL1 + HttpURL.GET_USERINFO, map, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				ResponseJsonInfo info = JsonUtil.fromJson(arg0.result, UserInfo.class);
				if (info != null) {
					if (info.getHttpCode().equals(HttpCode.SUCESS)) {
						userInfo = (UserInfo) info.getBody();
						refreshView();
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

	/** 刷新界面 */
	private void refreshView() {
		if (userInfo == null) {
			return;
		}
		String wechatId = userInfo.getWxOpenId();
		String qqId = userInfo.getQqOpenId();
		String weiboId = userInfo.getWbOpenId();
		if (!TextUtils.isEmpty(wechatId)) {
			bind_wechat_tv.setText("已绑定");
			isBindWechat = true;
		} else {
			bind_wechat_tv.setText("未绑定");
			isBindWechat = false;
		}
		if (!TextUtils.isEmpty(qqId)) {
			bind_qq_tv.setText("已绑定");
			isBindQQ = true;
		} else {
			bind_qq_tv.setText("未绑定");
			isBindQQ = false;
		}
	}

	@OnClick(value = { R.id.wechat_ll, R.id.qq_ll })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.wechat_ll:
			acctountType = 0;
			if (!isBindWechat) {
				showBindDialog("绑定微信");
			} else {
				showBindDialog("解绑微信");
			}
			break;
		case R.id.qq_ll:
			acctountType = 1;
			if (!isBindQQ) {
				showBindDialog("绑定QQ");
			} else {
				showBindDialog("解绑QQ");
			}
			break;
		}
	}

	/**
	 * 绑定or解绑账号
	 */
	protected void showBindDialog(String str) {
		new CustomAlterDialog.Builder(mActivity).setMsg(str)
				.setCancelButton(ContextUtil.getString(R.string.cancel), null)
				.setSureButton(ContextUtil.getString(R.string.sure), new BindorUbindListener()).build().show();

	}

	/** 绑定or解绑监听 */
	private class BindorUbindListener implements OnDialogClickListener {

		@Override
		public void onDialogClick(View v, CustomAlterDialog dialog) {
			// TODO Auto-generated method stub
			switch (acctountType) {// 社交类型(0,微信，1,qq，2,微博)
			case 0:
				WXEntryActivity.login(new WXCallBack());// 微信
				break;
			case 1:
				Intent qqIntent = new Intent(BindSocialActivity.this, QQActivity.class);
				startActivityForResult(qqIntent, REQUESTCODE_QQ);
				break;
			case 2:

				break;
			}
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
			id = userInfo.getId() + "";
			phone = userInfo.getPhone();
			showImageURL = wxUserInfo.getHeadimgurl();
			openId = wxUserInfo.getUnionid();
			bindThirdAcount(id, phone, openId, showImageURL);
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
				if (qqUserInfo == null) {
					return;
				}
				id = userInfo.getId() + "";
				phone = userInfo.getPhone();
				showImageURL = qqUserInfo.getFigureurl_qq_2();
				openId = qqUserInfo.getOpenId();
				bindThirdAcount(id, phone, openId, showImageURL);
				break;
			default:
				break;
			}
		}
	}

	/*** 绑定or解绑第三方账号 */
	public void bindThirdAcount(String id, String phone, String openId, String showImageURL) {
		showProgressDialog();
		String acctountOpenIdKey = "";
		String URL = "";
		switch (acctountType) {// 社交类型(0,微信，1,qq，2,微博)
		case 0:
			acctountOpenIdKey = "wxOpenId";
			URL = HttpURL.URL1 + HttpURL.BIND_WECHAT;
			break;
		case 1:
			acctountOpenIdKey = "qqOpenId";
			URL = HttpURL.URL1 + HttpURL.BIND_QQ;
			break;
		case 2:
			acctountOpenIdKey = "wbOpenId";
			URL = HttpURL.URL1 + HttpURL.BIND_WEIBO;
			break;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("showImageURL", showImageURL);
		map.put("phone", phone);
		map.put(acctountOpenIdKey, openId);
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				ResponseJsonInfo info = JsonUtil.fromJson(arg0.result, UserInfo.class);

				if (info != null) {
					ScreenUtil.showToast(info.getPromptMessage());
					if (info.getHttpCode().equals(HttpCode.SUCESS)) {
						userInfo = (UserInfo) info.getBody();
						UserInfoManager.USERINFO_CHANGE = true;// 刷新个人中心页面
						refreshView();
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

}
