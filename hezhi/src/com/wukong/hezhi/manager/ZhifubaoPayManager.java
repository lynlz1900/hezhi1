package com.wukong.hezhi.manager;

import java.util.Map;
import com.alipay.sdk.app.PayTask;
import com.wukong.hezhi.bean.PayResult;
import com.wukong.hezhi.utils.LogUtil;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

/***
 * 
 * @ClassName: ZhifubaoPayManager
 * @Description: TODO(支付宝支付管理类)
 * @author HuZhiyin
 * @date 2017-8-9 上午11:45:40
 * 
 */
public class ZhifubaoPayManager {

	private static final int SDK_PAY_FLAG = 1;// 支付

	private ZhifubaoPayManager() {
	}

	private static class Holder {
		private static final ZhifubaoPayManager SINGLETON = new ZhifubaoPayManager();
	}

	public static ZhifubaoPayManager getInstance() {
		return Holder.SINGLETON;
	}

	/** 支付 */
	public void pay(final String orderInfo, IZhiFuBaoCallBack zhiFuBaoCallBack) {
		if (zhiFuBaoCallBack != null) {
			this.zhiFuBaoCallBack = zhiFuBaoCallBack;
		}

		if (TextUtils.isEmpty(orderInfo)) {
			zhiFuBaoCallBack.fail();
			return;
		}

//		if (!PackageUtil.isZhifubaoAvailable(ContextUtil.getContext())) {
//			ScreenUtil.showToast("支付宝未安装");
//			zhiFuBaoCallBack.fail();
//			return;
//		}

		final Activity activity = ActivitiesManager.getInstance().currentActivity();
		Runnable payRunnable = new Runnable() {
			@Override
			public void run() {
				PayTask alipay = new PayTask(activity);
				Map<String, String> result = alipay.payV2(orderInfo, true);
				LogUtil.i(result.toString());
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG:
				@SuppressWarnings("unchecked")
				PayResult payResult = new PayResult((Map<String, String>) msg.obj);
				/**
				 * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				String resultStatus = payResult.getResultStatus();
				if (TextUtils.equals(resultStatus, "9000")) {// 为9000则代表支付成功
					zhiFuBaoCallBack.sucess();
				} else {// 该笔订单真实的支付结果，需要依赖服务端的异步通知。
					zhiFuBaoCallBack.fail();
				}
				break;
			}
		}
	};

	/** 支付宝支付的回调接口 */
	public interface IZhiFuBaoCallBack {
		void sucess();

		void fail();
	}

	private IZhiFuBaoCallBack zhiFuBaoCallBack = new IZhiFuBaoCallBack() {
		@Override
		public void sucess() {
		}

		@Override
		public void fail() {
		}
	};

}
