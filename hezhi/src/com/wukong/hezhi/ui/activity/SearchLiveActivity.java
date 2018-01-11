package com.wukong.hezhi.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.wukong.hezhi.adapter.LiveFragmentAdapter;
import com.wukong.hezhi.bean.AddressInfo;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.bean.VuforiaInfo;
import com.wukong.hezhi.bean.VuforiaInfos;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.KeyboardManager;
import com.wukong.hezhi.manager.LocationManager;
import com.wukong.hezhi.manager.StatsPageManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.LoadMoreListView;
import com.wukong.hezhi.ui.view.LoadMoreListView.ILoadListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/***
 * 
 * @ClassName: SearchLiveActivity
 * @Description: TODO(搜索品牌直播)
 * @author HuZhiyin
 * @date 2017-5-23 上午8:45:33
 * 
 */
public class SearchLiveActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.search_live);
	public String pageCode ="search_live";
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

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_search_live;
	}

	@Override
	protected void init() {
		initView();
		setListener();
	}

	public void initView() {
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		adapter = new LiveFragmentAdapter(cards, this);
		lv_scroll.setAdapter(adapter);
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
					ScreenUtil.showToast("没有更多数据了");
					lv_scroll.loadCompleted();
				}
			}
		});
		seacrh_et
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						// TODO Auto-generated method stub
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {// 点击了搜索按钮
							refreshData();
						}
						return false;
					}

				});
	}

	@OnClick(value = { R.id.seacrh_rl, R.id.header_left })
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

	/** 加载数据 */
	private void loadData() {
		if(TextUtils.isEmpty(seacrh_et.getText().toString().trim())){
			ScreenUtil.showToast("请输入要搜索的关键字");
			mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
			lv_scroll.loadCompleted();// 上拉加载隐藏
			return;
		}
		String name = seacrh_et.getText().toString().trim();
		
		String URL = HttpURL.URL1 + HttpURL.LIVE_BAND_NEW;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("page", page);
		map.put("name", name);
		if(UserInfoManager.getInstance().getUserId() != 0)
			map.put("memberId", UserInfoManager.getInstance().getUserId());
		map = StatsPageManager.getInstance().getPageSearchData(map);
		LogUtil.i(map.toString());
		
//		String url = HttpURL.URL1 + HttpURL.LIVE_BAND+"page="+page+"&name="+name+
//				"&memberId="+UserInfoManager.getInstance().getUserId();
		HttpManager.getInstance().post(URL, map,new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
				lv_scroll.loadCompleted();// 上拉加载隐藏
			}

			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				mSwipeLayout.setRefreshing(false);// 加载完成将下拉刷新隐藏
				lv_scroll.loadCompleted();// 加载完成将上拉加载隐藏
				ResponseJsonInfo<VuforiaInfos> info = JsonUtil.fromJson(
						arg0.result, VuforiaInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					VuforiaInfos vuforiaInfos = info.getBody();
					countPage = vuforiaInfos.getCountPage();// 获取总页数
					LogUtil.d("countPage" + countPage);
					ArrayList<VuforiaInfo> list = (ArrayList<VuforiaInfo>) vuforiaInfos
							.getDataList();
					if(page == 1){
						cards.clear();
					}
					cards.addAll(list);
					adapter.notifyDataSetChanged();
					if (cards != null && cards.size() == 0) {
						lv_scroll.setEmptyView(empty);
					}
				}
			}
		});
	}
}
