package com.wukong.hezhi.ui.fragment;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.constants.HezhiConfig;
import com.wukong.hezhi.manager.ObserveManager;
import com.wukong.hezhi.manager.TestManager;
import com.wukong.hezhi.receiver.JgPushReceiver;
import com.wukong.hezhi.ui.activity.MainActivity;
import com.wukong.hezhi.ui.activity.MsgCenterActivity;
import com.wukong.hezhi.ui.activity.MyGuanzhuActivity;
import com.wukong.hezhi.ui.activity.UnityPlayerActivity;
import com.wukong.hezhi.ui.segment.MyGuanZhuFragment;
import com.wukong.hezhi.ui.segment.MyZuJiFragment;
import com.wukong.hezhi.ui.view.GuideView;
import com.wukong.hezhi.ui.view.GuideView.OnClickCallback;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.PackageUtil;
import com.wukong.hezhi.utils.PermissionUtil;
import com.wukong.hezhi.utils.SPUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * 
 * @ClassName: HezhiFragment
 * @Description: TODO(盒知)
 * @author HuZhiyin
 * @date 2016-9-13 下午3:53:49
 * 
 */
public class HezhiFragment extends BaseFragment {
	public String pageName = ContextUtil.getString(R.string.hezhi);
	public String pageCode = "hezhi";

	@ViewInject(R.id.header_left)
	private RelativeLayout header_left;

	@ViewInject(R.id.header_right)
	private RelativeLayout header_right;

	@ViewInject(R.id.red_point)
	private ImageView red_point;

	@ViewInject(R.id.scl_view)
	private ScrollView scl_view;

	@ViewInject(R.id.lookall_tv)
	private TextView lookall_tv;

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;

	private boolean isShowRedPoint = false;// 小红点是否显示

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
			@Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_hezhi, container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		initView();
		setListener();
		return rootView;
	}

	private void setListener() {
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
				ObserveManager.getInstance().notifyState(MyGuanZhuFragment.class, HezhiFragment.class, null);
			}
		});
	}

	private void addFragment() {
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		MyGuanZhuFragment myGuanZhuFragment = new MyGuanZhuFragment();
		transaction.replace(R.id.myguanzhu_fr, myGuanZhuFragment);
		MyZuJiFragment myZuJiFragment = new MyZuJiFragment();
		transaction.replace(R.id.myzuji_fr, myZuJiFragment);
		transaction.commitAllowingStateLoss();
	}

	private void initView() {
		scl_view.smoothScrollTo(0, 20);
		addFragment();
		setPoint();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		setPoint();
	}

	private void setPoint() {
		isShowRedPoint = SPUtil.getShareBoolean(ContextUtil.getContext(), HezhiConfig.SP_COMMON_CONFIG,
				Constant.Sp.REP_POINT);
		// boolean isCheckedAll =
		// JgPushManager.getInstance().isChecked();//所有的消息都点击过了
		if (isShowRedPoint) {
			red_point.setVisibility(View.VISIBLE);
		} else {
			red_point.setVisibility(View.INVISIBLE);
		}
	}

	@OnClick(value = { R.id.header_left, R.id.header_right, R.id.header_title, R.id.saoyisao_tv, R.id.lookall_tv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.header_left:
			toUnity();

			break;
		case R.id.header_right:
			SPUtil.savaToShared(ContextUtil.getContext(), HezhiConfig.SP_COMMON_CONFIG, Constant.Sp.REP_POINT, false);// 当点击按钮时候，小红点消失
			toActivity(MsgCenterActivity.class);
			break;
		case R.id.lookall_tv:// 查看全部
			toActivity(MyGuanzhuActivity.class);
			break;
		case R.id.header_title:
			if (HezhiConfig.DEBUG) {
				TestManager.getInstance().test(getActivity());
			}
			break;
		}
	}

	private void toUnity() {
		if (!PermissionUtil.cameraPermission()) {
			ScreenUtil.showToast(ContextUtil.getString(R.string.photo_permission_tip));
			return;
		}
		toActivity(UnityPlayerActivity.class);
	}

	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		// TODO Auto-generated method stub
		if (notifyTo.equals(getClass())) {

			if (notifyFrom.equals(MainActivity.class)) {
//				showGuideView();
			}

			if (notifyFrom.equals(JgPushReceiver.class)) {
				if ((int) msg == JgPushReceiver.MSG) {// 极光推送的消息
					setPoint();
				}
			}
		}

	}

	private void showGuideView() {

		final GuideView.Builder builder = new GuideView.Builder(getActivity(), PackageUtil.getVersionName());
		LinearLayout layout_saoyisao = (LinearLayout) ScreenUtil.inflate(R.layout.layout_guide_saoyisao);
		LinearLayout layout_msg = (LinearLayout) ScreenUtil.inflate(R.layout.layout_guide_msg);
		builder.setTextSize(20).addHintView(header_left, layout_saoyisao, GuideView.Direction.RIGHT_BOTTOM,
				GuideView.MyShape.CIRCULAR, 0, 0, new OnClickCallback() {

					@Override
					public void onGuideViewClicked() {
						// TODO Auto-generated method stub
						builder.showNext();
					}
				}).addHintView(header_right, layout_msg, GuideView.Direction.LEFT_BOTTOM, GuideView.MyShape.CIRCULAR, 0,
						0, new OnClickCallback() {
							@Override
							public void onGuideViewClicked() {
								// TODO Auto-generated method stub
								builder.showNext();
							}
						});
		builder.show();
	}

}
