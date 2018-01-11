package com.wukong.hezhi.ui.activity;

import com.lidroid.xutils.ViewUtils;
import com.umeng.analytics.MobclickAgent;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.ActivitiesManager;
import com.wukong.hezhi.manager.JumpActivityManager;
import com.wukong.hezhi.manager.LocationManager;
import com.wukong.hezhi.manager.NFCManager;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.manager.ObserveManager.Observer;
import com.wukong.hezhi.manager.StatsPageManager;
import com.wukong.hezhi.ui.view.CustomAlterProgressDialog;
import com.wukong.hezhi.ui.view.HeadView;
import com.wukong.hezhi.ui.view.swipeview.SwipeBackActivity;
import com.wukong.hezhi.ui.view.swipeview.SwipeBackLayout;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.DateUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ReflectUtil;
import com.wukong.hezhi.utils.SPUtil;
import com.wukong.hezhi.utils.StatusBarUtil;
import com.wukong.hezhi.utils.ThreadUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 
 * @ClassName: BaseActivity
 * @Description: TODO(Activity基类)
 * @author HuZhiyin
 * @date 2016-9-13 下午5:20:36
 * 
 */
public abstract class BaseActivity extends SwipeBackActivity implements OnClickListener, Observer {
	/** 当前类名 */
	protected String TAG = getClass().getSimpleName();

	/** 上下文对象（mActivity是子类的上下文对象，在onResume里为其赋值） */
	public BaseActivity mActivity;

	/** 在基类中实现滑动结束activity */
	private SwipeBackLayout mSwipeBackLayout;

	/** activity管理类 */
	private ActivitiesManager appManager = ActivitiesManager.getInstance();

	/** NFC管理类 */
	private NFCManager nfcManager = NFCManager.getInstance();
	
	/** activity头部 */
	protected HeadView headView;

	/** 进入页面时间 */
	private String mEnterDate;
	/** 离开页面时间 */
	private String mExitDate;

	@Override
	protected void onCreate(@Nullable Bundle arg0) {
		super.onCreate(arg0);
		mActivity = this;
		synchronized (this) {
			appManager.addActivity(this);
			ObserveManager.getInstance().addObserver(this);// 将activity加入到观察者内，方便发消息
		}
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		StatusBarUtil.setColor(this, ContextUtil.getColor(R.color.white));// 设置状态栏颜色
		setSwipeBackLayout();// 侧滑关闭当前页面
		setHeader();// 设置头部
		ViewUtils.inject(this); // 注入view和事件,需在setContentView()之后执行
		init();// 抽象方法，子类调用
	}

	/** 设置头部 */
	private void setHeader() {
		if (!isNotAddTitle()) {// 是否要头部title
			headView = new HeadView(this);
			headView.attachActivity(layoutId());
		} else {
			if (layoutId() != 0) {
				setContentView(layoutId());
			}
		}
	}

	/** 侧滑关闭当前页面 */
	private void setSwipeBackLayout() {
		mSwipeBackLayout = getSwipeBackLayout();
		mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);// 设置滑动方向
		mSwipeBackLayout.setEdgeSize(100);// 设置滑动范围
		setSwipeBackEnable(true); // 默认开启滑动删除，如需禁止滑动删除，在子类中将setSwipeBackEnable(false);
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();
		mActivity = this;
		nfcManager.initNFC(this);// 初始化NFC,要放在此处，不能放在Oncreate()。要不然会出bug，暂没有搞清楚原因。
		nfcManager.onResume(this);// nfc
		if (HezhiConfig.DEBUG) {
			/** 设置导航url */
			String url = SPUtil.getShareStr(this, HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.SERVICE_URL);// 可配置url

			if (!TextUtils.isEmpty(url)) {
				HttpURL.URL = url;
			}
			/** 设置传媒url */
			String url1 = SPUtil.getShareStr(this, HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.SERVICE_URL1);// 可配置url
			if (!TextUtils.isEmpty(url1)) {
				HttpURL.URL1 = url1;
			}
		}

		MobclickAgent.onPageStart(TAG);// 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。TAG为页面名称，可自定义)
		MobclickAgent.onResume(this); // 统计时长
		mEnterDate = DateUtil.getTime();// 页面进入时间
	}

	@Override
	protected void onPause() {
		super.onPause();
		mActivity = null;
		MobclickAgent.onPageEnd(TAG);
		MobclickAgent.onPause(this);
		mExitDate = DateUtil.getTime();// 页面离开时间
		statistics();
	}

	/** 大数据统计页面操作路径 */
	private void statistics() {
		LogUtil.i(ReflectUtil.getPageName(this));
		if (!TextUtils.isEmpty(ReflectUtil.getPageName(this)) && !TextUtils.isEmpty(ReflectUtil.getPageCode(this))) {// 页面的pageName和pageCode不为空是才统计
			HttpManager.getInstance().post(HttpURL.URL1 + HttpURL.STATISTICS_PAGE, StatsPageManager.getInstance()
					.getPageData(ReflectUtil.getPageName(this), ReflectUtil.getPageCode(this), mEnterDate, mExitDate),
					null);// 离开的时候统计页面信息
		}
	}
	
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		nfcManager.onNewIntent(intent);// nfc
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		LocationManager.getInstance().startLocation();// 开启定位,每次从后台回到应用，从新定位一次。
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		synchronized (this) {
			appManager.finishActivity(this);
			ObserveManager.getInstance().removeObserver(this);// activity销毁时候，将其移出被观察者。防止当前页面已被销毁，发通知刷新页面会导致空指针异常
		}
	}

	/** progressDialog */
	private CustomAlterProgressDialog progressDialog;

	/***
	 * 显示ProgressDialog
	 */
	public void showProgressDialog() {
		ThreadUtil.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				progressDialog = new CustomAlterProgressDialog.Builder(BaseActivity.this).setCancelable(false).build();
				progressDialog.show();
			}
		});
	}

	/***
	 * 隐藏ProgressDialog
	 */
	public void dismissProgressDialog() {
		ThreadUtil.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (progressDialog != null && progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
			}
		});

	}

	/** 跳转页面 */
	public void toActivity(Class<?> cls) {
		toActivity(cls, null, null);
	}

	/** 带参数跳转页面 */
	public void toActivity(Class<?> cls, String key, Object value) {
		JumpActivityManager.getInstance().toActivity(this, cls, key, value);
	}

	@Override
	public void onClick(View v) {

	}

	/** 是否不加头部Title（默认是加头部Title） */
	protected abstract boolean isNotAddTitle();

	/** 布局的R.layou.xxx */
	protected abstract int layoutId();

	/** 初始化 */
	protected abstract void init();

	/** 被观察的对象的回调方法 */
	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		// TODO Auto-generated method stub
	}

	/** 防止双击 */
	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// if (ev.getAction() == MotionEvent.ACTION_DOWN) {
	// if (NoDoubleClickUtil.isDoubleClk()) {
	// return true;
	// }
	// }
	// return super.dispatchTouchEvent(ev);
	// }
}
