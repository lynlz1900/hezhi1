package com.wukong.hezhi.ui.activity;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.CommodityActivityAdapter;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.CommodityInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.KeyboardManager;
import com.wukong.hezhi.manager.StatsPageManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.LoadMoreGridView;
import com.wukong.hezhi.ui.view.LoadMoreGridView.ILoadListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: SearchCommodityActivity
 * @Description: TODO(搜索商品界面)
 * @author Huzhiyin
 * @date 2017年9月14日 上午9:26:55
 *
 */
public class SearchCommodityActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.search_commodity);
	public String pageCode = "search_commodity";

	@ViewInject(R.id.seacrh_et)
	private EditText seacrh_et;

	@ViewInject(R.id.seacrh_rl)
	private RelativeLayout seacrh_rl;

	@ViewInject(R.id.lv_scroll)
	private LoadMoreGridView lv_scroll;

	@ViewInject(R.id.empty)
	private ViewStub empty;

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;
	private CommodityActivityAdapter adapter;
	private List<CommodityInfo> infos = new ArrayList<CommodityInfo>();

	private int page = 0;// 当前页
	private boolean isOver = false;// 是否加载介绍

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_search_commodity;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		initView();
		setListener();
	}

	private void initView() {
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		adapter = new CommodityActivityAdapter(infos, this);
		lv_scroll.setAdapter(adapter);
	}

	@OnClick(value = { R.id.search_ll, R.id.header_left, R.id.seacrh_rl })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.seacrh_rl:
			refreshData();
			break;
		case R.id.header_left:
			finish();
			break;
		}
	}

	private void refreshData() {
		KeyboardManager.getInstance().hideKeyboard(this);// 隐藏键盘
		mSwipeLayout.post(new Runnable() {
			@Override
			public void run() {
				mSwipeLayout.setRefreshing(true);
				page = 1;// 刷新将page置为1
				loadData();
			}
		});// 进入页面，自动加载
	}

	/** 设置监听 */
	private void setListener() {

		mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				page = 1;// 刷新将page置为1
				loadData();

			}
		});

		lv_scroll.setLoadListener(new ILoadListener() {

			@Override
			public void onLoad() {
				// TODO Auto-generated method stub
				page++;
				if (!isOver) {// 如果加载结束
					loadData();
				} else {
					ScreenUtil.showToast(ContextUtil.getString(R.string.no_more_data));
					lv_scroll.loadCompleted();
				}
			}
		});
		seacrh_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {// 点击了搜索按钮
					refreshData();
				}
				return false;
			}

		});
	}

	private String search;

	/** 加载数据 */
	private void loadData() {
		if (TextUtils.isEmpty(seacrh_et.getText().toString().trim())) {
			ScreenUtil.showToast("请输入要搜索的关键字");
			mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
			lv_scroll.loadCompleted();// 上拉加载隐藏
			return;
		}
		search = seacrh_et.getText().toString().trim();
		String URL = HttpURL.URL1 + HttpURL.COMMODITY_LIST;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page);
		map.put("search", search);
		if(UserInfoManager.getInstance().getUserId() != 0)
			map.put("memberId", UserInfoManager.getInstance().getUserId());
		if(Constant.customScene.isFromCustomScene)
			map.put("customizationCaseId", Constant.customScene.customSceneId);
		map = StatsPageManager.getInstance().getPageSearchData(map);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
				lv_scroll.loadCompleted();// 上拉加载隐藏
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				mSwipeLayout.setRefreshing(false);// 加载完成将下拉刷新隐藏
				lv_scroll.loadCompleted();// 加载完成将上拉加载隐藏
				ResponseJsonInfo<CommodityInfos> info = JsonUtil.fromJson(arg0.result, CommodityInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					CommodityInfos cInfos = info.getBody();
					ArrayList<CommodityInfo> list = (ArrayList<CommodityInfo>) cInfos.getDataList();
					if (page == 1) {
						infos.clear();
					}
					if (list.size() == 0) {
						isOver = true;
					} else {
						isOver = false;
						infos.addAll(list);
					}
					adapter.notifyDataSetChanged();
					if (infos != null && infos.size() == 0) {
						lv_scroll.setEmptyView(empty);
					}
				} else {
					infos.clear();
					lv_scroll.setEmptyView(empty);
				}
			}
		});
	}

}
