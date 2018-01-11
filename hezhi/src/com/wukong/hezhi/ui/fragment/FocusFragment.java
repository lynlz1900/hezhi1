package com.wukong.hezhi.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.ArticleAdapter;
import com.wukong.hezhi.bean.ArticleInfo;
import com.wukong.hezhi.bean.ArticleInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.activity.BaseActivity;
import com.wukong.hezhi.ui.segment.ArticleEditCommentFragment;
import com.wukong.hezhi.ui.view.LoadMoreListView;
import com.wukong.hezhi.ui.view.LoadMoreListView.ILoadListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @ClassName: FocusFragment
 * @Description: TODO(盒知看点)
 * @author HuZhiyin
 * @date 2017-3-14 下午1:41:31
 * 
 */
public class FocusFragment extends BaseFragment {
	 public String pageName = ContextUtil.getString(R.string.article_list);
	 public String pageCode ="article_list";
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
	private String tpye;// 文章type

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_focus, container,
				false);// 关联布局文件
		ViewUtils.inject(this, rootView); // 注入view和事件
		Bundle mBundle = getArguments();
		tpye = mBundle.getString("tpye");
		init();
		return rootView;
	}

	public void init() {
		initView();
		setListener();
	}

	private void initView() {
		refreshListView();
		adapter = new ArticleAdapter(myCollecionInfos,
				(BaseActivity) getActivity());
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
					ScreenUtil.showToast("没有更多数据了");
					lv_scroll.loadCompleted();
				}
			}
		});
	}

	public void refresh(String id, String comment) {// 刷新评论
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

	public void refresh(String id) {// 刷新是否已阅读
		List<ArticleInfo> cardsCache = new ArrayList<>();
		for (ArticleInfo articleInfo : myCollecionInfos) {
			if (id.equals(articleInfo.getId())) {
				articleInfo.setHavaToSee(true);// 设置为已阅读
			}
			cardsCache.add(articleInfo);
		}
		myCollecionInfos.clear();
		myCollecionInfos.addAll(cardsCache);
		adapter.notifyDataSetChanged();
	}

	public void refreshListView() {
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
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

		String url = HttpURL.URL1 + HttpURL.ARTICLE;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("articleMenuId", tpye);
		map.put("page", page);
		map.put("userId", UserInfoManager.getInstance().getUserIdOrDeviceId());
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

	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		// TODO Auto-generated method stub
		if (notifyTo.equals(getClass())) {
			if (notifyFrom.equals(ArticleEditCommentFragment.class)) {
				try {
					String str = (String) msg;
					String strm[] = str.split(",");
					String id = strm[0];
					String commentCount = strm[1];
					refresh(id, commentCount);// 刷新评论
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
			if (notifyFrom.equals(ArticleAdapter.class)) {
				String id = (String) msg;
				refresh(id);// 刷新阅读状态
			}

		}
	}

}
