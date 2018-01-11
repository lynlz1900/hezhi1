package com.wukong.hezhi.manager;

import java.util.HashMap;
import java.util.Map;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.ProductInfo;
import com.wukong.hezhi.bean.RedBagInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.ui.activity.LoginActivity;
import com.wukong.hezhi.ui.activity.RedBagActivity;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.ui.view.CustomAlterProgressDialog;
import com.wukong.hezhi.ui.view.EmptyRedBagDialog;
import com.wukong.hezhi.ui.view.RedBagDialog;
import com.wukong.hezhi.ui.view.RedBagWindows;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import com.wukong.hezhi.utils.ThreadUtil;

import android.app.Activity;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;

public class RedBagNfcManager {
	private static Activity mActivity;
	private ProductInfo productInfo;
	private static final int LOGIN = 0;// 登录
	private static final int BIND = 1;// 绑定
	private static final int NFC_REDBAG = 1;// NFC红包
	private static int delayTime = 2000;// 默认延迟2秒谈红包

	private RedBagNfcManager() {
	}

	private static class Holder {
		private static final RedBagNfcManager SINGLETON = new RedBagNfcManager();
	}

	/**
	 * 单一实例
	 */
	public static RedBagNfcManager getInstance(Activity activity) {
		mActivity = activity;
		return Holder.SINGLETON;
	}

	/** 检查是否有红包 */
	public void checkRedBag(ProductInfo info) {
		if (info == null) {
			return;
		}
		dismissRedBagWindow();
		bugWindows = RedBagWindows.getInstance();
		this.productInfo = info;
		String URL = HttpURL.URL1 + HttpURL.CHECK_ACTIVITY_NFC + "authId=" + productInfo.getAuthId() + "&productId="
				+ productInfo.getProductId();
		HttpManager.getInstance().get(URL, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				ResponseJsonInfo info = JsonUtil.fromJson(arg0.result, RedBagInfo.class);
				if (info != null) {
					if (info.getHttpCode().equals(HttpCode.SUCESS)) {
						RedBagInfo redBagInfo = (RedBagInfo) info.getBody();
						checkReceived(redBagInfo);// 检查是否领取过红包
					} else if (info.getHttpCode().equals(HttpCode.FAIL)) {
						// ScreenUtil.showToast(info.getPromptMessage());
					}
				}
			}
		});
	}

	/** 检查是否领取过红包 */
	private void checkReceived(final RedBagInfo redBagInfo) {
		if (UserInfoManager.getInstance().isLogin()) {// 是否是手机登录
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("authId", productInfo.getAuthId());
			map.put("productId", productInfo.getProductId());
			map.put("memberId", UserInfoManager.getInstance().getUserId());
			map.put("deviceId", PackageUtil.getPhoneId());
			map.put("nfcId", NFCManager.uid);
			String URL = HttpURL.URL1 + HttpURL.CHECK_RECEIVED_NFC;
			HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
				@Override
				public void onSuccess(ResponseInfo<String> responseInfo) {
					// TODO Auto-generated method stub
					ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result, RedBagInfo.class);
					if (info != null) {
						if (info.getHttpCode().equals(HttpCode.SUCESS)) {
							RedBagInfo info2 = (RedBagInfo) info.getBody();
							if (!info2.isResult()) {// 如果没有领取过，则弹出红包领取toast
								showRedBagWindow(redBagInfo);
							}
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
		} else {
			showRedBagWindow(redBagInfo);
		}
	}

	RedBagWindows bugWindows;

	private void dismissRedBagWindow() {
		if (bugWindows != null) {
			try {
				bugWindows.dismiss();
			} catch (Exception e) {
			}
			
			bugWindows = null;
		}
	}

	/** 从底下弹出领红包toast */
	private void showRedBagWindow(final RedBagInfo redBagInfo) {

		final View view = mActivity.getWindow().getDecorView().findViewById(android.R.id.content);// 获取一个view,popubwindow会用到
		ThreadUtil.postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (bugWindows != null) {
					bugWindows.show(view, redBagInfo.getSendName(), new OnClickListener() {
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							if (UserInfoManager.getInstance().isLogin()) {// 如果已经登录，则打开红包
								openRedBagDialog(redBagInfo);// 弹出拆红包dialog
								bugWindows.dismiss();
							} else {

								showTipDialog(ContextUtil.getString(R.string.login_phone_tip), LOGIN);
							}

						}
					});
				}

			}
		}, delayTime);
	}

	/** 弹出拆红包dialog */
	private void openRedBagDialog(final RedBagInfo redBagInfo) {

		final RedBagDialog redBugDialog = new RedBagDialog(mActivity);
		redBugDialog.show(redBagInfo.getLogoUrl(), redBagInfo.getSendName(), redBagInfo.getWishing(),
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						Rotate3d rotate = new Rotate3d();
						rotate.setDuration(1000);
						rotate.setCenter(v.getMeasuredWidth() / 2, v.getMeasuredHeight() / 2);
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
	private void openRedBag() {
		final CustomAlterProgressDialog progressDialog = new CustomAlterProgressDialog.Builder(mActivity)
				.setCancelable(false).build();
		progressDialog.show();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("authId", productInfo.getAuthId());
		map.put("productId", productInfo.getProductId());
		map.put("memberId", UserInfoManager.getInstance().getUserId());
		map.put("deviceId", PackageUtil.getPhoneId());
		map.put("nfcId", NFCManager.uid);
		String URL = HttpURL.URL1 + HttpURL.SEND_REDBAG_NFC;
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
				ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result, RedBagInfo.class);
				if (info != null) {
					if (info.getHttpCode().equals(HttpCode.SUCESS)) {
						RedBagInfo redBagInfo = (RedBagInfo) info.getBody();
						redBagInfo.setAuthId(productInfo.getAuthId());
						redBagInfo.setProductId(productInfo.getProductId());
						if (redBagInfo.getMoney() == 0) {// 如果是空红包
							redBagInfo.setTips(ContextUtil.getString(R.string.empty_bag_tips));
							final EmptyRedBagDialog emptyRedBagDialog = new EmptyRedBagDialog(mActivity);
							emptyRedBagDialog.show(redBagInfo, null);

						} else {
							redBagInfo.setRegbagType(NFC_REDBAG);
							JumpActivityManager.getInstance().toActivity(mActivity, RedBagActivity.class,
									Constant.Extra.REDBAG_INFO, redBagInfo);// 跳转到红包页面
						}

					} else if (info.getHttpCode().equals(HttpCode.FAIL)) {
						ScreenUtil.showToast(info.getPromptMessage());
					}
				}

			}

			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				ScreenUtil.showToast(arg1);
				progressDialog.dismiss();
			}
		});
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
		protected void applyTransformation(float interpolatedTime, Transformation t) {
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
							JumpActivityManager.getInstance().toActivity(
									// 跳转登录界面
									mActivity, LoginActivity.class, Constant.Extra.ISFROMREDBAG, true);
							break;
						}
					}
				}).build().show();

		// CustomDialog customDialog = new CustomDialog(mActivity);
		// customDialog.showDialog(title,
		// ContextUtil.getString(R.string.cancel),
		// ContextUtil.getString(R.string.sure),
		// null, new OnDialogClickListener() {
		//
		// @Override
		// public void onDialogClick(View v, CustomDialog dialog) {
		// // TODO Auto-generated method stub
		// switch (type) {
		// case LOGIN:// 跳转登录界面
		// JumpActivityManager.getInstance().toActivity(
		// // 跳转登录界面
		// mActivity, LoginActivity.class, Constant.Extra.ISFROMREDBAG, true);
		// break;
		// }
		// }
		// });

	}

}
