package com.wukong.hezhi.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.wukong.hezhi.ui.view.LoadMoreGridView;
import com.wukong.hezhi.ui.view.LoadMoreGridView.ILoadListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.ViewStub;

/**
 * 
 * @ClassName: CommodityActivity
 * @Description: TODO(选择定制的商品)
 * @author HuZhiyin
 * @date 2017-8-7 下午4:37:50
 * 
 */
public class CommodityActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.commodity_select);
	public String pageCode = "commodity_select";
	
	@ViewInject(R.id.lv_scroll)
	private LoadMoreGridView lv_scroll;

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;
	
	@ViewInject(R.id.empty)
	private ViewStub empty;
	
	private CommodityActivityAdapter adapter;
	private List<CommodityInfo> infos = new ArrayList<CommodityInfo>();

	private int page = 0;// 当前页
	private boolean isOver = false;// 是否加载介绍

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_commodity;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.commodity_select));
		headView.setRihgtTitleText("", R.drawable.icon_search);
		initView();
		setListener();
	}

	private void initView() {
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		mSwipeLayout.post(new Runnable() {
			@Override
			public void run() {
				mSwipeLayout.setRefreshing(true);
				page = 1;// 刷新将page置为1
				loadData();
			}
		});// 进入页面，自动加载
		adapter = new CommodityActivityAdapter(infos, this);
		lv_scroll.setAdapter(adapter);
		lv_scroll.setEmptyView(empty);
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
	}

	/** 加载数据 */
	private void loadData() {
		String URL = HttpURL.URL1 + HttpURL.COMMODITY_LIST;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page);
		HttpManager.getInstance().post(URL, map, new RequestCallBack<String>() {
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
				}
			}
		});
	}

	@OnClick(value = { R.id.header_right })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_right:
			toActivity(SearchCommodityActivity.class, Constant.Extra.FROM_ACTIVITY, TAG);
			break;
		}
	}

}
