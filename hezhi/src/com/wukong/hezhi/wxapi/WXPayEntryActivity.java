package com.wukong.hezhi.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.wukong.hezhi.ui.activity.BaseActivity;
import com.wukong.hezhi.ui.activity.BaseApplication;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 
 * @ClassName: WXPayActivity
 * @Description: TODO(微信支付)
 * @author HuZhiyin
 * @date 2017-8-9 上午10:30:58
 * 
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handleIntent(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		handleIntent(intent);
	}

	private void handleIntent(Intent paramIntent) {
		BaseApplication.wxApi.handleIntent(paramIntent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
		case 0:// 成功
			mPayCallBack.sucess();
			break;
		case -1:// 错误， 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等
			mPayCallBack.fail();
			break;
		case -2:// 用户取消 无需处理。发生场景：用户不支付了，点击取消，返回APP。
			mPayCallBack.fail();
			break;
		}
		finish();
	}

	/** 微信付款 */
	public static void pay(WXPayInfo wxPayInfo, IWXPayCallBack payCallBack) {
		if (payCallBack != null) {
			mPayCallBack = payCallBack;
		}
		if (wxPayInfo == null) {
			mPayCallBack.fail();
			return;
		}
		if (!PackageUtil.isWeixinAvilible(ContextUtil.getContext())) {
			ScreenUtil.showToast("微信未安装");
			mPayCallBack.fail();
			return;
		}
		// PayReq request = new PayReq();
		// request.appId = wxPayInfo.getAppId();// 微信开放平台审核通过的应用APPID
		// request.partnerId = wxPayInfo.getPartnerId();// 微信支付分配的商户号
		// request.prepayId = wxPayInfo.getPrepayId();// 微信返回的支付交易会话ID
		// request.packageValue = wxPayInfo.getPackageValue();//
		// 暂填写固定值Sign=WXPay
		// request.nonceStr = wxPayInfo.getNonceStr();// 随机字符串，不长于32位。推荐随机数生成算法
		// request.timeStamp = wxPayInfo.getTimeStamp();// 时间戳，请见接口规则-参数规定
		// request.sign = wxPayInfo.getSign();// 签名，详见签名生成算法
		PayReq request = (PayReq) JsonUtil.Obj2Obj(wxPayInfo, PayReq.class);
		BaseApplication.wxApi.sendReq(request);
	}

	/** 微信支付的回调接口 */
	public interface IWXPayCallBack {
		void sucess();

		void fail();
	}

	private static IWXPayCallBack mPayCallBack = new IWXPayCallBack() {
		@Override
		public void sucess() {
		}

		@Override
		public void fail() {
		}
	};

}
