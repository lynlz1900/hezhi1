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
import com.wukong.hezhi.adapter.ArticleAdapter;
import com.wukong.hezhi.bean.ArticleInfo;
import com.wukong.hezhi.bean.ArticleInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.segment.ArticleEditCommentFragment;
import com.wukong.hezhi.ui.view.LoadMoreListView;
import com.wukong.hezhi.ui.view.LoadMoreListView.ILoadListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/***
 * 
 * @ClassName: MyCollectionActivity
 * @Description: TODO(我的收藏)
 * @author HuZhiyin
 * @date 2017-6-16 上午8:01:52
 * 
 */
public class MyCollectionActivity extends BaseActivity  {
	public String pageName = ContextUtil.getString(R.string.my_collection);
	public String pageCode ="my_collection";
	@ViewInject(R.id.lv_scroll)
	private LoadMoreListView lv_scroll;

	@ViewInject(R.id.empty)
	private ViewStub empty;

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;

	private int page = 0;// 当前页
	private boolean isOver = false;// 是否加载介绍
	private List<ArticleInfo> myCollecionInfos = new ArrayList<>();
	private ArticleAdapter adapter;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_my_collection;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.collection));
		headView.setRightTitleText(ContextUtil
				.getString(R.string.search_article));
		headView.setRihgtTitleText("", R.drawable.icon_search);
		initView();
		setListener();
	}

	private void initView() {
		setSwipeLayout();
		adapter = new ArticleAdapter(myCollecionInfos, mActivity);
		lv_scroll.setAdapter(adapter);
		lv_scroll.setEmptyView(empty);
	}

	@OnClick(value = { R.id.header_right })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_right:
			toActivity(SearchArticleActivity.class,
					Constant.Extra.FROM_ACTIVITY, TAG);
			break;
		}
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
					ScreenUtil.showToast("没有更多数据了");
					lv_scroll.loadCompleted();
				}
			}
		});
	}

	public void setSwipeLayout() {
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		refresListView();
	}

	private void refresListView() {
		mSwipeLayout.post(new Runnable() {
			@Override
			public void run() {
				// mSwipeLayout.setRefreshing(true);
				page = 1;// 刷新将page置为1
				loadData();
			}
		});// 进入页面，自动加载
	}

	/** 加载数据 */
	private void loadData() {

		String url = HttpURL.URL1 + HttpURL.MY_COLLECT;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", UserInfoManager.getInstance().getUserId());
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
				ResponseJsonInfo<ArticleInfos> info = JsonUtil.fromJson(
						arg0.result, ArticleInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					ArticleInfos infos = info.getBody();
					ArrayList<ArticleInfo> list = (ArrayList<ArticleInfo>) infos
							.getDataList();
					if (page == 1) {
						myCollecionInfos.clear();
					}
					if (list.size() == 0) {
						isOver = true;
					} else {
						isOver = false;
						myCollecionInfos.addAll(list);
					}
					adapter.notifyDataSetChanged();
				}
			}
		});
	}

	public void refresh(String id, String comment) {
		List<ArticleInfo> cardsCache = new ArrayList<>();
		for (ArticleInfo articleInfo : myCollecionInfos) {
			if (id.equals(articleInfo.getId())) {// 设置为已领取
				articleInfo.setCommentCount(comment);
			}
			cardsCache.add(articleInfo);
		}
		myCollecionInfos.clear();
		myCollecionInfos.addAll(cardsCache);
		adapter.notifyDataSetChanged();
	}

	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		// TODO Auto-generated method stub
		
		if (notifyTo.equals(getClass())) {
			if (notifyFrom.equals(ArticleEditCommentFragment.class)) {
				if (msg instanceof String) {
					try {
						String str = (String) msg;
						String strm[] = str.split(",");
						String id = strm[0];
						String commentCount = strm[1];
						refresh(id, commentCount);
					} catch (Exception e) {
						// TODO: handle exception
					}
				} else if (msg instanceof Boolean) {
					if ((Boolean) msg) {
						refresListView();
					}
				}
			}

		}
	}
}
