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
import com.wukong.hezhi.adapter.ArticleAdapter;
import com.wukong.hezhi.bean.ArticleInfo;
import com.wukong.hezhi.bean.ArticleInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.constants.Constant;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.KeyboardManager;
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
 * @ClassName: SearchArticleActivity
 * @Description: TODO(搜素文章)
 * @author HuZhiyin
 * @date 2017-6-16 上午8:01:52
 * 
 */
public class SearchArticleActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.search_articles);
	public String pageCode ="search_articles";
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

	private int page = 0;// 当前页
	private boolean isOver = false;// 是否加载介绍
	private List<ArticleInfo> myCollecionInfos = new ArrayList<>();
	private ArticleAdapter adapter;
	private String fromActivity;

	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_search_atricle;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		// headView.setTitleStr(ContextUtil.getString(R.string.search_article));
		initView();
		setListener();
	}

	private void initView() {
		fromActivity = (String) getIntent().getSerializableExtra(
				Constant.Extra.FROM_ACTIVITY);
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		adapter = new ArticleAdapter(myCollecionInfos, mActivity);
		lv_scroll.setAdapter(adapter);
		// lv_scroll.setEmptyView(empty);
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

	private String searchLabel;

	/** 加载数据 */
	private void loadData() {
		if(TextUtils.isEmpty(seacrh_et.getText().toString().trim())){
			ScreenUtil.showToast("请输入要搜索的关键字");
			mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
			lv_scroll.loadCompleted();// 上拉加载隐藏
			return;
		}
		
		searchLabel = seacrh_et.getText().toString().trim();
		String url = null;
		Map<String, Object> map = null;
		if ("MyCollectionActivity".equals(fromActivity)) {// 搜索收藏文章
			url = HttpURL.URL1 + HttpURL.QUERYMYCOLLECTIONBYKEYWORDS;
			map = new HashMap<String, Object>();
			if(UserInfoManager.getInstance().getUserId() != 0)
				map.put("userId", UserInfoManager.getInstance().getUserId());
			map.put("label", searchLabel);
			map.put("page", page);
		} else {// 搜索所有文章
			url = HttpURL.URL1 + HttpURL.SEARCH_ARTICLE;
			map = new HashMap<String, Object>();
			if(UserInfoManager.getInstance().getUserId() != 0)
				map.put("userId", UserInfoManager.getInstance().getUserId());
			map.put("label", searchLabel);
			map.put("page", page);
			map = StatsPageManager.getInstance().getPageSearchData(map);
		}
		LogUtil.i(map.toString());
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
					if(page == 1){
						myCollecionInfos.clear();
					}
					if (list.size() == 0) {
						isOver = true;
					} else {
						isOver = false;
						myCollecionInfos.addAll(list);
					}
					adapter.notifyDataSetChanged();
					if (myCollecionInfos != null
							&& myCollecionInfos.size() == 0) {
						lv_scroll.setEmptyView(empty);
					}
				}
			}
		});
	}

}
