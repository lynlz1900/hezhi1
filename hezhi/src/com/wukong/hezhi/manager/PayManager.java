package com.wukong.hezhi.manager;

import java.util.HashMap;
import java.util.Map;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.OrderZhifubaoInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.ZhifubaoPayManager.IZhiFuBaoCallBack;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.wxapi.WXPayEntryActivity;
import com.wukong.hezhi.wxapi.WXPayEntryActivity.IWXPayCallBack;
import com.wukong.hezhi.wxapi.WXPayInfo;

import android.app.Activity;
import android.text.TextUtils;

/***
 * 
 * @ClassName: PayManager
 * @Description: TODO(支付管理类)
 * @author HuZhiyin
 * @date 2017-8-11 上午8:28:36
 * 
 */
public class PayManager {
	@SuppressWarnings("unused")
	private Activity mActivity;
	/** 微信支付 */
	public static final int PAY_WX = 1;
	/** 支付宝支付 */
	public static final int PAY_ZHIFUBAO = 2;
	/** 订单号 */
	private String orderNum;

	private PayManager() {
	}

	private static class Holder {
		private static final PayManager SINGLETON = new PayManager();
	}

	public static PayManager getInstance() {
		return Holder.SINGLETON;
	}

	/**
	 * 
	 * @Title: pay @Description: TODO(支付) @param @param mActivity
	 *         上下文 @param @param payWay 支付方式 @param @param orderNum
	 *         订单号 @param @param payCallBack 回调接口 @return void 返回类型 @throws
	 */
	public void pay(Activity mActivity, int payWay, String orderNum, IPayCallBack payCallBack) {
		this.mActivity = mActivity;
		this.orderNum = orderNum;
		if (payCallBack != null) {
			this.payCallBack = payCallBack;
		}
		if (TextUtils.isEmpty(orderNum)) {
			return;
		}
		switch (payWay) {
		case PAY_WX:
			placeOrderWX();//微信下单
			break;
		case PAY_ZHIFUBAO:
			placeOrderZhifubao();//支付宝下单
			break;
		}
	}

	/** 微信下单 */
	private void placeOrderWX() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderNo", orderNum);
		map.put("payType", "WEIXINPAY");

		String URL = HttpURL.URL1 + HttpURL.PAY_WX;
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {

			@SuppressWarnings("rawtypes")
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result, WXPayInfo.class);
				if (info != null && HttpCode.SUCESS.equals(info.getHttpCode())) {
					WXPayInfo orderInfo = (WXPayInfo) info.getBody();
					payWX(orderInfo);//调起微信支付
				} else if (info != null && HttpCode.FAIL.equals(info.getHttpCode())) {
					ScreenUtil.showToast(info.getPromptMessage());
					payCallBack.fail(ContextUtil.getString(R.string.fail_order));
				} else {
					payCallBack.fail(ContextUtil.getString(R.string.fail_order));
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				payCallBack.fail(ContextUtil.getString(R.string.fail_order));
			}
		});
	}

	/** 支付宝下单 */
	private void placeOrderZhifubao() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderNo", orderNum);
		map.put("payType", "ALIPAY");
		String URL = HttpURL.URL1 + HttpURL.PAY_ALI;
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@SuppressWarnings("rawtypes")
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				LogUtil.i(responseInfo.result);
				ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result, OrderZhifubaoInfo.class);
				if (info != null && HttpCode.SUCESS.equals(info.getHttpCode())) {
					OrderZhifubaoInfo orderInfo = (OrderZhifubaoInfo) info.getBody();
					String orderInfoStr = orderInfo.getOrderInfo();
					payZhifubao(orderInfoStr);//调起支付宝支付
				} else if (info != null && HttpCode.FAIL.equals(info.getHttpCode())) {
					ScreenUtil.showToast(info.getPromptMessage());
					payCallBack.fail(ContextUtil.getString(R.string.fail_order));
				} else {
					payCallBack.fail(ContextUtil.getString(R.string.fail_order));
				}
			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				payCallBack.fail(ContextUtil.getString(R.string.fail_order));
			}
		});
	}

	/** 微信支付 */
	private void payWX(WXPayInfo wxPayInfo) {
		WXPayEntryActivity.pay(wxPayInfo, new IWXPayCallBack() {

			@Override
			public void sucess() {
				// TODO Auto-generated method stub
				payCallBack.sucess(ContextUtil.getString(R.string.sucess_pay));
			}

			@Override
			public void fail() {
				// TODO Auto-generated method stub
				payCallBack.fail(ContextUtil.getString(R.string.fail_pay));
			}
		});
	}

	/** 支付宝支付 */
	private void payZhifubao(String orderInfo) {
		ZhifubaoPayManager.getInstance().pay(orderInfo, new IZhiFuBaoCallBack() {

			@Override
			public void sucess() {
				// TODO Auto-generated method stub
				payCallBack.sucess(ContextUtil.getString(R.string.sucess_pay));
			}

			@Override
			public void fail() {
				// TODO Auto-generated method stub
				payCallBack.fail(ContextUtil.getString(R.string.fail_pay));
			}
		});
	}

	/** 回调接口 */
	public interface IPayCallBack {
		void sucess(String sucess);

		void fail(String fail);
	}

	private IPayCallBack payCallBack = new IPayCallBack() {
		@Override
		public void sucess(String sucess) {
		}

		@Override
		public void fail(String fail) {
		}
	};
}
