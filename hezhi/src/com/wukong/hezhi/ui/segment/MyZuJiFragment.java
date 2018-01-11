package com.wukong.hezhi.ui.segment;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.MyZujiFragmentAdapter;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.UnityInfo;
import com.wukong.hezhi.db.DBManager;
import com.wukong.hezhi.ui.activity.UnityPlayerActivity;
import com.wukong.hezhi.ui.fragment.BaseFragment;
import com.wukong.hezhi.ui.view.NoScrollListview;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.PermissionUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * @ClassName: MyZuJiFragment
 * @Description: TODO(盒知界面扫描的记录部分)
 * @author HuZhiyin
 * @date 2017-7-28 上午9:52:31
 * 
 */
public class MyZuJiFragment extends BaseFragment {

	@ViewInject(R.id.lv_scroll)
	private NoScrollListview lv_scroll;

	@ViewInject(R.id.empty_ll)
	private LinearLayout empty_ll;

	@ViewInject(R.id.saoyisao_tv)
	private TextView saoyisao_tv;

	private MyZujiFragmentAdapter adapter;


	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_my_zuji, container,
				false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		return rootView;
	}

	private void init() {
		lv_scroll.setFocusable(false);
		adapter = new MyZujiFragmentAdapter(getDataFromDB(), getActivity());
		lv_scroll.setAdapter(adapter);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		init();
	}

	@SuppressWarnings("rawtypes")
	private List<UnityInfo> getDataFromDB() {
		List<String> jsonStrs = DBManager.getInstance().qurreyJsonsByTpye(
				DBManager.UNITY);
		List<UnityInfo> unityInfos = new ArrayList<UnityInfo>();
		if (jsonStrs != null) {
			for (int i = 0; i < jsonStrs.size(); i++) {
				ResponseJsonInfo info = JsonUtil.fromJson(jsonStrs.get(i),
						UnityInfo.class);
				UnityInfo unityInfo = (UnityInfo) info.getBody();
				LogUtil.i(unityInfo.getName());
				unityInfos.add(unityInfo);
			}
		}
		if (unityInfos.size() == 0) {
			empty_ll.setVisibility(View.VISIBLE);
		} else {
			empty_ll.setVisibility(View.GONE);
		}

		return unityInfos;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		// TODO Auto-generated method stub
		if (notifyTo.equals(getClass())) {
			if (notifyFrom.equals(MyZujiFragmentAdapter.class)) {
				init();
			}
		}
	}

	@OnClick(value = { R.id.saoyisao_tv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.saoyisao_tv:
			toUnity();
			break;
		}
	}

	private void toUnity() {
		if (PermissionUtil.cameraPermission()) {
			toActivity(UnityPlayerActivity.class);
		} else {
			ScreenUtil.showToast(ContextUtil
					.getString(R.string.camera_permission_tip));
		}
	}

}
