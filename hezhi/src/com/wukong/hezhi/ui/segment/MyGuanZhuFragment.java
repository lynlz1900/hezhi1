package com.wukong.hezhi.ui.segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.MyGuanZhuFragmentAdapter;
import com.wukong.hezhi.adapter.RecommendAdapter;
import com.wukong.hezhi.bean.MyGuanZhuInfo;
import com.wukong.hezhi.bean.MyGuanZhuInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.activity.BaseActivity;
import com.wukong.hezhi.ui.activity.MyGuanzhuActivity;
import com.wukong.hezhi.ui.fragment.BaseFragment;
import com.wukong.hezhi.ui.fragment.HezhiFragment;
import com.wukong.hezhi.ui.view.NoScrollGridview;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;

/**
 * 
* @ClassName: MyGuanZhuFragment 
* @Description: TODO(盒知界面关注商家的部分) 
* @author HuZhiyin 
* @date 2017-7-28 上午9:52:06 
*
 */
public class MyGuanZhuFragment extends BaseFragment {
	@ViewInject(R.id.lookall_tv)
	private TextView lookall_tv;

	@ViewInject(R.id.my_gd)
	private NoScrollGridview my_gd;

	@ViewInject(R.id.horizontal_lv)
	private NoScrollGridview horizontal_lv;

	@ViewInject(R.id.title_tv)
	private TextView title_tv;

	@ViewInject(R.id.recomend_ll)
	private LinearLayout recomend_ll;

	private MyGuanZhuFragmentAdapter adapter;
	private RecommendAdapter adapter2;

	List<MyGuanZhuInfo> myGuanZhuInfos = new ArrayList<MyGuanZhuInfo>();
	List<MyGuanZhuInfo> myGuanZhuInfos2 = new ArrayList<MyGuanZhuInfo>();


	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_my_guanzhu,
				container, false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		init();
		return rootView;
	}

	private void init() {
		adapter = new MyGuanZhuFragmentAdapter(myGuanZhuInfos,
				(BaseActivity) getActivity());
		adapter2 = new RecommendAdapter(myGuanZhuInfos2,
				(BaseActivity) getActivity());
		my_gd.setAdapter(adapter);
		horizontal_lv.setAdapter(adapter2);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		loadData();
	}

	/** 关注 */
	private void loadData() {
		long userId;
		if (UserInfoManager.getInstance().isLogin()) {
			userId = UserInfoManager.getInstance().getUserId();
		} else {
			userId = -1;
		}
		String url = HttpURL.URL1 + HttpURL.GUANZHU_BUSINESS;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("memberId", userId);
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				ResponseJsonInfo<MyGuanZhuInfos> info = JsonUtil.fromJson(
						arg0.result, MyGuanZhuInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					MyGuanZhuInfos infos = info.getBody();
					ArrayList<MyGuanZhuInfo> list = (ArrayList<MyGuanZhuInfo>) infos
							.getDataList();
					if ("recommend".equals(infos.getDataType())) {// 推荐关注
						lookall_tv.setVisibility(View.INVISIBLE);
						title_tv.setText(ContextUtil.getString(R.string.band_guanzhu));
						my_gd.setVisibility(View.GONE);
						myGuanZhuInfos2.clear();
						for (MyGuanZhuInfo myGuanZhuInfo : list) {
							myGuanZhuInfos2.add(myGuanZhuInfo);
						}
						adapter2.notifyDataSetChanged();
					} else if ("attention".equals(infos.getDataType())) {// 我的关注
						title_tv.setText(ContextUtil.getString(R.string.my_guanzhu));
						my_gd.setVisibility(View.VISIBLE);
						if (list.size() >= 3) {
							lookall_tv.setVisibility(View.VISIBLE);
						} else {
							lookall_tv.setVisibility(View.INVISIBLE);
						}
						myGuanZhuInfos.clear();
						for (MyGuanZhuInfo myGuanZhuInfo : list) {
							myGuanZhuInfos.add(myGuanZhuInfo);
						}
						adapter.notifyDataSetChanged();
					}

				}
			}
		});
	}

	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object change) {
		// TODO Auto-generated method stub
		super.updateState(notifyTo, notifyFrom, change);
		if (notifyTo.equals(getClass())) {
			if (notifyFrom.equals(HezhiFragment.class)) {
				loadData();
			}
		}
	}

	@OnClick(value = { R.id.lookall_tv })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.lookall_tv:// 查看全部
			toActivity(MyGuanzhuActivity.class);
			break;
		}
	}
}
