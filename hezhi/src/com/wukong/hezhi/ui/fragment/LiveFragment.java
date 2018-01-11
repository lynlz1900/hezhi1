package com.wukong.hezhi.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.LiveFragmentAdapter;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.VuforiaInfo;
import com.wukong.hezhi.bean.VuforiaInfos;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.KeyboardManager;
import com.wukong.hezhi.manager.RedBagGanzManager;
import com.wukong.hezhi.manager.RedBagVideoManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.activity.BaseActivity;
import com.wukong.hezhi.ui.view.LoadMoreListView;
import com.wukong.hezhi.ui.view.LoadMoreListView.ILoadListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 
 * @ClassName: LiveFragment
 * @Description: TODO(品牌直播)
 * @author HuZhiyin
 * @date 2017-3-14 下午1:41:31
 * 
 */
public class LiveFragment extends BaseFragment {
//	public String pageName = ContextUtil.getString(R.string.band_live);
//	public String pageCode ="band_live";
	@ViewInject(R.id.seacrh_et)
	private EditText seacrh_et;

	@ViewInject(R.id.seacrh_rl)
	private RelativeLayout seacrh_rl;

	@ViewInject(R.id.lv_scroll)
	private LoadMoreListView lv_scroll;

	@ViewInject(R.id.empty)
	private ViewStub empty;

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;

	private LiveFragmentAdapter adapter;
	private List<VuforiaInfo> cards = new ArrayList<VuforiaInfo>();
	private int page = 0;// 当前页
	private int countPage;// 总页数
	private String tpye;
	

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_live, container,
				false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		Bundle mBundle = getArguments();
		tpye = mBundle.getString("tpye");
		initView();
		setListener();
		return rootView;
	}

	public void refreshRedBagType(String ppid) {// 刷新领取红包状态
		List<VuforiaInfo> cardsCache = new ArrayList<VuforiaInfo>();
		for (VuforiaInfo vuforiaInfo : cards) {
			if (ppid.equals(vuforiaInfo.getPpid())) {// 设置为已领取
				vuforiaInfo.setReceiveMark(1);
			}
			cardsCache.add(vuforiaInfo);
		}
		cards.clear();
		cards.addAll(cardsCache);
		adapter.notifyDataSetChanged();
	}

	public void refreshAttenNum(String ppid) {// 刷新浏览次数状态
		List<VuforiaInfo> cardsCache = new ArrayList<VuforiaInfo>();
		for (VuforiaInfo vuforiaInfo : cards) {
			if (ppid.equals(vuforiaInfo.getPpid())) {
				int i = vuforiaInfo.getMaxNum() + 1;
				vuforiaInfo.setMaxNum(i);
			}
			cardsCache.add(vuforiaInfo);
		}
		cards.clear();
		cards.addAll(cardsCache);
		adapter.notifyDataSetChanged();
	}

	public void initView() {
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		mSwipeLayout.post(new Runnable() {
			@Override
			public void run() {
				mSwipeLayout.setRefreshing(true);
				page = 1;// 刷新将page置为1
				loadData();
			}
		});// 进入页面，自动加载
		adapter = new LiveFragmentAdapter(cards, (BaseActivity) getActivity());
		lv_scroll.setAdapter(adapter);
		lv_scroll.setEmptyView(empty);
	}

	/** 设置监听 */
	private void setListener() {

		mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				page = 1;// 刷新将page置为1
				loadData();
				LogUtil.d("OnRefreshListener" + page);

			}
		});
		lv_scroll.setLoadListener(new ILoadListener() {
			@Override
			public void onLoad() {
				// TODO Auto-generated method stub
				page++;
				LogUtil.d("setOnLoadListener" + page);
				if (page <= countPage) {// 如果加载的页数小于或等于总页数，则加载
					loadData();
				} else {
					// ScreenUtil.showToast("没有更多数据了");
					lv_scroll.loadCompleted();
				}
			}
		});
	}

	@OnClick(value = { R.id.seacrh_rl })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.seacrh_rl:
			KeyboardManager.getInstance().hideKeyboard(getActivity());// 隐藏键盘
			mSwipeLayout.post(new Runnable() {
				@Override
				public void run() {
					mSwipeLayout.setRefreshing(true);
					page = 1;// 刷新将page置为1
					loadData();
				}
			});// 进入页面，自动加载
			break;
		}
	}

	/** 加载数据 */
	private void loadData() {
		String name = seacrh_et.getText().toString().trim();
		long memberId = -1;
		if (UserInfoManager.getInstance().isLogin()) {
			memberId = UserInfoManager.getInstance().getUserId();
		}
		String url = HttpURL.URL1 + HttpURL.LIVE_BAND + "page=" + page
				+ "&name=" + name + "&arTypeId=" + tpye + "&memberId="
				+ memberId;
		LogUtil.d(url);
		HttpManager.getInstance().get(url, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
				lv_scroll.loadCompleted();// 上拉加载隐藏
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				mSwipeLayout.setRefreshing(false);// 加载完成将下拉刷新隐藏
				lv_scroll.loadCompleted();// 加载完成将上拉加载隐藏
				ResponseJsonInfo<VuforiaInfos> info = JsonUtil.fromJson(
						arg0.result, VuforiaInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					VuforiaInfos vuforiaInfos = info.getBody();
					if(vuforiaInfos == null){
						return;
					}
					countPage = vuforiaInfos.getCountPage();// 获取总页数
					LogUtil.d("countPage" + countPage);
					ArrayList<VuforiaInfo> list = (ArrayList<VuforiaInfo>) vuforiaInfos
							.getDataList();
					if (page == 1) {
						cards.clear();// 清空数据
					}
					for (VuforiaInfo vuforiaInfo : list) {
						cards.add(vuforiaInfo);
					}
					adapter.notifyDataSetChanged();
				} else {
					ScreenUtil.showToast("数据加载失败");
				}
			}
		});
	}

	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		// TODO Auto-generated method stub
		if (notifyTo.equals(getClass())) {
			if (notifyFrom.equals(LiveFragmentAdapter.class)) {
				try {
					String ppid = (String) msg;
					refreshAttenNum(ppid);
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else if (notifyFrom.equals(RedBagGanzManager.class)
					|| notifyFrom.equals(RedBagVideoManager.class)) {
				refreshRedBagType((String) msg);
			}
		}
	}
}
