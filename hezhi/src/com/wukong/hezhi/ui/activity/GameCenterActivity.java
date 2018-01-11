package com.wukong.hezhi.ui.activity;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.ViewStub;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.wukong.hezhi.R;
import com.wukong.hezhi.adapter.GameCenterActivityAdapter;
import com.wukong.hezhi.bean.GameInfo;
import com.wukong.hezhi.bean.GameInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.ui.view.LoadMoreListView;
import com.wukong.hezhi.ui.view.LoadMoreListView.ILoadListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ReadStreamUtil;
import com.wukong.hezhi.utils.ScreenUtil;

/**
 * 
 * @ClassName: GameCenterActivity
 * @Description: TODO(益趣互动)
 * @author HuZhiyin
 * @date 2017-5-11 下午3:20:35
 * 
 */
public class GameCenterActivity extends BaseActivity {
	public String pageName = ContextUtil.getString(R.string.game_center);
	public String pageCode ="game_center";
	
	@ViewInject(R.id.lv_scroll)
	private LoadMoreListView lv_scroll;
	@ViewInject(R.id.empty)
	private ViewStub empty;
	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;

	private GameCenterActivityAdapter adapter;
	private List<GameInfo> cards = new ArrayList<GameInfo>();
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
		return R.layout.activity_game_center;
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		headView.setTitleStr(ContextUtil.getString(R.string.game_center));

		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		mSwipeLayout.post(new Runnable() {
			@Override
			public void run() {
				mSwipeLayout.setRefreshing(true);
				page = 1;// 刷新将page置为1
				getDataFromAsset();
			}
		});// 进入页面，自动加载
		adapter = new GameCenterActivityAdapter(cards, this);
		lv_scroll.setAdapter(adapter);
		lv_scroll.setEmptyView(empty);
		setListener();
	}

	/** 设置监听 */
	private void setListener() {

		mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				page = 1;// 刷新将page置为1
				getDataFromAsset();
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
					getDataFromAsset();
				} else {
					ScreenUtil.showToast("没有更多数据了");
					lv_scroll.loadCompleted();
				}
			}
		});
	}

	/** 加载数据 */
	// private void loadData() {
	// String url = HttpURL.URL1 + HttpURL.MY_DYNAMIC + "memberId="
	// + UserInfoManager.getInstance().getUserId() + "&page=" + page;
	// HttpHelper httpHelper = HttpHelper.getInstances();
	// httpHelper.get(url, new RequestCallBack<String>() {
	// @Override
	// public void onFailure(HttpException arg0, String arg1) {
	// // TODO Auto-generated method stub
	// mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
	// lv_scroll.loadCompleted();// 上拉加载隐藏
	// }
	//
	// @Override
	// public void onSuccess(ResponseInfo<String> arg0) {
	// // TODO Auto-generated method stub
	// mSwipeLayout.setRefreshing(false);// 加载完成将下拉刷新隐藏
	// lv_scroll.loadCompleted();// 加载完成将上拉加载隐藏
	// ResponseJsonInfo<GameInfos> info = JsonUtil.fromJson(
	// arg0.result, GameInfos.class);
	// if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
	// GameInfos responseJsonInfo2s = info.getBody();
	// countPage = responseJsonInfo2s.getCountPage();// 获取总页数
	// LogUtil.d("countPage" + countPage);
	// ArrayList<GameInfo> list = (ArrayList<GameInfo>) responseJsonInfo2s
	// .getDataList();
	// for (GameInfo vuforiaInfo : list) {
	// cards.add(vuforiaInfo);
	// }
	// adapter.notifyDataSetChanged();
	// } else {
	// ScreenUtil.showToast("数据加载失败");
	// }
	// }
	// });
	// }

	private void getDataFromAsset() {
		mSwipeLayout.setRefreshing(false);// 加载完成将下拉刷新隐藏
		lv_scroll.loadCompleted();// 加载完成将上拉加载隐藏
		String jsonStr = ReadStreamUtil.getJson("TXT/yuquhudongjson.txt");
		ResponseJsonInfo<GameInfos> info = JsonUtil.fromJson(jsonStr,
				GameInfos.class);
		if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
			GameInfos responseJsonInfo2s = info.getBody();
			countPage = responseJsonInfo2s.getCountPage();// 获取总页数
			LogUtil.d("countPage" + countPage);
			ArrayList<GameInfo> list = (ArrayList<GameInfo>) responseJsonInfo2s
					.getDataList();
			if (page == 1) {
				cards.clear();// 清空数据
			}
			cards.addAll(list);
			adapter.notifyDataSetChanged();
		} else {
			ScreenUtil.showToast("数据加载失败");
		}
		;

	}

}
