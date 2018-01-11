package com.wukong.hezhi.manager;

import java.util.HashMap;
import java.util.Map;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.OrderInfo;
import com.wukong.hezhi.bean.RedBagInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.ui.activity.LoginActivity;
import com.wukong.hezhi.ui.activity.OrderCustDetailActivity;
import com.wukong.hezhi.ui.activity.SearchOrderCustActivity;
import com.wukong.hezhi.ui.activity.RedBagActivity;
import com.wukong.hezhi.ui.fragment.OrderCustFragment;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.ui.view.CustomAlterProgressDialog;
import com.wukong.hezhi.ui.view.EmptyRedBagDialog;
import com.wukong.hezhi.ui.view.RedBagDialog;
import com.wukong.hezhi.ui.view.RedBagWindows;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.ThreadUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;

/**
 * 
 * @ClassName: RedBagOrderManager
 * @Description: TODO(订单红包管理类)
 * @author HuangFeiFei
 * @date 2017-8-17 下午16:18:22
 * 
 */
public class RedBagOrderManager {
	private static Activity mActivity;
	private static final int LOGIN = 0;// 登录
	private RedBagWindows bugWindows;
	private OrderInfo mOrderInfo;
	
	private int REDBAG_DELAY_TIME = 000;// 红包延迟弹出时间，默认5秒

	private RedBagOrderManager() {
	}

	private static class Holder {
		private static final RedBagOrderManager SINGLETON = new RedBagOrderManager();
	}

	/**
	 * 单一实例
	 * */
	public static RedBagOrderManager getInstance(Activity activity) {
		mActivity = activity;
		return Holder.SINGLETON;
	}

	/** 检查是否有红包 */
	@SuppressWarnings("rawtypes")
	public void checkRedBag(OrderInfo orderInfo) {
		if(orderInfo == null)
			return;

		mOrderInfo = orderInfo;
		
		bugWindows = RedBagWindows.getInstance();
		
		String URL = HttpURL.URL1 + HttpURL.ORDER_REDBAG_CHECK
				+"?orderNo="+orderInfo.getOrderNumber();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderNo",  orderInfo.getOrderNumber());
		LogUtil.i(URL);
		LogUtil.i(map.toString());
		HttpManager.getInstance().get(URL, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				LogUtil.i(arg0.result);
				ResponseJsonInfo info = JsonUtil.fromJson(arg0.result,
						RedBagInfo.class);
				if (info != null) {
					if (info.getHttpCode().equals(HttpCode.SUCESS)) {
						RedBagInfo redBagInfo = (RedBagInfo) info.getBody();
						showRedBagWindow(redBagInfo);
//						checkHasReceived(redBagInfo);// 检查是否领取过
					} else if (info.getHttpCode().equals(HttpCode.FAIL)) {
						if(info.getPromptMessage() != null 
								|| info.getPromptMessage().length() >0)
							ScreenUtil.showToast(info.getPromptMessage());
					}
				}
			}
		});
	}

	/** 检查红包是否领取过 */
	public void checkHasReceived(final RedBagInfo redBagInfo) {

		if (UserInfoManager.getInstance().isLogin()) {// 是否是手机登录
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("orderNo", mOrderInfo.getOrderNumber());
			map.put("memberId", UserInfoManager.getInstance().getUserId());
			map.put("deviceId", PackageUtil.getPhoneId());
			String URL = HttpURL.URL1 + HttpURL.CHECK_RECEIVED_GANZ;
			HttpManager.getInstance().post(URL, map,
					new RequestCallBack<String>() {
						@SuppressWarnings("rawtypes")
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							ResponseJsonInfo info = JsonUtil.fromJson(
									responseInfo.result, RedBagInfo.class);
							if (info != null) {
								if (info.getHttpCode().equals(HttpCode.SUCESS)) {
									RedBagInfo info2 = (RedBagInfo) info
											.getBody();
									if (!info2.isResult()) {// 如果没有领取过，则弹出红包领取toast
										showRedBagWindow(redBagInfo);
									}
								} else if (info.getHttpCode().equals(HttpCode.FAIL)) {
									if(info.getPromptMessage() != null 
											|| info.getPromptMessage().length() >0)
										ScreenUtil.showToast(info.getPromptMessage());
								}
							}
						}

						@Override
						public void onFailure(HttpException arg0, String arg1) {
							ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
						}
					});

		} else {
			showRedBagWindow(redBagInfo);
		}
	}

	public void dismissRedBagWindow() {
		if (bugWindows != null) {
			bugWindows.dismiss();
			bugWindows = null;
		}
	}

	/** 从底下弹出领取红包 */
	public void showRedBagWindow(final RedBagInfo redBagInfo) {
		final View view = mActivity.getWindow().getDecorView()
				.findViewById(android.R.id.content);// 获取一个view,popubwindow会用到
		ThreadUtil.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (bugWindows != null) {
					bugWindows.show(view, redBagInfo.getSendName(),
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									if (UserInfoManager.getInstance().isLogin()) {// 如果已经登录，则打开红包
										openRedBagDialog(redBagInfo);// 弹出拆红包dialog
										bugWindows.dismiss();
									} else {
										showTipDialog(ContextUtil.getString(R.string.login_phone_tip),LOGIN);
									}

								}
							});
				}
			}
		}, REDBAG_DELAY_TIME);
	}

	/** 弹出拆红包dialog */
	private void openRedBagDialog(final RedBagInfo redBagInfo) {
		final RedBagDialog redBugDialog = new RedBagDialog(mActivity);
		redBugDialog.show(redBagInfo.getLogoUrl(), redBagInfo.getSendName(),
				redBagInfo.getWishing(), new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Rotate3d rotate = new Rotate3d();
						rotate.setDuration(1000);
						rotate.setCenter(v.getMeasuredWidth() / 2,
								v.getMeasuredHeight() / 2);
						// rotate.setFillAfter(true); //
						// 使动画结束后定在最终画面，如果不设置为true，则将会回到初始画面
						v.startAnimation(rotate);
						rotate.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationRepeat(Animation animation) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onAnimationEnd(Animation animation) {
								// TODO Auto-generated method stub
								redBugDialog.dismiss();
								openRedBag();// 拆红包
							}
						});
					}
				});
	}

	/** 拆开发放红包 */
	public void openRedBag() {
		final CustomAlterProgressDialog progressDialog = new CustomAlterProgressDialog.Builder(mActivity)
				.setCancelable(false).build();
		progressDialog.show();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("orderNo", mOrderInfo.getOrderNumber());
		map.put("memberId", UserInfoManager.getInstance().getUserId());
		String URL = HttpURL.URL1 + HttpURL.ORDER_REDBAG;
		LogUtil.i(URL);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@SuppressWarnings("rawtypes")
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				LogUtil.i(responseInfo.result);
				progressDialog.dismiss();
				ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result,
						RedBagInfo.class);
				if (info != null) {
					if (info.getHttpCode().equals(HttpCode.SUCESS)) {
						RedBagInfo redBagInfo = (RedBagInfo) info.getBody();
						redBagInfo.setAr(false);
						redBagInfo.setCustomizeProductId(
								String.valueOf(mOrderInfo.getProductId()));
						
						if (redBagInfo.getMoney() == 0) {// 如果是空红包
							redBagInfo.setTips(ContextUtil
									.getString(R.string.empty_bag_tips));
							final EmptyRedBagDialog emptyRedBagDialog = new EmptyRedBagDialog(
									mActivity);
							emptyRedBagDialog.show(redBagInfo, null);

						} else {
							notifyCust(redBagInfo);
							JumpActivityManager.getInstance().toActivity(
									mActivity, RedBagActivity.class,
									Constant.Extra.REDBAG_INFO, redBagInfo);// 跳转到红包页面
						}
					} else if (info.getHttpCode().equals(HttpCode.FAIL)) {
						if(info.getPromptMessage() != null 
								|| info.getPromptMessage().length() >0)
							ScreenUtil.showToast(info.getPromptMessage());
					}
				}

			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}
		});
	}

	/** 通知各个界面更新红包数据 */
	private void notifyCust(RedBagInfo redBagInfo){
		ObserveManager.getInstance().notifyState(
				OrderCustFragment.class, RedBagOrderManager.class,
				mOrderInfo.getId()+","+
				String.valueOf(redBagInfo.getMoney()));// 如果领取成功了，则通过订单列表界面刷新成已领取。
		if(Constant.isFromPage.isOrderListSearch)
			ObserveManager.getInstance().notifyState(
					SearchOrderCustActivity.class, RedBagOrderManager.class,
					mOrderInfo.getId()+","+
					String.valueOf(redBagInfo.getMoney()));// 如果领取成功了，则通过订单列表界面刷新成已领取。
		if(Constant.isFromPage.isOrderDetail)
			ObserveManager.getInstance().notifyState(
					OrderCustDetailActivity.class, RedBagOrderManager.class,
					String.valueOf(redBagInfo.getMoney()));// 如果领取成功了，则通过订单详情界面刷新成已领取。
	}
	
	/** 3d旋转动画 */
	class Rotate3d extends Animation {

		private float mCenterX = 0;
		private float mCenterY = 0;

		public void setCenter(float centerX, float centerY) {
			mCenterX = centerX;
			mCenterY = centerY;
		}

		@Override
		protected void applyTransformation(float interpolatedTime,
				Transformation t) {
			Matrix matrix = t.getMatrix();

			Camera camera = new Camera();
			camera.save();
			// 设置camera动作为绕Y轴旋转
			// 总共旋转360度，因此计算在每个补间时间点interpolatedTime的角度即为两着相乘
			camera.rotateY(360 * interpolatedTime);
			// 根据camera动作产生一个matrix，赋给Transformation的matrix，以用来设置动画效果
			camera.getMatrix(matrix);
			camera.restore();

			matrix.preTranslate(-mCenterX, -mCenterY);
			matrix.postTranslate(mCenterX, mCenterY);
		}
	}

	/**
	 * 弹框提示
	 */
	protected void showTipDialog(String msg, final int type) {
		new CustomAlterDialog.Builder(mActivity).setMsg(msg)
		.setCancelButton(ContextUtil.getString(R.string.cancel), null)
		.setSureButton(ContextUtil.getString(R.string.sure), new OnDialogClickListener() {
			@Override
			public void onDialogClick(View v, CustomAlterDialog dialog) {
				// TODO Auto-generated method stub
				switch (type) {
				case LOGIN:// 跳转登录界面
					Intent loginIntent = new Intent(mActivity,
							LoginActivity.class);
					loginIntent.putExtra(Constant.Extra.ISFROMREDBAG, true);
					mActivity.startActivity(loginIntent);
					break;
				}
			}
		}).build().show();
		
//		CustomDialog customDialog = new CustomDialog(mActivity);
//		customDialog.showDialog(title, ContextUtil.getString(R.string.cancel), ContextUtil.getString(R.string.sure),
//				null, new OnDialogClickListener() {
//
//					@Override
//					public void onDialogClick(View v, CustomDialog dialog) {
//						// TODO Auto-generated method stub
//						switch (type) {
//						case LOGIN:// 跳转登录界面
//							Intent loginIntent = new Intent(mActivity,
//									LoginActivity.class);
//							loginIntent.putExtra(Constant.Extra.ISFROMREDBAG, true);
//							loginIntent.putExtra(Constant.Extra.ISFROMUINTY, false);
//							mActivity.startActivity(loginIntent);
//							break;
//						}
//					}
//				});

	}
}
