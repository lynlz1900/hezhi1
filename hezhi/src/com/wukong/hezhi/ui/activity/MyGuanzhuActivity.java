package com.wukong.hezhi.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.ViewStub;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.MyGuanZhuActivityAdapter;
import com.wukong.hezhi.bean.MyGuanZhuInfo;
import com.wukong.hezhi.bean.MyGuanZhuInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.KeyboardManager;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.LoadMoreListView;
import com.wukong.hezhi.ui.view.LoadMoreListView.ILoadListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/***
 * 
 * @ClassName: RecommendActivity
 * @Description: TODO(关注商家页面)
 * @author HuZhiyin
 * @date 2017-5-23 上午8:45:33
 * 
 */
public class MyGuanzhuActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.my_attetion);
	public String pageCode ="my_attetion";
	@ViewInject(R.id.lv_scroll)
	private LoadMoreListView lv_scroll;

	@ViewInject(R.id.empty)
	private ViewStub empty;

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;

	private MyGuanZhuActivityAdapter adapter;
	private List<MyGuanZhuInfo> cards = new ArrayList<MyGuanZhuInfo>();
	private int page = 0;// 当前页
	private int countPage;// 总页数

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_my_guanzhu;
	}

	@Override
	protected void init() {
		headView.setTitleStr(ContextUtil.getString(R.string.my_attetion));
		initView();
		setListener();
	}

	public void initView() {
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		mSwipeLayout.post(new Runnable() {
			@Override
			public void run() {
				// mSwipeLayout.setRefreshing(true);
				page = 1;// 刷新将page置为1
				loadData();
			}
		});// 进入页面，自动加载
		adapter = new MyGuanZhuActivityAdapter(cards, this);
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
					ScreenUtil.showToast("没有更多数据了");
					lv_scroll.loadCompleted();
				}
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
			KeyboardManager.getInstance().hideKeyboard(this);// 隐藏键盘
			mSwipeLayout.post(new Runnable() {
				@Override
				public void run() {
					mSwipeLayout.setRefreshing(true);
					page = 1;// 刷新将page置为1
					loadData();
				}
			});// 进入页面，自动加载
			break;
		case R.id.header_left:
			finish();
			break;
		}
	}

	/** 加载数据 */
	private void loadData() {
		String url = HttpURL.URL1 + HttpURL.MY_FOUCUS;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("memberId", UserInfoManager.getInstance().getUserIdOrDeviceId());
		map.put("page", page);
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
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
				ResponseJsonInfo<MyGuanZhuInfos> info = JsonUtil.fromJson(
						arg0.result, MyGuanZhuInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					MyGuanZhuInfos myGuanZhuInfos = info.getBody();
					countPage = myGuanZhuInfos.getCountPage();// 获取总页数
					LogUtil.d("countPage" + countPage);
					ArrayList<MyGuanZhuInfo> list = (ArrayList<MyGuanZhuInfo>) myGuanZhuInfos
							.getDataList();
					if(page == 1){
						cards.clear();
					}
					cards.addAll(list);
					adapter.notifyDataSetChanged();
				} else {
					ScreenUtil.showToast("数据加载失败");
				}
			}
		});
	}
}
