package com.wukong.hezhi.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.ViewStub;

import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.CashDetailActivityAdapter;
import com.wukong.hezhi.bean.CashInfo;
import com.wukong.hezhi.bean.CashInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.LoadMoreListView;
import com.wukong.hezhi.ui.view.LoadMoreListView.ILoadListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 
 * @ClassName: MyDynamicActivity
 * @Description: TODO(我的动态)
 * @author HuZhiyin
 * @date 2017-3-31 下午2:51:30
 * 
 */
public class CashDetailActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.cash_detail);
	public String pageCode ="cash_detail";
	
	@ViewInject(R.id.lv_scroll)
	private LoadMoreListView lv_scroll;
	@ViewInject(R.id.empty)
	private ViewStub empty;
	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;

	private CashDetailActivityAdapter adapter;
	private List<CashInfo> cards = new ArrayList<CashInfo>();
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
		return R.layout.activity_cash_detail;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.cash_detail));

		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		mSwipeLayout.post(new Runnable() {
			@Override
			public void run() {
				mSwipeLayout.setRefreshing(true);
				page = 1;// 刷新将page置为1
				loadData();
			}
		});// 进入页面，自动加载
		adapter = new CashDetailActivityAdapter(cards, this);
		lv_scroll.setAdapter(adapter);
		lv_scroll.setEmptyView(empty);
		setListener();
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

	/** 加载数据 */
	private void loadData() {
		String url = HttpURL.URL1+HttpURL.MY_DYNAMIC + "memberId="+UserInfoManager.getInstance().getUserId()+"&page="+page;
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
				mSwipeLayout.setRefreshing(false);// 加载完成将下拉刷新隐藏
				lv_scroll.loadCompleted();// 加载完成将上拉加载隐藏
				ResponseJsonInfo<CashInfos> info = JsonUtil.fromJson(
						arg0.result, CashInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					CashInfos responseJsonInfo2s = info.getBody();
					countPage = responseJsonInfo2s.getCountPage();// 获取总页数
					LogUtil.d("countPage" + countPage);
					ArrayList<CashInfo> list = (ArrayList<CashInfo>) responseJsonInfo2s
							.getDataList();
					if(page == 1){
						cards.clear();// 清空数据
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
