package com.wukong.hezhi.manager;

import com.wukong.hezhi.R;
import com.wukong.hezhi.bean.UserInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.ui.activity.LoginActivity;
import com.wukong.hezhi.ui.view.CustomAlterDialog;
import com.wukong.hezhi.ui.view.CustomAlterDialog.OnDialogClickListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.SPUtil;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

/***
 * 
 * @ClassName: UserInfoManager
 * @Description: TODO(用户信息管理类)
 * @author HuZhiyin
 * @date 2017-1-25 下午1:02:48
 * 
 */
public class UserInfoManager {
	/** 用户信息 */
	private UserInfo userInfo;
	/** 用户信息状态是否发生改变 */
	public static boolean USERINFO_CHANGE = true;

	private UserInfoManager() {
	}

	private static class Holder {
		private static final UserInfoManager SINGLETON = new UserInfoManager();
	}

	public static UserInfoManager getInstance() {
		return Holder.SINGLETON;
	}

	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
		if (userInfo != null) {
			SPUtil.savaToShared(ContextUtil.getContext(),
					HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.USER_ID,
					userInfo.getId());// 将userId存在本地。记住登录状态
		}
		USERINFO_CHANGE = true;// 每次重置用户信息，都要刷新个人中心页面
	}

	/** 获取手机号 */
	public String getPhone() {
		if (userInfo == null) {
			return "";
		} else {
			return userInfo.getPhone();
		}
	}

	/** 判断是否登录 */
	public boolean isLogin() {

		if (getUserId() == 0) {
			return false;
		} else {
			return true;
		}

	}

	/** 获取用户id */
	public long getUserId() {

		if (userInfo == null) {// 如果userInfo为空，则取保存本地的userId（获取上一次登录的状态）
			long userId = SPUtil.getShareLong(ContextUtil.getContext(),
					HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.USER_ID);
			return userId;
		} else {
			return userInfo.getId();
		}
	}

	/** 获取用户id或者设备id */
	public String getUserIdOrDeviceId() {
		String userId = "";
		if (getUserId() == 0) {// 如果用户没有登录
			userId = PackageUtil.getPhoneId();
		} else {
			userId = getUserId() + "";
		}
		return userId;
	}

	/** 清除用户的数据 */
	public void cleanUserInfo() {
		setUserInfo(null);// 清除用户信息
		SPUtil.savaToShared(ContextUtil.getContext(),
				HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.USER_ID, 0L);// 将userId置为无效id

	}
	
	public void showDialog(final Activity context,String msg) {
		new CustomAlterDialog.Builder(context).setTitle(msg)
				.setCancelButton(ContextUtil.getString(R.string.i_know), null)
				.setSureButton(ContextUtil.getString(R.string.login_again), new OnDialogClickListener() {
					@Override
					public void onDialogClick(View v, CustomAlterDialog dialog) {
						context.startActivity(new Intent(context, LoginActivity.class));
					}
				}).build().show();

	}
	
	public boolean userLoginStatus(Activity activity,int status){
		if (Constant.loginType.LOGIN_OVER == status) {// 登录过期
			UserInfoManager.getInstance().showDialog(activity,ContextUtil.getString(R.string.login_overdue));
			UserInfoManager.getInstance().cleanUserInfo();// 将用户数据置空
		} else if (Constant.loginType.LOGIN_PUSH == status) {// 被挤下线
			UserInfoManager.getInstance().showDialog(activity,ContextUtil.getString(R.string.login_push));
			UserInfoManager.getInstance().cleanUserInfo();// 将用户数据置空
		}else{
			return true;
		}
		
		return false;
	}
}
