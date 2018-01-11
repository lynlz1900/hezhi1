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
import com.wukong.hezhi.adapter.MyAttentionAdapter;
import com.wukong.hezhi.bean.MyGuanZhuInfo;
import com.wukong.hezhi.bean.MyGuanZhuInfos;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.ViewStub;

/***
 * 
 * @ClassName: MyAttentionActivity
 * @Description: TODO(我的---》关注)
 * @author HuangFeiFei
 * @date 2017-10-23 14：50
 * 
 */
public class MyAttentionActivity extends BaseActivity{

	public String pageName = ContextUtil.getString(R.string.my_attention);
	public String pageCode ="my_attention";
	
	@ViewInject(R.id.lv_scroll)
	private LoadMoreListView lv_scroll;

	@ViewInject(R.id.empty)
	private ViewStub empty;

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;

	@ViewInject(R.id.layout_l)
	private View layout_l;
	
	private int page = 1;// 当前页
	private int countPage = 1;// 总页数
	
	private MyAttentionAdapter myAttentionAdapter;
	private List<MyGuanZhuInfo> myGuanZhuInfos = new ArrayList<MyGuanZhuInfo>();
	private List<MyGuanZhuInfo> myGuanZhuInfosSel = new ArrayList<MyGuanZhuInfo>();
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_my_attention;
	}

	@Override
	protected void init() {
		headView.setTitleStr(ContextUtil.getString(R.string.my_attention));
		initView();
		setListener();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refresListView();
	}
	
	/**初始化控件**/
	private void initView() {
		myGuanZhuInfosSel.clear();
		myGuanZhuInfosSel.addAll(myGuanZhuInfos);
		setAdapter();
		setSwipeLayout();
	}
	
	/**初始化adapter**/
	private void setAdapter(){
		myAttentionAdapter = new MyAttentionAdapter
				(myGuanZhuInfosSel,mActivity);
		lv_scroll.setAdapter(myAttentionAdapter);
		lv_scroll.setEmptyView(empty);
	}
	
	/** 初始化加载**/
	public void setSwipeLayout() {
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
	}
	
	/** 设置监听 */
	private void setListener() {
		//下拉刷新
		mSwipeLayout.setOnRefreshListener(new OnRefreshListener() {
			public void onRefresh() {
				page = 1;// 刷新将page置为1
				loadData();
			}
		});

		//上拉加载更多
		lv_scroll.setLoadListener(new ILoadListener() {
			@Override
			public void onLoad() {
				// TODO Auto-generated method stub
				page++;
				if (page <= countPage) {// 如果加载结束
					loadData();
				} else {
					lv_scroll.loadCompleted();
					ScreenUtil.showToast(ContextUtil.getString(R.string.no_more_data));
				}
			}
		});
	}
	
	@OnClick(value = { })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		}
	}
	
	/**加载首页数据**/
	private void refresListView() {
		mSwipeLayout.post(new Runnable() {
			@Override
			public void run() {
				page = 1;// 刷新将page置为1
				loadData();
			}
		});// 进入页面，自动加载
	}
	
	/** 加载数据 */
	private void loadData() {
		long userId;
		if (UserInfoManager.getInstance().isLogin()) {
			userId = UserInfoManager.getInstance().getUserId();
		} else {
			userId = -1;
		}
		String url = HttpURL.URL1 + HttpURL.MY_FOUCUS;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("memberId", userId);
		map.put("page", page);
		LogUtil.i(url);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
				lv_scroll.loadCompleted();// 上拉加载隐藏
				dismissProgressDialog();
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}

			@SuppressWarnings({"unchecked" })
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				mSwipeLayout.setRefreshing(false);// 加载完成将下拉刷新隐藏
				lv_scroll.loadCompleted();// 加载完成将上拉加载隐藏
				LogUtil.i(arg0.result);
				ResponseJsonInfo<MyGuanZhuInfos> info = JsonUtil.fromJson(
						arg0.result, MyGuanZhuInfos.class);
				ArrayList<MyGuanZhuInfo> list = new ArrayList<MyGuanZhuInfo>();
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					
					try {
						MyGuanZhuInfos infos = info.getBody();
						countPage = infos.getCountPage();// 获取总页数
						list = (ArrayList<MyGuanZhuInfo>) infos
								.getDataList();
					} catch (Exception e) {
					}
					
					if (page == 1) {
						myGuanZhuInfos.clear();
					}
					if (list == null || list.size() == 0) {
					} else {
						myGuanZhuInfos.addAll(list);
					}
					
					myGuanZhuInfosSel.clear();
					myGuanZhuInfosSel.addAll(myGuanZhuInfos);
					myAttentionAdapter.notifyDataSetChanged();
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
				}else{
					ScreenUtil.showToast(ContextUtil.getString(R.string.data_get_fail));
				}
			}
		});
	}
}
