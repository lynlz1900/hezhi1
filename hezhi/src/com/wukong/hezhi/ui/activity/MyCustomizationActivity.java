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
import com.wukong.hezhi.adapter.MyCustomAdapter;
import com.wukong.hezhi.adapter.MyCustomizationAdapter;
import com.wukong.hezhi.bean.CommodityInfo;
import com.wukong.hezhi.bean.CommodityInfos;
import com.wukong.hezhi.bean.ResponseJsonInfo;
import com.wukong.hezhi.http.HttpCode;
import com.wukong.hezhi.http.HttpManager;
import com.wukong.hezhi.http.HttpURL;
import com.wukong.hezhi.manager.UserInfoManager;
import com.wukong.hezhi.ui.view.LoadMoreListView;
import com.wukong.hezhi.ui.view.LoadMoreListView.ILoadListener;
import com.wukong.hezhi.utils.ContextUtil;
import com.wukong.hezhi.utils.DateUtil;
import com.wukong.hezhi.utils.JsonUtil;
import com.wukong.hezhi.utils.LogUtil;
import com.wukong.hezhi.utils.ScreenUtil;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.View;
import android.view.ViewStub;

/***
 * 
 * @ClassName: MyCustomizationActivity
 * @Description: TODO(我的定制)
 * @author HuangFeiFei
 * @date 2017-8-4 下午16：12
 * 
 */
public class MyCustomizationActivity extends BaseActivity{

	public String pageName = ContextUtil.getString(R.string.my_customization);
	public String pageCode ="my_customization";
	
	@ViewInject(R.id.lv_scroll)
	private LoadMoreListView lv_scroll;

	@ViewInject(R.id.empty)
	private ViewStub empty;

	@ViewInject(R.id.swprf)
	private SwipeRefreshLayout mSwipeLayout;

	@ViewInject(R.id.layout_l)
	private View layout_l;
	
	private int page = 0;// 当前页
	@SuppressWarnings("unused")
	private boolean isOver = false;// 是否加载介绍
	/*** 总页数 **/
	private int countPage = 0;
	/** 是否编辑状态**/
	public static boolean isEdit = false;
	
	private MyCustomAdapter myCustomAdapter;
	@SuppressWarnings("unused")
	private MyCustomizationAdapter myCustomizationAdapter;
	private List<CommodityInfo> commodityInfos = new ArrayList<CommodityInfo>();
	private List<CommodityInfo> commodityInfosSel = new ArrayList<CommodityInfo>();
	private List<HashMap<String, Object>> data = new ArrayList<>();
	private HashMap<String, List<CommodityInfo>> hashMap = new HashMap<>();
	private List<String> keys = new ArrayList<>();
	
	@Override
	protected boolean isNotAddTitle() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected int layoutId() {
		// TODO Auto-generated method stub
		return R.layout.activity_my_customization;
	}

	@Override
	protected void init() {
		headView.setTitleStr(ContextUtil.getString(R.string.customiation));
		headView.setRightTitleText(ContextUtil
				.getString(R.string.text_edit));
		initView();
		setListener();
	}
	
	/**初始化控件**/
	private void initView() {
		isEdit = false;//默认不编辑
		commodityInfosSel.clear();
		commodityInfosSel.addAll(commodityInfos);
		setAdapter();
		setSwipeLayout();
	}
	
	/**初始化adapter**/
	private void setAdapter(){
		myCustomizationAdapter = new MyCustomizationAdapter(commodityInfosSel,mActivity,0);
		myCustomAdapter = new MyCustomAdapter(data, mActivity);
		lv_scroll.setAdapter(myCustomAdapter);
		lv_scroll.setEmptyView(empty);
	}
	
	/** 初始化加载**/
	public void setSwipeLayout() {
		mSwipeLayout.setColorSchemeColors(ContextUtil.getColor(R.color.base)); // 改变加载显示的颜色
		refresListView();
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
	
	@OnClick(value = { R.id.header_right })
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.header_right:
			OnClickEdit();
			break;
		}
	}
	
	/**
	 * 点击右上角编辑更新列表，
	 * isEdit:true 有删除按钮
	 * isEdit:false 无删除按钮
	 * **/
	private void OnClickEdit(){
		
		if(data == null || data.size() < 1){
			return;
		}
		
		isEdit = !isEdit;//编辑状态
		myCustomAdapter.notifyDataSetChanged();
		
		if(isEdit){
			layout_l.setVisibility(View.VISIBLE);
			headView.setRightTitleText(ContextUtil
					.getString(R.string.text_complete));
		}
		else{
			layout_l.setVisibility(View.GONE);
			headView.setRightTitleText(ContextUtil
					.getString(R.string.text_edit));
		}
	}
	
	/**加载首页数据**/
	private void refresListView() {
		mSwipeLayout.post(new Runnable() {
			@Override
			public void run() {
				showProgressDialog();
				page = 1;// 刷新将page置为1
				loadData();
			}
		});// 进入页面，自动加载
	}
	
	/** 加载数据 */
	private void loadData() {
		String url = HttpURL.URL1 + HttpURL.MY_CUST;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userId", UserInfoManager.getInstance().getUserId());
		map.put("page", page);
		LogUtil.i(url);
		LogUtil.i(map.toString());
		HttpManager.getInstance().post(url, map, new RequestCallBack<String>() {
			@Override
			public void onFailure(HttpException arg0, String arg1) {
				// TODO Auto-generated method stub
				mSwipeLayout.setRefreshing(false);// 将下拉刷新隐藏
				lv_scroll.loadCompleted();// 上拉加载隐藏
				dismissProgressDialog();
				LogUtil.i(arg1);
				ScreenUtil.showToast(ContextUtil.getString(R.string.server_error));
			}

			@SuppressWarnings({"unchecked" })
			@Override
			public void onSuccess(ResponseInfo<String> arg0) {
				// TODO Auto-generated method stub
				LogUtil.i(arg0.result);
				commodityInfos.clear();
				commodityInfos.addAll(commodityInfosSel);//先更新orderInfos，否则会导致数据不是最新的
				dismissProgressDialog();
				mSwipeLayout.setRefreshing(false);// 加载完成将下拉刷新隐藏
				lv_scroll.loadCompleted();// 加载完成将上拉加载隐藏
				ResponseJsonInfo<CommodityInfos> info = JsonUtil.fromJson(
						arg0.result, CommodityInfos.class);
				if (info != null && info.getHttpCode().equals(HttpCode.SUCESS)) {
					CommodityInfos infos = new CommodityInfos();
					try {
						infos = (CommodityInfos)info.getBody();
						List<CommodityInfo> list = infos.getDataList();
						countPage = infos.getCountPage();
						if (page == 1) {
							commodityInfos.clear();
							hashMap.clear();
							keys.clear();
						}
						if (list == null || list.size() == 0) {
							isOver = true;
						} else {
							isOver = false;
							commodityInfos.addAll(list);
						}
						
						commodityInfosSel.clear();
						commodityInfosSel.addAll(commodityInfos);
//						myCustomizationAdapter.notifyDataSetChanged();
						
						data.clear();
						data.addAll(setData(list));
						myCustomAdapter.notifyDataSetChanged();
					} catch (Exception e) {
					}
				}else if (info != null && info.getHttpCode().equals(HttpCode.FAIL)){
					if(info.getPromptMessage() != null || info.getPromptMessage().length() >0)
						ScreenUtil.showToast(info.getPromptMessage());
				}else{
					ScreenUtil.showToast(ContextUtil.getString(R.string.data_get_fail));
				}
			}
		});
	}
	
	/** 设置数据 */
	private List<HashMap<String, Object>> setData(List<CommodityInfo> commodityInfos){
		
		for(CommodityInfo commodityInfo : commodityInfos){
			String time = DateUtil.getYMD(commodityInfo.getCreateDateLong());
			if(hashMap.containsKey(time)){
				((List<CommodityInfo>) hashMap.get(time)).add(commodityInfo);
			}else{
				List<CommodityInfo> infosTemp = new ArrayList<>();
				infosTemp.add(commodityInfo);
				hashMap.put(time, infosTemp);
				keys.add(time);
			}
		}
		
		List<HashMap<String, Object>> list = getData(hashMap, keys);
		return list;
	}
	
	/** 获取数据*/
	private List<HashMap<String, Object>> getData(HashMap<String, List<CommodityInfo>> hashMap,List<String> keys){
		List<HashMap<String, Object>> list = new ArrayList<>();
		
		for (String key : keys)  {
			HashMap<String, Object> map = new HashMap<>();
			map.put("time",key);
			map.put("data",hashMap.get(key));
			list.add(map);
		}
		
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void updateState(Class notifyTo, Object notifyFrom, Object msg) {
		if(notifyTo.equals(getClass())){
			if(notifyFrom.equals(MyCustomizationAdapter.class)){
				try {
					int position = (int)msg;
					data.remove(position);
					myCustomAdapter.notifyDataSetChanged();
					isEdit = false;
					layout_l.setVisibility(View.GONE);
					headView.setRightTitleText(ContextUtil
							.getString(R.string.text_edit));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
	}
}
