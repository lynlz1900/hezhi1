package com.wukong.hezhi.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.umeng.analytics.MobclickAgent;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.manager.ObserveManager.Observer;
import com.wukong.hezhi.manager.StatsPageManager;
import com.wukong.hezhi.utils.DateUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ReflectUtil;

import java.io.Serializable;

public class BaseFragment extends Fragment implements OnClickListener, Observer {
	/** 当前类名 */
	protected String TAG = getClass().getSimpleName();
	/** 进入页面时间 */
	private String mEnterDate;
	/** 离开页面时间 */
	private String mExitDate;

	public Activity mActivity;

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		synchronized (this) {
			ObserveManager.getInstance().addObserver(this);// 将fragment加入到观察者内，方便发消息
		}
		mActivity = getActivity();
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/** 跳转页面 */
	public void toActivity(Class<?> cls) {
		toActivity(cls, null, null);
	}

	/** 带参数跳转页面 */
	public void toActivity(Class<?> cls, String key, Object value) {
		Intent intent = new Intent(getActivity(), cls);
		if (value instanceof String) {
			intent.putExtra(key, (String) value);
		} else if (value instanceof Serializable) {
			intent.putExtra(key, (Serializable) value);
		}
		startActivity(intent);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart(TAG);// 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。TAG为页面名称，可自定义)
		MobclickAgent.onResume(getActivity()); // 统计时长
		LogUtil.d(TAG + "onResume");
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd(TAG);
		MobclickAgent.onPause(getActivity());
		LogUtil.d(TAG + "onPause");
		if (isShow) {// 此处用于activity 跳转
			statistics();
		}
	}


	/** 是否显示过此fragment */
	private boolean hasShow = false;
	/** 当前fragment是否显示 */
	private boolean isShow;

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		// TODO Auto-generated method stub
		super.setUserVisibleHint(isVisibleToUser);
		isShow = isVisibleToUser;
		if (isVisibleToUser) {// 如果是可见,统计进入时间
			mEnterDate = DateUtil.getTime();
			hasShow = true;
			LogUtil.d(TAG + "visble");
		}
		if (!isVisibleToUser && hasShow) {// 如果是不可见,统计离开时间，并提交数据
			statistics();
		}
	}

	/**大数据统计页面操作路径*/
	private void statistics() {
		mExitDate = DateUtil.getTime();
		if (!TextUtils.isEmpty(ReflectUtil.getPageName(this)) && !TextUtils.isEmpty(ReflectUtil.getPageCode(this))) {// 页面的pageName和pageCode不为空是才提交
			HttpManager.getInstance().post(HttpURL.URL1 + HttpURL.STATISTICS_PAGE, StatsPageManager.getInstance().getPageData(ReflectUtil.getPageName(this), ReflectUtil.getPageCode(this),
					mEnterDate, mExitDate), null);// 离开的时候统计页面信息
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		synchronized (this) {
			ObserveManager.getInstance().removeObserver(this);// fragment销毁时候，将其移出被观察者。防止当前页面已被销毁，发通知刷新页面会导致空指针异常
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	/** 被观察的对象的回调方法 */
	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		// TODO Auto-generated method stub

	}

}
