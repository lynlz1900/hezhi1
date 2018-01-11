package com.wukong.hezhi.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.ui.activity.BaseApplication;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/***
 * 
 * @ClassName: WXLoginActivity
 * @Description: TODO(微信登录)
 * @author HuZhiyin
 * @date 2017-8-9 上午10:30:00
 * 
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.d("onCreate回调");
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
		LogUtil.d("onNewIntent回调");
	}

	private void handleIntent(Intent paramIntent) {
		BaseApplication.wxApi.handleIntent(paramIntent, this);
	}

	@Override
	public void onReq(BaseReq arg0) {
		LogUtil.d("onReq回调");
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		LogUtil.d("onResp回调");
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			LogUtil.d("登录成功");
			try {
				String code = ((SendAuth.Resp) resp).code;
				getAceessToken(code);
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			LogUtil.d("登录取消");
			callBack.fail();
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			LogUtil.d("登录拒绝");
			callBack.fail();
			break;
		default:
			callBack.fail();
			break;
		}
		finish();
	}

	/** 通过code获取AceessToken */
	public void getAceessToken(String code) {
		params = "";
		addParams("appid", WXConstant.APP_ID);
		addParams("secret", WXConstant.APP_SECRET);
		addParams("code", code);
		addParams("grant_type", "authorization_code");
		String url = WXConstant.TOKEN + params;
		HttpManager.getInstance().get(url, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String jsonData = responseInfo.result;
				WXResponseInfo wxResponseInfo = (WXResponseInfo) JsonUtil
						.parseJson2Obj(jsonData, WXResponseInfo.class);
				if (wxResponseInfo != null) {
					getUserInfo(wxResponseInfo);
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				callBack.fail();
				ScreenUtil.showToast(msg);
			}
		});
	}

	/**
	 * 获取微信用户信息
	 */
	public void getUserInfo(WXResponseInfo wxResponseInfo) {
		params = "";
		addParams("access_token", wxResponseInfo.getAccess_token());
		addParams("openid", wxResponseInfo.getOpenid());
		addParams("lang", "zh_CN");
		String url = WXConstant.USERINFO + params;
		HttpManager.getInstance().get(url, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String jsonData = responseInfo.result;
				WXUserInfo wxUserInfo = (WXUserInfo) JsonUtil.parseJson2Obj(
						jsonData, WXUserInfo.class);
				if (wxUserInfo != null
						&& !TextUtils.isEmpty(wxUserInfo.getUnionid())) {
					callBack.sucess(wxUserInfo);
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				callBack.fail();
				ScreenUtil.showToast(msg);
			}
		});
	}

	/**
	 * 微信登录
	 */
	public static void login(ILoginCallBack mWXCallBack) {
		if (!PackageUtil.isWeixinAvilible(ContextUtil.getContext())) {
			ScreenUtil.showToast("微信未安装");
			return;
		}
		if (mWXCallBack != null) {
			callBack = mWXCallBack;
		}

		final SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "carjob_wx_login";
		BaseApplication.wxApi.sendReq(req);
	}

	private static String params = "";

	public void addParams(String key, String value) {
		if (TextUtils.isEmpty(params)) {
			params = key + "=" + value;
		}
		params = params + "&" + key + "=" + value;
	}

	private static ILoginCallBack callBack = new ILoginCallBack() {

		@Override
		public void sucess(WXUserInfo wxUserInfo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void fail() {
			// TODO Auto-generated method stub

		}
	};

	/** 微信的登录回调接口 */
	public interface ILoginCallBack {
		void sucess(WXUserInfo wxUserInfo);

		void fail();
	}

}
