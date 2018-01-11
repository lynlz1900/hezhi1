package com.wukong.hezhi.manager;

import java.util.HashMap;
import java.util.Map;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.RedBagInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.UnityInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.ui.activity.LoginActivity;
import com.wukong.hezhi.ui.activity.RedBagActivity;
import com.wukong.hezhi.ui.fragment.LiveFragment;
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
import android.content.Intent;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Transformation;

public class RedBagVideoManager {
	private static Activity mActivity;
	private static String ppid = "0";// 感知应用id
	private static final int LOGIN = 0;// 登录
	private static final int BIND = 1;// 绑定
	// public static final int GANZ_REDBAG = 2;// 感知红包
	private RedBagWindows bugWindows;
	private static int delayTime = 0;// 默认延迟12秒谈红包

	private RedBagVideoManager() {
	} // 构造方法是私有的，从而避免外界利用构造方法直接创建任意多实例。

	private static class Holder {
		private static final RedBagVideoManager SINGLETON = new RedBagVideoManager();
	}

	/**
	 * 单一实例
	 * */
	public static RedBagVideoManager getInstance(Activity activity) {
		mActivity = activity;
		return Holder.SINGLETON;
	}

	/** 检查是否有红包 */
	public void checkRedBag(String jsonStr) {
		bugWindows = RedBagWindows.getInstance();
		ResponseJsonInfo info = JsonUtil.fromJson(jsonStr, UnityInfo.class);
		if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
			UnityInfo unityInfo = (UnityInfo) info.getBody();
			ppid = unityInfo.getPpId();
		} else {
			return;
		}

		String URL = HttpURL.URL1 + HttpURL.CHECK_ACTIVITY_GANZ
				+ "perceptionPictureId=" + ppid;
		HttpManager.getInstance().get(URL, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				ResponseJsonInfo info = JsonUtil.fromJson(arg0.result,
						RedBagInfo.class);
				if (info != null) {
					if (info.getHttpCode().equals(HttpCode.SUCESS)) {
						RedBagInfo redBagInfo = (RedBagInfo) info.getBody();
						checkHasReceived(redBagInfo);// 检查是否领取过
					} else if (info.getHttpCode().equals(HttpCode.FAIL)) {
						// ScreenUtil.showToast(info.getPromptMessage());
					}
				}
			}
		});
	}

	/** 检查红包是否领取过 */
	public void checkHasReceived(final RedBagInfo redBagInfo) {

		if (UserInfoManager.getInstance().isLogin()) {// 是否是手机登录
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("perceptionPictureId", ppid);
			map.put("memberId", UserInfoManager.getInstance().getUserId());
			map.put("deviceId", PackageUtil.getPhoneId());
			String URL = HttpURL.URL1 + HttpURL.CHECK_RECEIVED_GANZ;
			HttpManager.getInstance().post(URL, map,
					new RequestCallBack<String>() {
						@Override
						public void onSuccess(ResponseInfo<String> responseInfo) {
							// TODO Auto-generated method stub
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
									ScreenUtil.showToast(info
											.getPromptMessage());
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
				// TODO Auto-generated method stub
				if (bugWindows != null) {
					bugWindows.show(view, redBagInfo.getSendName(),
							new OnClickListener() {
								@Override
								public void onClick(View v) {
									if (UserInfoManager.getInstance().isLogin()) {// 如果已经登录，则打开红包
										openRedBagDialog(redBagInfo);// 弹出拆红包dialog
										bugWindows.dismiss();
									}  else {

										showTipDialog(
												ContextUtil
														.getString(R.string.login_phone_tip),
												LOGIN);
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
		map.put("perceptionPictureId", ppid);
		map.put("memberId", UserInfoManager.getInstance().getUserId());
		map.put("deviceId", PackageUtil.getPhoneId());
		String URL = HttpURL.URL1 + HttpURL.SEND_REDBAG_GANZHI;
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
				ResponseJsonInfo info = JsonUtil.fromJson(responseInfo.result,
						RedBagInfo.class);
				if (info != null) {
					if (info.getHttpCode().equals(HttpCode.SUCESS)) {

						RedBagInfo redBagInfo = (RedBagInfo) info.getBody();
						redBagInfo.setPpid(ppid);
						// redBagInfo.setAr(true);
						ObserveManager.getInstance().notifyState(
								LiveFragment.class, RedBagVideoManager.class,
								ppid);// 如果领取成功了，则通过品牌直播界面刷新成已领取。
						if (redBagInfo.getMoney() == 0) {// 如果是空红包
							redBagInfo.setTips(ContextUtil
									.getString(R.string.empty_bag_tips));
							final EmptyRedBagDialog emptyRedBagDialog = new EmptyRedBagDialog(
									mActivity);
							emptyRedBagDialog.show(redBagInfo, null);

						} else {
							JumpActivityManager.getInstance().toActivity(
									mActivity, RedBagActivity.class,
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
		
	}
}
